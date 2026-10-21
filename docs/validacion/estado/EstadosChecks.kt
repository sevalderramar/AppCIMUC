import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.SavedStateHandle
import com.example.appcimuc.repository.PreferenciasFiltrosRepository
import com.example.appcimuc.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// Simula entradas restauradas en SavedStateHandle y usa VMs reales. No simula Bundle/Android ni muerte real de proceso.
private var checks = 0
private fun verificar(valor: Boolean, mensaje: String) {
    check(valor) { mensaje }
    checks++
}
private val recordar = booleanPreferencesKey("recordar_filtros_proyectos")
private val categoria = stringPreferencesKey("categoria_proyecto")
private val anio = intPreferencesKey("anio_proyecto")
private class StoreEstado : DataStore<Preferences> {
    var valor: Preferences = preferencesOf(recordar to true, categoria to "cat-materiales", anio to 2026)
    var gate: CompletableDeferred<Unit>? = null
    var escrituras = 0
    override val data: Flow<Preferences> = flow { gate?.await(); emit(valor) }
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        escrituras++
        valor = transform(valor)
        return valor
    }
}
private fun copia(handle: SavedStateHandle): Map<String, Any?> = handle.keys().associateWith { handle.get<Any>(it) }

fun main() = runBlocking {
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Unconfined + job)
    fun vm(handle: SavedStateHandle, store: StoreEstado) = MainViewModel(
        preferenciasRepository = PreferenciasFiltrosRepository(store), preferenciasScope = scope, savedStateHandle = handle)
    try {
        val vacio = SavedStateHandle()
        verificar(!tieneExplorarGuardado(vacio), "un handle vacío no es una tarea restaurada")
        val disco = StoreEstado()
        val original = vm(vacio, disco)
        verificar(original.explorarUiState.value.let { it.categoriaId == "cat-materiales" && it.anio == 2026 &&
            it.texto.isEmpty() && it.tipo == TipoContenido.PROYECTOS }, "inicio nuevo aplica preferencias, no consultas")
        verificar(disco.escrituras == 0, "inicio nuevo solo lee DataStore")
        original.seleccionarCategoria("cat-procesos")
        original.seleccionarAnio(2025)
        original.buscar("consulta proyectos")
        original.seleccionarTipo(TipoContenido.CAPACIDADES)
        original.buscar("modelado")
        original.seleccionarTipo(TipoContenido.INTEGRANTES)
        original.buscar("Ana")
        val snapshot = copia(vacio)
        verificar(snapshot.size == 7, "solo siete valores restaurables (incluido marcador)")
        verificar(snapshot.values.all { it == null || it is String || it is Int || it is Boolean },
            "sin listas, entidades, imágenes ni borradores en handle")
        verificar(snapshot[ClavesExplorarGuardado.TIPO] == TipoContenido.INTEGRANTES.name &&
            snapshot[ClavesExplorarGuardado.PROYECTOS] == "consulta proyectos", "selección y consulta en handle autoritativo")
        val otroDisco = StoreEstado()
        val recuperado = vm(SavedStateHandle(snapshot), otroDisco)
        verificar(recuperado.explorarUiState.value.let { it.tipo == TipoContenido.INTEGRANTES &&
            it.textoProyectos == "consulta proyectos" && it.textoCapacidades == "modelado" && it.textoIntegrantes == "Ana" &&
            it.categoriaId == "cat-procesos" && it.anio == 2025 }, "tarea restaurada recupera todas las entradas")
        verificar(recuperado.preferenciasFiltrosUiState.value.recordar && otroDisco.escrituras == 0,
            "tarea gana a DataStore sin reescribir preferencias")
        verificar(recuperado.integrante("int-ana") != null && recuperado.integrantes("proy-energia").single().id == "int-luis",
            "datos resueltos de nuevo por ID desde repositorio")
        verificar(recuperado.proyecto("eliminado") == null && recuperado.integrante("eliminado") == null &&
            recuperado.capacidad("eliminado") == null, "IDs inexistentes se resuelven como ausentes")

        val sinFiltros = SavedStateHandle(snapshot + mapOf(ClavesExplorarGuardado.CATEGORIA to null, ClavesExplorarGuardado.ANIO to null))
        val todos = vm(sinFiltros, StoreEstado())
        verificar(todos.explorarUiState.value.categoriaId == null && todos.explorarUiState.value.anio == null,
            "Todos restaurado no es sustituido por preferencias activas")
        val invalidos = SavedStateHandle(snapshot + mapOf(ClavesExplorarGuardado.CATEGORIA to "borrada", ClavesExplorarGuardado.ANIO to 1900,
            ClavesExplorarGuardado.TIPO to "tipo_eliminado"))
        val seguro = vm(invalidos, StoreEstado())
        verificar(seguro.explorarUiState.value.let { it.categoriaId == null && it.anio == null && it.tipo == TipoContenido.PROYECTOS },
            "filtros/tipo inválidos usan valores seguros sin fallback al disco")
        val tiposInvalidos = SavedStateHandle(snapshot + mapOf(ClavesExplorarGuardado.PROYECTOS to 42,
            ClavesExplorarGuardado.ANIO to "no-numérico"))
        verificar(vm(tiposInvalidos, StoreEstado()).explorarUiState.value.let { it.textoProyectos.isEmpty() && it.anio == null },
            "valores de tipo inesperado no lanzan cast exception")

        val apagado = StoreEstado().apply { valor = preferencesOf(recordar to false) }
        val restauradoApagado = vm(SavedStateHandle(snapshot), apagado)
        verificar(!restauradoApagado.preferenciasFiltrosUiState.value.recordar &&
            restauradoApagado.explorarUiState.value.categoriaId == "cat-procesos", "tarea conserva filtros aunque recordar esté apagado")
        val nuevoApagado = vm(SavedStateHandle(), apagado)
        verificar(nuevoApagado.explorarUiState.value.let { it.categoriaId == null && it.anio == null && it.texto.isEmpty() },
            "inicio sin tarea ni recordar usa defaults")
        val gate = CompletableDeferred<Unit>()
        val tardio = StoreEstado().apply { this.gate = gate }
        val duranteCarga = vm(SavedStateHandle(snapshot), tardio)
        verificar(duranteCarga.explorarUiState.value.textoIntegrantes == "Ana", "estado de tarea disponible antes de terminar lectura")
        duranteCarga.seleccionarTipo(TipoContenido.PROYECTOS)
        duranteCarga.seleccionarCategoria(null)
        duranteCarga.buscar("nueva selección")
        gate.complete(Unit)
        verificar(duranteCarga.explorarUiState.value.let { it.categoriaId == null && it.textoProyectos == "nueva selección" },
            "lectura tardía no sustituye una nueva selección")
        verificar(tardio.valor[categoria] == null, "solo la edición explícita provoca escritura tras la lectura")
        val handleLargo = SavedStateHandle()
        val consultaLarga = vm(handleLargo, StoreEstado())
        consultaLarga.buscar("a".repeat(10000))
        verificar(consultaLarga.explorarUiState.value.textoProyectos.length == 500 &&
            (handleLargo.get<Any>(ClavesExplorarGuardado.PROYECTOS) as String).length == 500,
            "consulta acotada igual en UI y handle, sin dos valores diferentes")

        // Formulario/resumen comparten el mismo VM mientras vive. Otro VM por ID no hereda datos ni eventos.
        val contacto = ContactoViewModel("proy-energia")
        contacto.cambiarNombre("Persona Demo")
        contacto.cambiarCorreo("persona@example.test")
        contacto.cambiarMensaje("Mensaje ficticio")
        verificar(contacto.revisar() && contacto.uiState.value.paso == PasoContacto.RESUMEN, "contacto alcanza resumen en memoria")
        contacto.editar()
        verificar(contacto.uiState.value.nombre == "Persona Demo", "editar conserva datos del VM de flujo")
        contacto.revisar()
        val nuevoContacto = ContactoViewModel("proy-energia")
        verificar(nuevoContacto.uiState.value.let { it.paso == PasoContacto.FORMULARIO && it.nombre.isEmpty() &&
            it.correo.isEmpty() && it.mensaje.isEmpty() && it.errores.esValido }, "nuevo proceso/VM de contacto empieza vacío")
        verificar(!nuevoContacto.confirmar(), "nuevo VM no repite confirmación de un resumen anterior")
        verificar(contacto.confirmar() && !contacto.confirmar(), "confirmación única en memoria")
        verificar(ContactoViewModel("proy-energia").uiState.value.paso == PasoContacto.FORMULARIO,
            "resultado no se restaura como evento")
        val resumenInvalido = ContactoUiState(proyecto = contacto.uiState.value.proyecto, paso = PasoContacto.RESUMEN)
        verificar(confirmarContacto(resumenInvalido).paso == PasoContacto.FORMULARIO, "resumen sin datos vuelve seguro al formulario")

        val participacion = ParticipacionViewModel("proy-energia")
        participacion.abrir(TipoParticipacion.APORTE)
        verificar(!participacion.enviar(TipoParticipacion.APORTE) && participacion.uiState.value.aporte.errores.size == 4,
            "errores de aporte permanecen en VM")
        participacion.cambiar(TipoParticipacion.APORTE, CampoParticipacion.NOMBRE, "Persona Demo")
        participacion.cambiar(TipoParticipacion.APORTE, CampoParticipacion.CORREO, "persona@example.test")
        participacion.cambiar(TipoParticipacion.APORTE, CampoParticipacion.ASUNTO, "Referencia")
        participacion.cambiar(TipoParticipacion.APORTE, CampoParticipacion.INFORMACION, "Texto ficticio")
        val nuevaParticipacion = ParticipacionViewModel("proy-energia")
        verificar(nuevaParticipacion.uiState.value.let { it.modal == ModalParticipacion.CERRADO && it.aporte.valores.isEmpty() &&
            it.aporte.errores.isEmpty() && it.postulacion.valores.isEmpty() }, "borradores/errores/modal no se restauran tras nuevo VM")
        verificar(participacion.enviar(TipoParticipacion.APORTE) && !participacion.enviar(TipoParticipacion.APORTE),
            "aporte no se envía dos veces")
        participacion.cerrar()
        verificar(participacion.uiState.value.modal == ModalParticipacion.CERRADO && participacion.uiState.value.aporte.valores.isEmpty(),
            "finalizar/cerrar limpia estado")
        verificar(copia(vacio) == snapshot, "los formularios no alteran ni guardan datos personales en estado de exploración")
        println("$checks comprobaciones de estado/preferencia/formularios correctas; no son una prueba Android de proceso/rotación.")
    } finally { job.cancelAndJoin() }
}
