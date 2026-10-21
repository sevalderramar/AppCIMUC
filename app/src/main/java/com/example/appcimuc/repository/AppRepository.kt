package com.example.appcimuc.repository

import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.model.Proyecto

/**
 * Repositorio simple para dejar creada la capa repository solicitada como opcional.
 * Más adelante puede reemplazarse por una API, base de datos o fuente local.
 */
class AppRepository {
    fun getAppInfo(): AppInfo {
        return AppInfo(
            title = "Explora proyectos, capacidades e investigación",
            message = "Descubre el ecosistema de innovación del CIM UC y conecta con oportunidades de colaboración."
        )
    }

    fun getProyectos(): List<Proyecto> = CatalogoEjemplo.proyectos
    fun getCategorias() = CatalogoEjemplo.categorias
    fun getIntegrantes() = CatalogoEjemplo.integrantes
    fun getCapacidades() = CatalogoEjemplo.capacidades
    fun getProyecto(id: String) = getProyectos().find { it.id == id }
    fun getCategoria(id: String) = CatalogoEjemplo.categorias.find { it.id == id }
    fun getIntegrante(id: String) = CatalogoEjemplo.integrantes.find { it.id == id }
    fun getCapacidad(id: String) = CatalogoEjemplo.capacidades.find { it.id == id }
    fun getIntegrantes(proyectoId: String) = getProyecto(proyectoId)?.integranteIds
        .orEmpty().mapNotNull(::getIntegrante)
    fun getCapacidades(proyectoId: String) = getProyecto(proyectoId)?.capacidadIds
        .orEmpty().mapNotNull(::getCapacidad)
    fun getRecursos(proyectoId: String) = CatalogoEjemplo.recursos.filter { it.proyectoId == proyectoId }
    fun getProyectosDeIntegrante(id: String) = getProyectos().filter { id in it.integranteIds }
    fun getProyectosDeCapacidad(id: String) = getProyectos().filter { id in it.capacidadIds }
}
