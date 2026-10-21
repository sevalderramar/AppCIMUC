package com.example.appcimuc.viewmodel

import com.example.appcimuc.model.PreferenciasFiltrosProyectos

data class PreferenciasFiltrosUiState(
    // recordar representa solo el valor confirmado, no una escritura pendiente/fallida.
    val recordar: Boolean = false,
    val cargando: Boolean = true,
    val disponible: Boolean = false,
    val guardando: Boolean = false,
    val error: String? = null
)

// No toca consultas, tipo seleccionado ni otros catálogos. Cada filtro inválido vuelve a Todos.
// Una tarea restaurada o edición anterior a la lectura tiene prioridad sobre el disco.
internal fun restaurarFiltrosProyectos(
    estado: ExplorarUiState,
    preferencias: PreferenciasFiltrosProyectos,
    conservarEstadoActual: Boolean
): ExplorarUiState {
    if (conservarEstadoActual) return estado
    return estado.copy(
        categoriaId = preferencias.categoriaId?.takeIf { id ->
            preferencias.recordar && estado.categorias.any { it.id == id }
        },
        anio = preferencias.anio?.takeIf { preferencias.recordar && it in estado.anios }
    )
}
