import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.appcimuc.model.PreferenciasFiltrosProyectos
import com.example.appcimuc.repository.AppRepository
import com.example.appcimuc.repository.PreferenciasFiltrosRepository
import com.example.appcimuc.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

// Sin JUnit/dependencias nuevas: DataStore real sobre archivos temporales, y política pura de restauración.
// El VM usa un scope inyectado controlado; no prueba Compose/viewModelScope Android ni celular.
private var comprobaciones = 0
private fun verificar(valor: Boolean, mensaje: String) {
    check(valor) { mensaje }
    comprobaciones++
}

private class Almacen(val archivo: File) {
    private val job = SupervisorJob()
    val dataStore = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.IO + job), produceFile = { archivo })
    val repository = PreferenciasFiltrosRepository(dataStore)
    suspend fun cerrar() { job.cancelAndJoin() }
}

private class StoreControlado(valorInicial: Preferences = emptyPreferences()) : DataStore<Preferences> {
    val contenido = MutableStateFlow(valorInicial)
    var lecturaBloqueada: CompletableDeferred<Unit>? = null
    var fallarLectura = false
    var fallarEscritura = false
    var escrituras = 0
    override val data: Flow<Preferences> = flow {
        lecturaBloqueada?.await()
        if (fallarLectura) throw IOException("Lectura simulada")
        emit(contenido.value)
    }
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        if (fallarEscritura) throw IOException("Escritura simulada")
        escrituras++
        contenido.value = transform(contenido.value)
        return contenido.value
    }
}

private suspend fun comprobarViewModel() {
    val recordar = booleanPreferencesKey("recordar_filtros_proyectos")
    val categoria = stringPreferencesKey("categoria_proyecto")
    val anio = intPreferencesKey("anio_proyecto")
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Unconfined + job)
    try {
        val gate = CompletableDeferred<Unit>()
        val store = StoreControlado(preferencesOf(recordar to true, categoria to "cat-materiales", anio to 2026))
        store.lecturaBloqueada = gate
        val vm = MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(store), preferenciasScope = scope)
        verificar(vm.preferenciasFiltrosUiState.value.cargando && store.escrituras == 0, "VM no escribe antes de leer")
        vm.cambiarRecordarFiltros(false)
        verificar(store.escrituras == 0, "cambio de preferencia bloqueado durante lectura inicial")
        gate.complete(Unit)
        verificar(vm.explorarUiState.value.let { it.categoriaId == "cat-materiales" && it.anio == 2026 }, "VM restaura tras leer")
        verificar(store.escrituras == 0, "carga inicial no escribe defaults")
        vm.buscar("texto libre ficticio")
        verificar(store.escrituras == 0, "buscar no dispara escritura")
        vm.seleccionarCategoria("cat-procesos")
        verificar(store.contenido.value[categoria] == "cat-procesos", "VM persiste categoría explícita")
        vm.cambiarRecordarFiltros(false)
        verificar(vm.explorarUiState.value.categoriaId == "cat-procesos" && vm.explorarUiState.value.anio == 2026,
            "desactivar conserva los filtros de sesión")
        verificar(store.contenido.value[recordar] == false && store.contenido.value[categoria] == null &&
            store.contenido.value[anio] == null, "VM desactiva y elimina filtros de disco")
        val reinicio = MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(store), preferenciasScope = scope)
        verificar(!reinicio.preferenciasFiltrosUiState.value.recordar && reinicio.explorarUiState.value.let {
            it.categoriaId == null && it.anio == null && it.textoProyectos.isEmpty()
        }, "nuevo VM desactivado: Todos y búsqueda vacía")
        vm.cambiarRecordarFiltros(true)
        val activo = MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(store), preferenciasScope = scope)
        verificar(activo.preferenciasFiltrosUiState.value.recordar && activo.explorarUiState.value.let {
            it.categoriaId == "cat-procesos" && it.anio == 2026 && it.textoProyectos.isEmpty()
        }, "nuevo VM activo recupera filtros, no la búsqueda")
        val antes = store.escrituras
        activo.seleccionarTipo(TipoContenido.CAPACIDADES)
        activo.buscar("ensayos")
        activo.limpiarExplorar()
        activo.seleccionarTipo(TipoContenido.INTEGRANTES)
        activo.buscar("Ana")
        activo.limpiarExplorar()
        verificar(store.escrituras == antes, "búsqueda/limpieza de otros tipos no escribe")

        val gateTardio = CompletableDeferred<Unit>()
        val tardio = StoreControlado(preferencesOf(recordar to true, categoria to "cat-materiales", anio to 2026))
        tardio.lecturaBloqueada = gateTardio
        val vmTardio = MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(tardio), preferenciasScope = scope)
        vmTardio.seleccionarCategoria("cat-procesos")
        verificar(tardio.escrituras == 0, "edición de sesión durante lectura no escribe defaults")
        gateTardio.complete(Unit)
        verificar(vmTardio.explorarUiState.value.categoriaId == "cat-procesos" && tardio.contenido.value[categoria] == "cat-procesos",
            "edición anterior a la carga tiene prioridad y se guarda después de leer")

        val fallido = StoreControlado()
        fallido.fallarLectura = true
        val vmFallo = MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(fallido), preferenciasScope = scope)
        verificar(vmFallo.preferenciasFiltrosUiState.value.let { !it.disponible && it.error != null && !it.cargando },
            "VM muestra error de lectura sin declarar carga correcta")
        verificar(fallido.escrituras == 0, "error de lectura no cambia el archivo")
        vmFallo.seleccionarCategoria("cat-procesos")
        verificar(vmFallo.explorarUiState.value.categoriaId == "cat-procesos", "error permite filtros de sesión")
        fallido.fallarLectura = false
        vmFallo.reintentarPreferencias()
        verificar(vmFallo.preferenciasFiltrosUiState.value.disponible && vmFallo.preferenciasFiltrosUiState.value.error == null,
            "reintentar lectura recupera estado")
        fallido.fallarEscritura = true
        vmFallo.cambiarRecordarFiltros(true)
        verificar(vmFallo.preferenciasFiltrosUiState.value.let { !it.recordar && !it.guardando && it.error != null },
            "fallo de activación mantiene valor confirmado y muestra error")
        verificar(fallido.escrituras == 0, "fallo de escritura no se considera éxito")
        vmFallo.seleccionarAnio(2025)
        fallido.fallarEscritura = false
        vmFallo.reintentarPreferencias()
        verificar(vmFallo.preferenciasFiltrosUiState.value.recordar && fallido.contenido.value[categoria] == "cat-procesos" &&
            fallido.contenido.value[anio] == 2025, "reintentar guardado usa filtros actuales")
        fallido.fallarEscritura = true
        vmFallo.cambiarRecordarFiltros(false)
        verificar(vmFallo.preferenciasFiltrosUiState.value.recordar && vmFallo.preferenciasFiltrosUiState.value.error != null &&
            fallido.contenido.value[recordar] == true, "fallo de desactivación no afirma que se borró")
        fallido.fallarEscritura = false
        vmFallo.reintentarPreferencias()
        verificar(!vmFallo.preferenciasFiltrosUiState.value.recordar && fallido.contenido.value[categoria] == null &&
            vmFallo.explorarUiState.value.categoriaId == "cat-procesos", "reintentar desactivación borra disco, no sesión")
    } finally { job.cancelAndJoin() }
}

