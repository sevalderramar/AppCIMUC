package com.example.appcimuc.viewmodel

import com.example.appcimuc.model.Proyecto

enum class PasoContacto { FORMULARIO, RESUMEN, CONFIRMACION }

data class ErroresContacto(
    val nombre: String? = null,
    val correo: String? = null,
    val mensaje: String? = null
) {
    val esValido: Boolean get() = nombre == null && correo == null && mensaje == null
}

data class ContactoUiState(
    val proyecto: Proyecto? = null,
    val nombre: String = "",
    val correo: String = "",
    val mensaje: String = "",
    val errores: ErroresContacto = ErroresContacto(),
    val paso: PasoContacto = PasoContacto.FORMULARIO
)

// Validación local de formato; no comprueba existencia del buzón ni utiliza red.
private val formatoCorreo = Regex(
    "^[A-Za-z0-9.!#\u0024%&'*+/=?^_`{|}~-]+@" +
        "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +
        "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+\u0024"
)

internal fun errorCorreo(correo: String): String? {
    val email = correo.trim()
    val local = email.substringBefore('@')
    val correoValido = email.length <= 254 && local.length <= 64 &&
        !local.startsWith('.') && !local.endsWith('.') && !local.contains("..") && formatoCorreo.matches(email)
    return when {
        email.isBlank() -> "Ingresa tu correo electrónico."
        !correoValido -> "Ingresa un correo válido, por ejemplo nombre@ejemplo.cl."
        else -> null
    }
}

internal fun validarContacto(nombre: String, correo: String, mensaje: String): ErroresContacto {
    return ErroresContacto(
        nombre = if (nombre.isBlank()) "Ingresa tu nombre." else null,
        correo = errorCorreo(correo),
        mensaje = if (mensaje.isBlank()) "Escribe un mensaje." else null
    )
}

// Transiciones puras: la UI no valida ni puede saltar pasos.
internal fun revisarContacto(estado: ContactoUiState): ContactoUiState {
    if (estado.proyecto == null || estado.paso != PasoContacto.FORMULARIO) return estado
    val errores = validarContacto(estado.nombre, estado.correo, estado.mensaje)
    return if (!errores.esValido) estado.copy(errores = errores) else estado.copy(
        nombre = estado.nombre.trim(), correo = estado.correo.trim(), mensaje = estado.mensaje.trim(),
        errores = errores, paso = PasoContacto.RESUMEN
    )
}

internal fun editarContacto(estado: ContactoUiState): ContactoUiState =
    if (estado.paso == PasoContacto.RESUMEN) estado.copy(paso = PasoContacto.FORMULARIO) else estado

internal fun confirmarContacto(estado: ContactoUiState): ContactoUiState {
    if (estado.paso != PasoContacto.RESUMEN) return estado
    val errores = validarContacto(estado.nombre, estado.correo, estado.mensaje)
    if (estado.proyecto == null || !errores.esValido) {
        return estado.copy(paso = PasoContacto.FORMULARIO, errores = errores)
    }
    // Solo cambia el estado local una vez. No envía ni persiste datos personales.
    return estado.copy(nombre = "", correo = "", mensaje = "", errores = ErroresContacto(),
        paso = PasoContacto.CONFIRMACION)
}
