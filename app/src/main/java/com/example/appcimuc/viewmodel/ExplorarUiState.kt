package com.example.appcimuc.viewmodel

import com.example.appcimuc.model.Capacidad
import com.example.appcimuc.model.Categoria
import com.example.appcimuc.model.Integrante
import com.example.appcimuc.model.Proyecto
import java.util.Locale

enum class TipoContenido { PROYECTOS, CAPACIDADES, INTEGRANTES }

data class ExplorarUiState(
    val tipo: TipoContenido = TipoContenido.PROYECTOS,
    val textoProyectos: String = "",
    val categoriaId: String? = null,
    val anio: Int? = null,
    val textoCapacidades: String = "",
    val textoIntegrantes: String = "",
    val categorias: List<Categoria> = emptyList(),
    val anios: List<Int> = emptyList(),
    val proyectos: List<Proyecto> = emptyList(),
    val capacidades: List<Capacidad> = emptyList(),
    val integrantes: List<Integrante> = emptyList(),
    val totalProyectos: Int = 0,
    val totalCapacidades: Int = 0,
    val totalIntegrantes: Int = 0
) {
    val texto: String get() = when (tipo) {
        TipoContenido.PROYECTOS -> textoProyectos
        TipoContenido.CAPACIDADES -> textoCapacidades
        TipoContenido.INTEGRANTES -> textoIntegrantes
    }
    val cantidadResultados: Int get() = when (tipo) {
        TipoContenido.PROYECTOS -> proyectos.size
        TipoContenido.CAPACIDADES -> capacidades.size
        TipoContenido.INTEGRANTES -> integrantes.size
    }
    val catalogoVacio: Boolean get() = when (tipo) {
        TipoContenido.PROYECTOS -> totalProyectos == 0
        TipoContenido.CAPACIDADES -> totalCapacidades == 0
        TipoContenido.INTEGRANTES -> totalIntegrantes == 0
    }
    val tieneBusquedaOFiltros: Boolean get() = texto.isNotEmpty() ||
        (tipo == TipoContenido.PROYECTOS && (categoriaId != null || anio != null))
}

private val espacios = Regex("\\s+")
private fun normalizar(texto: String) = texto.trim().replace(espacios, " ").lowercase(Locale.ROOT)
private fun coincide(consulta: String, vararg campos: String): Boolean =
    campos.any { normalizar(it).contains(consulta) }

// Función pura: sin Compose, Android, red ni persistencia. Combina filtros con AND.
internal fun filtrarExplorar(
    estado: ExplorarUiState,
    proyectos: List<Proyecto>,
    capacidades: List<Capacidad>,
    integrantes: List<Integrante>
): ExplorarUiState {
    val consultaProyectos = normalizar(estado.textoProyectos)
    val consultaCapacidades = normalizar(estado.textoCapacidades)
    val consultaIntegrantes = normalizar(estado.textoIntegrantes)
    return estado.copy(
        proyectos = proyectos.filter {
            coincide(consultaProyectos, it.titulo, it.descripcion) &&
                (estado.categoriaId == null || it.categoriaId == estado.categoriaId) &&
                (estado.anio == null || it.anio == estado.anio)
        },
        capacidades = capacidades.filter { coincide(consultaCapacidades, it.nombre, it.descripcion) },
        integrantes = integrantes.filter { coincide(consultaIntegrantes, it.nombre, it.descripcion) },
        totalProyectos = proyectos.size,
        totalCapacidades = capacidades.size,
        totalIntegrantes = integrantes.size
    )
}
