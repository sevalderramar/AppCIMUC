package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.model.Proyecto
import com.example.appcimuc.model.PreferenciasFiltrosProyectos
import com.example.appcimuc.repository.AppRepository
import com.example.appcimuc.repository.PreferenciasFiltrosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel base: conecta la interfaz con los datos sin poner lógica en la Activity.
 */
class MainViewModel(
    private val repository: AppRepository = AppRepository(),
    private val preferenciasRepository: PreferenciasFiltrosRepository? = null,
    // Solo para pruebas JVM con un scope controlado; la app utiliza viewModelScope.
    private val preferenciasScope: CoroutineScope? = null,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    val appInfo: AppInfo = repository.getAppInfo()
    val proyectos = repository.getProyectos()
    val categorias = repository.getCategorias()
    private val capacidadesCatalogo = repository.getCapacidades()
    private val integrantesCatalogo = repository.getIntegrantes()
    val totalProyectos = proyectos.size
    val totalIntegrantes = repository.getIntegrantes().size
    val totalCapacidades = repository.getCapacidades().size
    // Selección demostrativa estable: dos proyectos más recientes, con desempate por ID.
    val destacados = proyectos.sortedWith(compareByDescending<Proyecto> { it.anio }
        .thenBy { it.id }).take(2)

    // Se captura ANTES de registrar defaults: una tarea restaurada prevalece incluso
    // cuando sus filtros eran Todos y la preferencia persistente dice otra cosa.
    private val tareaRestaurada = tieneExplorarGuardado(savedStateHandle)
    private val baseExplorar = ExplorarUiState(
        categorias = categorias.filter { categoria -> proyectos.any { it.categoriaId == categoria.id } },
        anios = proyectos.map { it.anio }.distinct().sortedDescending()
    )
    // Proyección para UI: las entradas autoritativas se leen del handle; los resultados
    // se recalculan. Esta proyección no se serializa ni se edita desde la UI.
    private val _explorarUiState = MutableStateFlow(filtrarExplorar(
        leerExplorarGuardado(savedStateHandle, baseExplorar), proyectos, capacidadesCatalogo, integrantesCatalogo
    ))
    val explorarUiState: StateFlow<ExplorarUiState> = _explorarUiState.asStateFlow()

    private val _preferenciasFiltrosUiState = MutableStateFlow(PreferenciasFiltrosUiState())
    val preferenciasFiltrosUiState: StateFlow<PreferenciasFiltrosUiState> = _preferenciasFiltrosUiState.asStateFlow()
    private var filtrosEditadosEnSesion = false
    private var recordarPendiente: Boolean? = null

    init {
        guardarExplorar(savedStateHandle, _explorarUiState.value)
        cargarPreferencias()
    }

    private fun cargarPreferencias() {
        val preferencias = preferenciasRepository
        if (preferencias == null) {
            _preferenciasFiltrosUiState.value = PreferenciasFiltrosUiState(cargando = false,
                error = "Las preferencias no están disponibles. Los filtros se mantienen solo en esta sesión.")
            return
        }
        _preferenciasFiltrosUiState.update { it.copy(cargando = true, error = null) }
        (preferenciasScope ?: viewModelScope).launch {
            try {
                val guardadas = preferencias.leer()
                actualizarExplorar {
                    restaurarFiltrosProyectos(it, guardadas, tareaRestaurada || filtrosEditadosEnSesion)
                }
                _preferenciasFiltrosUiState.value = PreferenciasFiltrosUiState(
                    recordar = guardadas.recordar, cargando = false, disponible = true)
                // Solo una edición explícita anterior a la lectura puede provocar esta escritura.
                // La carga sin ediciones nunca escribe valores iniciales sobre el archivo.
                if (filtrosEditadosEnSesion && guardadas.recordar) guardarPreferencias(true)
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                _preferenciasFiltrosUiState.update { it.copy(cargando = false,
                    error = "No se pudieron leer las preferencias. Puedes usar filtros de sesión y reintentar.") }
            }
        }
    }

    fun cambiarRecordarFiltros(recordar: Boolean) {
        guardarPreferencias(recordar)
    }

    fun reintentarPreferencias() {
        val estado = _preferenciasFiltrosUiState.value
        if (estado.cargando || estado.guardando) return
        val pendiente = recordarPendiente
        if (pendiente != null) guardarPreferencias(pendiente)
        else if (!estado.disponible) cargarPreferencias()
    }

    private fun guardarPreferencias(recordar: Boolean) {
        val preferencias = preferenciasRepository ?: return
        val estado = _preferenciasFiltrosUiState.value
        if (!estado.disponible || estado.cargando || estado.guardando) return
        val filtros = _explorarUiState.value
        val valor = PreferenciasFiltrosProyectos(recordar, filtros.categoriaId, filtros.anio)
        _preferenciasFiltrosUiState.update { it.copy(guardando = true, error = null) }
        (preferenciasScope ?: viewModelScope).launch {
            try {
                preferencias.guardar(valor)
                recordarPendiente = null
                _preferenciasFiltrosUiState.update { it.copy(recordar = recordar, guardando = false, error = null) }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                recordarPendiente = recordar
                _preferenciasFiltrosUiState.update { it.copy(guardando = false,
                    error = "No se pudieron guardar los cambios. Los filtros de sesión se conservan; reintenta el guardado.") }
            }
        }
    }

    private fun filtrosCambiados() {
        filtrosEditadosEnSesion = true
        // Tras un fallo no ocultamos el error con escrituras implícitas. Reintentar usa los filtros actuales.
        if (_preferenciasFiltrosUiState.value.recordar && recordarPendiente == null) guardarPreferencias(true)
    }

    fun seleccionarTipo(tipo: TipoContenido) {
        actualizarExplorar { it.copy(tipo = tipo) }
    }

    fun buscar(texto: String) = actualizarExplorar { estado ->
        val consulta = texto.take(ClavesExplorarGuardado.MAX_CONSULTA)
        when (estado.tipo) {
            TipoContenido.PROYECTOS -> estado.copy(textoProyectos = consulta)
            TipoContenido.CAPACIDADES -> estado.copy(textoCapacidades = consulta)
            TipoContenido.INTEGRANTES -> estado.copy(textoIntegrantes = consulta)
        }
    }

    fun seleccionarCategoria(id: String?) {
        if (_explorarUiState.value.tipo != TipoContenido.PROYECTOS || _preferenciasFiltrosUiState.value.guardando) return
        actualizarExplorar { estado ->
            estado.copy(categoriaId = id?.takeIf { valor -> estado.categorias.any { it.id == valor } })
        }
        filtrosCambiados()
    }

    fun seleccionarAnio(anio: Int?) {
        if (_explorarUiState.value.tipo != TipoContenido.PROYECTOS || _preferenciasFiltrosUiState.value.guardando) return
        actualizarExplorar { estado -> estado.copy(anio = anio?.takeIf { it in estado.anios }) }
        filtrosCambiados()
    }

    fun limpiarExplorar() {
        val proyectos = _explorarUiState.value.tipo == TipoContenido.PROYECTOS
        if (proyectos && _preferenciasFiltrosUiState.value.guardando) return
        actualizarExplorar { estado ->
            when (estado.tipo) {
                TipoContenido.PROYECTOS -> estado.copy(textoProyectos = "", categoriaId = null, anio = null)
                TipoContenido.CAPACIDADES -> estado.copy(textoCapacidades = "")
                TipoContenido.INTEGRANTES -> estado.copy(textoIntegrantes = "")
            }
        }
        if (proyectos) filtrosCambiados()
    }

    private fun actualizarExplorar(transformar: (ExplorarUiState) -> ExplorarUiState) {
        val entrada = transformar(leerExplorarGuardado(savedStateHandle, baseExplorar))
        guardarExplorar(savedStateHandle, entrada)
        _explorarUiState.value = filtrarExplorar(
            leerExplorarGuardado(savedStateHandle, baseExplorar), proyectos, capacidadesCatalogo, integrantesCatalogo)
    }

    fun proyecto(id: String) = repository.getProyecto(id)
    fun categoria(id: String) = repository.getCategoria(id)
    fun integrante(id: String) = repository.getIntegrante(id)
    fun capacidad(id: String) = repository.getCapacidad(id)
    fun integrantes(proyectoId: String) = repository.getIntegrantes(proyectoId)
    fun capacidades(proyectoId: String) = repository.getCapacidades(proyectoId)
    fun recursos(proyectoId: String) = repository.getRecursos(proyectoId)
    fun proyectosDeIntegrante(id: String) = repository.getProyectosDeIntegrante(id)
    fun proyectosDeCapacidad(id: String) = repository.getProyectosDeCapacidad(id)

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return crear(modelClass, SavedStateHandle())
                }

                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return crear(modelClass, extras.createSavedStateHandle())
                }

                @Suppress("UNCHECKED_CAST")
                private fun <T : ViewModel> crear(modelClass: Class<T>, handle: SavedStateHandle): T {
                    require(modelClass.isAssignableFrom(MainViewModel::class.java))
                    return MainViewModel(preferenciasRepository = PreferenciasFiltrosRepository(appContext),
                        savedStateHandle = handle) as T
                }
            }
        }
    }
}
