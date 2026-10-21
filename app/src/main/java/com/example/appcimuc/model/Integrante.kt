package com.example.appcimuc.model

data class Integrante(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val cargo: String? = null,
    val especialidad: String? = null
)
