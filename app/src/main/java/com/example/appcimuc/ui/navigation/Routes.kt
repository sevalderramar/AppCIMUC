package com.example.appcimuc.ui.navigation

import android.net.Uri

object Routes {
    const val INICIO = "inicio"
    const val EXPLORAR = "explorar"
    const val ID = "id"
    const val PROYECTO = "proyecto/{id}"
    const val INTEGRANTE = "integrante/{id}"
    const val CAPACIDAD = "capacidad/{id}"

    fun proyecto(id: String) = "proyecto/${Uri.encode(id)}"
    fun integrante(id: String) = "integrante/${Uri.encode(id)}"
    fun capacidad(id: String) = "capacidad/${Uri.encode(id)}"
}
