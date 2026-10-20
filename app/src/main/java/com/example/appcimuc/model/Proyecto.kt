package com.example.appcimuc.model

// Relaciones provisionales por ID; pendientes de contraste con el diagrama del equipo.
data class Proyecto(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoriaId: String,
    val anio: Int,
    val integranteIds: List<String>,
    val capacidadIds: List<String>
)
