package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.model.Proyecto
import com.example.appcimuc.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel base: conecta la interfaz con los datos sin poner lógica en la Activity.
 */
class MainViewModel(
    private val repository: AppRepository = AppRepository()
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

    private val _explorarUiState = MutableStateFlow(filtrarExplorar(
        ExplorarUiState(
            categorias = categorias.filter { categoria -> proyectos.any { it.categoriaId == categoria.id } },
            anios = proyectos.map { it.anio }.distinct().sortedDescending()
        ), proyectos, capacidadesCatalogo, integrantesCatalogo
    ))
    val explorarUiState: StateFlow<ExplorarUiState> = _explorarUiState.asStateFlow()

    fun seleccionarTipo(tipo: TipoContenido) {
        _explorarUiState.update { it.copy(tipo = tipo) }
    }

    fun buscar(texto: String) = actualizarExplorar { estado ->
        when (estado.tipo) {
            TipoContenido.PROYECTOS -> estado.copy(textoProyectos = texto)
            TipoContenido.CAPACIDADES -> estado.copy(textoCapacidades = texto)
            TipoContenido.INTEGRANTES -> estado.copy(textoIntegrantes = texto)
        }
    }

    fun seleccionarCategoria(id: String?) = actualizarExplorar { estado ->
        if (estado.tipo == TipoContenido.PROYECTOS) {
            estado.copy(categoriaId = id?.takeIf { valor -> estado.categorias.any { it.id == valor } })
        } else estado
    }

    fun seleccionarAnio(anio: Int?) = actualizarExplorar { estado ->
        if (estado.tipo == TipoContenido.PROYECTOS) estado.copy(anio = anio?.takeIf { it in estado.anios })
        else estado
    }

    fun limpiarExplorar() = actualizarExplorar { estado ->
        when (estado.tipo) {
            TipoContenido.PROYECTOS -> estado.copy(textoProyectos = "", categoriaId = null, anio = null)
            TipoContenido.CAPACIDADES -> estado.copy(textoCapacidades = "")
            TipoContenido.INTEGRANTES -> estado.copy(textoIntegrantes = "")
        }
    }

    private fun actualizarExplorar(transformar: (ExplorarUiState) -> ExplorarUiState) {
        _explorarUiState.update { estado ->
            filtrarExplorar(transformar(estado), proyectos, capacidadesCatalogo, integrantesCatalogo)
        }
    }

    fun proyecto(id: String) = repository.getProyecto(id)
    fun categoria(id: String) = repository.getCategoria(id)
    fun integrante(id: String) = repository.getIntegrante(id)
    fun capacidad(id: String) = repository.getCapacidad(id)
    fun integrantes(proyectoId: String) = repository.getIntegrantes(proyectoId)
    fun capacidades(proyectoId: String) = repository.getCapacidades(proyectoId)
    fun proyectosDeIntegrante(id: String) = repository.getProyectosDeIntegrante(id)
    fun proyectosDeCapacidad(id: String) = repository.getProyectosDeCapacidad(id)
}
