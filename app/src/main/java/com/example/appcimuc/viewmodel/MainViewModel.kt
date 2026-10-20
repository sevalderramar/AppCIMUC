package com.example.appcimuc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.model.Proyecto
import com.example.appcimuc.repository.AppRepository

/**
 * ViewModel base: conecta la interfaz con los datos sin poner lógica en la Activity.
 */
class MainViewModel(
    private val repository: AppRepository = AppRepository()
) : ViewModel() {

    val appInfo: AppInfo = repository.getAppInfo()
    val proyectos = repository.getProyectos()
    val categorias = repository.getCategorias()
    val totalProyectos = proyectos.size
    val totalIntegrantes = repository.getIntegrantes().size
    val totalCapacidades = repository.getCapacidades().size
    // Selección demostrativa estable: dos proyectos más recientes, con desempate por ID.
    val destacados = proyectos.sortedWith(compareByDescending<Proyecto> { it.anio }
        .thenBy { it.id }).take(2)

    fun proyecto(id: String) = repository.getProyecto(id)
    fun categoria(id: String) = repository.getCategoria(id)
    fun integrante(id: String) = repository.getIntegrante(id)
    fun capacidad(id: String) = repository.getCapacidad(id)
    fun integrantes(proyectoId: String) = repository.getIntegrantes(proyectoId)
    fun capacidades(proyectoId: String) = repository.getCapacidades(proyectoId)
    fun proyectosDeIntegrante(id: String) = repository.getProyectosDeIntegrante(id)
    fun proyectosDeCapacidad(id: String) = repository.getProyectosDeCapacidad(id)
}
