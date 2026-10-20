package com.example.appcimuc.viewmodel

import com.example.appcimuc.model.Proyecto

enum class TipoParticipacion { POSTULACION, APORTE }
enum class CampoParticipacion { NOMBRE, CORREO, INSTITUCION, MOTIVACION, ASUNTO, INFORMACION, FUENTE }
enum class ModalParticipacion { CERRADO, POSTULACION, APORTE, EXITO_POSTULACION, EXITO_APORTE }

data class FormularioParticipacion(
    val valores: Map<CampoParticipacion, String> = emptyMap(),
    val errores: Map<CampoParticipacion, String> = emptyMap(),
    val intento: Int = 0
)

data class ParticipacionUiState(
    val proyecto: Proyecto? = null,
    val modal: ModalParticipacion = ModalParticipacion.CERRADO,
    val postulacion: FormularioParticipacion = FormularioParticipacion(),
    val aporte: FormularioParticipacion = FormularioParticipacion()
)

internal fun camposParticipacion(tipo: TipoParticipacion): List<CampoParticipacion> = when (tipo) {
    TipoParticipacion.POSTULACION -> listOf(CampoParticipacion.NOMBRE, CampoParticipacion.CORREO,
        CampoParticipacion.INSTITUCION, CampoParticipacion.MOTIVACION)
    TipoParticipacion.APORTE -> listOf(CampoParticipacion.NOMBRE, CampoParticipacion.CORREO,
        CampoParticipacion.ASUNTO, CampoParticipacion.INFORMACION, CampoParticipacion.FUENTE)
}

private fun modalFormulario(tipo: TipoParticipacion) = if (tipo == TipoParticipacion.POSTULACION)
    ModalParticipacion.POSTULACION else ModalParticipacion.APORTE

internal fun abrirParticipacion(estado: ParticipacionUiState, tipo: TipoParticipacion): ParticipacionUiState {
    if (estado.modal != ModalParticipacion.CERRADO || estado.proyecto == null) return estado
    if (tipo == TipoParticipacion.POSTULACION && !estado.proyecto.puedePostular) return estado
    return if (tipo == TipoParticipacion.POSTULACION) estado.copy(modal = modalFormulario(tipo),
        postulacion = FormularioParticipacion()) else estado.copy(modal = modalFormulario(tipo), aporte = FormularioParticipacion())
}

internal fun cerrarParticipacion(estado: ParticipacionUiState) = estado.copy(
    modal = ModalParticipacion.CERRADO, postulacion = FormularioParticipacion(), aporte = FormularioParticipacion())

private fun errorCampo(campo: CampoParticipacion, valor: String): String? = when {
    campo == CampoParticipacion.FUENTE -> null // Respaldo libre, no exige URL.
    campo == CampoParticipacion.CORREO -> errorCorreo(valor)
    valor.isNotBlank() -> null
    else -> when (campo) {
        CampoParticipacion.NOMBRE -> "Ingresa tu nombre."
        CampoParticipacion.INSTITUCION -> "Indica tu institución o universidad."
        CampoParticipacion.MOTIVACION -> "Describe tu motivación y competencias relevantes."
        CampoParticipacion.ASUNTO -> "Indica el asunto o tipo de información."
        else -> "Describe la información que deseas entregar."
    }
}

internal fun cambiarCampoParticipacion(estado: ParticipacionUiState, tipo: TipoParticipacion,
    campo: CampoParticipacion, valor: String): ParticipacionUiState {
    if (estado.modal != modalFormulario(tipo) || campo !in camposParticipacion(tipo)) return estado
    val formulario = if (tipo == TipoParticipacion.POSTULACION) estado.postulacion else estado.aporte
    val errores = formulario.errores.toMutableMap()
    if (campo in errores) {
        val error = errorCampo(campo, valor)
        if (error == null) errores.remove(campo) else errores[campo] = error
    }
    val nuevo = formulario.copy(valores = formulario.valores + (campo to valor), errores = errores.toMap())
    return if (tipo == TipoParticipacion.POSTULACION) estado.copy(postulacion = nuevo) else estado.copy(aporte = nuevo)
}

internal fun enviarParticipacion(estado: ParticipacionUiState, tipo: TipoParticipacion): ParticipacionUiState {
    if (estado.modal != modalFormulario(tipo) || estado.proyecto == null) return estado
    if (tipo == TipoParticipacion.POSTULACION && !estado.proyecto.puedePostular) return cerrarParticipacion(estado)
    val formulario = if (tipo == TipoParticipacion.POSTULACION) estado.postulacion else estado.aporte
    val errores = camposParticipacion(tipo).mapNotNull { campo ->
        errorCampo(campo, formulario.valores[campo].orEmpty())?.let { campo to it }
    }.toMap()
    if (errores.isNotEmpty()) {
        val invalido = formulario.copy(errores = errores, intento = formulario.intento + 1)
        return if (tipo == TipoParticipacion.POSTULACION) estado.copy(postulacion = invalido) else estado.copy(aporte = invalido)
    }
    // Registro exclusivamente simulado: sin envíos, almacenamiento de datos ni modificación de cupos/equipo.
    return if (tipo == TipoParticipacion.POSTULACION) estado.copy(modal = ModalParticipacion.EXITO_POSTULACION,
        postulacion = FormularioParticipacion()) else estado.copy(modal = ModalParticipacion.EXITO_APORTE,
        aporte = FormularioParticipacion())
}
