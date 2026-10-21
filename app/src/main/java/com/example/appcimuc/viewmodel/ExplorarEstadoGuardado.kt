package com.example.appcimuc.viewmodel

import androidx.lifecycle.SavedStateHandle

// Solo entradas pequeñas. No se guarda ExplorarUiState, resultados, entidades ni borradores.
internal object ClavesExplorarGuardado {
    const val PRESENTE = "explorar.presente"
    const val TIPO = "explorar.tipo"
    const val PROYECTOS = "explorar.consulta_proyectos"
    const val CAPACIDADES = "explorar.consulta_capacidades"
    const val INTEGRANTES = "explorar.consulta_integrantes"
    const val CATEGORIA = "explorar.categoria"
    const val ANIO = "explorar.anio"
    const val MAX_CONSULTA = 500
}

internal fun tieneExplorarGuardado(handle: SavedStateHandle): Boolean =
    handle.get<Any>(ClavesExplorarGuardado.PRESENTE) == true

internal fun leerExplorarGuardado(handle: SavedStateHandle, base: ExplorarUiState): ExplorarUiState {
    if (!tieneExplorarGuardado(handle)) return base
    val tipo = handle.get<Any>(ClavesExplorarGuardado.TIPO) as? String
    val categoria = handle.get<Any>(ClavesExplorarGuardado.CATEGORIA) as? String
    val anio = handle.get<Any>(ClavesExplorarGuardado.ANIO) as? Int
    fun consulta(clave: String) = (handle.get<Any>(clave) as? String).orEmpty()
        .take(ClavesExplorarGuardado.MAX_CONSULTA)
    return base.copy(
        tipo = TipoContenido.entries.firstOrNull { it.name == tipo } ?: TipoContenido.PROYECTOS,
        textoProyectos = consulta(ClavesExplorarGuardado.PROYECTOS),
        textoCapacidades = consulta(ClavesExplorarGuardado.CAPACIDADES),
        textoIntegrantes = consulta(ClavesExplorarGuardado.INTEGRANTES),
        categoriaId = categoria?.takeIf { id -> base.categorias.any { it.id == id } },
        anio = anio?.takeIf { it in base.anios }
    )
}

internal fun guardarExplorar(handle: SavedStateHandle, estado: ExplorarUiState) {
    handle[ClavesExplorarGuardado.TIPO] = estado.tipo.name
    handle[ClavesExplorarGuardado.PROYECTOS] = estado.textoProyectos.take(ClavesExplorarGuardado.MAX_CONSULTA)
    handle[ClavesExplorarGuardado.CAPACIDADES] = estado.textoCapacidades.take(ClavesExplorarGuardado.MAX_CONSULTA)
    handle[ClavesExplorarGuardado.INTEGRANTES] = estado.textoIntegrantes.take(ClavesExplorarGuardado.MAX_CONSULTA)
    handle[ClavesExplorarGuardado.CATEGORIA] = estado.categoriaId
    handle[ClavesExplorarGuardado.ANIO] = estado.anio
    handle[ClavesExplorarGuardado.PRESENTE] = true
}
