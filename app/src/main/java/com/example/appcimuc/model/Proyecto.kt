package com.example.appcimuc.model

// Relaciones provisionales por ID; pendientes de contraste con el diagrama del equipo.
data class Proyecto(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoriaId: String,
    val anio: Int,
    val integranteIds: List<String>,
    val capacidadIds: List<String>,
    val estado: String = "En desarrollo",
    // Recurso drawable local opcional; null cuando aún no se dispone de fotografía.
    val imagenRes: Int? = null,
    // null significa información desconocida, no cero ni rechazo de postulaciones.
    val cuposDisponibles: Int? = null,
    val aceptaPostulaciones: Boolean? = null
) {
    val puedePostular: Boolean get() = aceptaPostulaciones == true && (cuposDisponibles ?: 0) > 0
}
