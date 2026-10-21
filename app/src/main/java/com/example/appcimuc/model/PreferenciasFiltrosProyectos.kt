package com.example.appcimuc.model

// Único contenido persistible: preferencia y dos filtros, nunca la búsqueda ni datos personales.
data class PreferenciasFiltrosProyectos(
    val recordar: Boolean = false,
    val categoriaId: String? = null,
    val anio: Int? = null
)