fun main(args: Array<String>) = runBlocking {
    val carpeta = Files.createTempDirectory(Paths.get(args.single()), "preferencias-checks-").toFile()
    val archivo = File(carpeta, "prueba.preferences_pb")
    val recordar = booleanPreferencesKey("recordar_filtros_proyectos")
    val categoria = stringPreferencesKey("categoria_proyecto")
    val anio = intPreferencesKey("anio_proyecto")
    val esperado = PreferenciasFiltrosProyectos(true, "cat-materiales", 2026)

    var almacen = Almacen(archivo)
    try {
        verificar(almacen.repository.leer() == PreferenciasFiltrosProyectos(), "desactivado por defecto")
        almacen.repository.guardar(esperado)
        verificar(almacen.repository.leer() == esperado, "activación con categoría/año")
        val claves = almacen.dataStore.data.first().asMap().keys.map { it.name }.toSet()
        verificar(claves == setOf(recordar.name, categoria.name, anio.name), "solo tres claves, sin búsqueda ni datos personales")
    } finally { almacen.cerrar() }

    almacen = Almacen(archivo) // Nunca dos instancias simultáneas por archivo.
    try {
        val antes = archivo.readBytes()
        verificar(almacen.repository.leer() == esperado, "reabrir conserva preferencia y filtros")
        verificar(archivo.readBytes().contentEquals(antes), "leer no sobrescribe con defaults")
        almacen.repository.guardar(PreferenciasFiltrosProyectos(false, "cat-procesos", 2025))
        val mapa = almacen.dataStore.data.first()
        verificar(mapa[recordar] == false, "persiste desactivación")
        verificar(mapa[categoria] == null && mapa[anio] == null, "desactivar elimina claves de filtros")
        verificar(mapa.asMap().size == 1, "no persiste otros campos")
    } finally { almacen.cerrar() }

    almacen = Almacen(archivo)
    try {
        verificar(almacen.repository.leer() == PreferenciasFiltrosProyectos(), "reiniciar desactivado vuelve a Todos")
        almacen.repository.guardar(PreferenciasFiltrosProyectos(true))
        verificar(almacen.repository.leer() == PreferenciasFiltrosProyectos(true), "Todos se representa sin categoría/año")
        almacen.repository.guardar(PreferenciasFiltrosProyectos(true, "eliminada", 1900))
        verificar(almacen.repository.leer().categoriaId == "eliminada", "el repositorio conserva IDs, no inventa relaciones")
    } finally { almacen.cerrar() }

    val catalogo = AppRepository()
    val base = ExplorarUiState(categorias = catalogo.getCategorias(), anios = listOf(2026, 2025))
    val restaurado = restaurarFiltrosProyectos(base, esperado, false)
    verificar(restaurado.categoriaId == "cat-materiales" && restaurado.anio == 2026, "restaura filtros válidos")
    verificar(restaurado.textoProyectos.isEmpty(), "inicio nuevo no restaura búsqueda libre")
    verificar(restaurarFiltrosProyectos(base, PreferenciasFiltrosProyectos(true, "eliminada", 1900), false) == base,
        "categoría y año inexistentes usan Todos")
    verificar(restaurarFiltrosProyectos(base, PreferenciasFiltrosProyectos(true, "cat-materiales", 1900), false)
        .let { it.categoriaId == "cat-materiales" && it.anio == null }, "año inválido no descarta categoría válida")
    verificar(restaurarFiltrosProyectos(base, PreferenciasFiltrosProyectos(true, "eliminada", 2026), false)
        .let { it.categoriaId == null && it.anio == 2026 }, "categoría inválida no descarta año válido")
    verificar(restaurarFiltrosProyectos(base, PreferenciasFiltrosProyectos(false, "cat-materiales", 2026), false) == base,
        "desactivado ignora filtros residuales")
    val sesion = base.copy(categoriaId = "cat-procesos", anio = 2025, textoProyectos = "búsqueda de sesión",
        tipo = TipoContenido.INTEGRANTES, textoCapacidades = "ensayos", textoIntegrantes = "Ana")
    verificar(restaurarFiltrosProyectos(sesion, esperado, true) == sesion,
        "edición anterior a lectura no se pierde por carga tardía")
    val conservado = restaurarFiltrosProyectos(sesion, esperado, false)
    verificar(conservado.tipo == sesion.tipo && conservado.textoProyectos == sesion.textoProyectos &&
        conservado.textoCapacidades == sesion.textoCapacidades && conservado.textoIntegrantes == sesion.textoIntegrantes,
        "restaurar solo modifica categoría y año")
    val filtrado = filtrarExplorar(restaurado, catalogo.getProyectos(), catalogo.getCapacidades(), catalogo.getIntegrantes())
    verificar(filtrado.proyectos.single().id == "proy-mezclas", "los filtros restaurados se aplican al catálogo")
    verificar(catalogo.getIntegrantes("proy-mezclas").single().id == "int-ana" &&
        catalogo.getCapacidades("proy-mezclas").size == 2, "relaciones intactas")

    var escrituras = 0
    val fallaLectura = object : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow { throw IOException("Lectura simulada") }
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            escrituras++
            return emptyPreferences()
        }
    }
    val errorLectura = runCatching { PreferenciasFiltrosRepository(fallaLectura).leer() }.exceptionOrNull()
    verificar(errorLectura is IOException, "error de lectura se propaga, no se inventa lectura correcta")
    verificar(escrituras == 0, "fallo de lectura no escribe defaults")
    val fallaEscritura = object : DataStore<Preferences> {
        override val data = flowOf(emptyPreferences())
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
            throw IOException("Escritura simulada")
    }
    val errorEscritura = runCatching { PreferenciasFiltrosRepository(fallaEscritura).guardar(esperado) }.exceptionOrNull()
    verificar(errorEscritura is IOException, "error de escritura se propaga, no se declara éxito")
    comprobarViewModel()
    println("$comprobaciones comprobaciones de DataStore real/restauración/VM correctas; no prueban UI ni lifecycle Android.")
}
