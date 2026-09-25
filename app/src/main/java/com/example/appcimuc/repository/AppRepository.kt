package com.example.appcimuc.repository

import com.example.appcimuc.model.AppInfo

/**
 * Repositorio simple para dejar creada la capa repository solicitada como opcional.
 * Más adelante puede reemplazarse por una API, base de datos o fuente local.
 */
class AppRepository {
    fun getAppInfo(): AppInfo {
        return AppInfo(
            title = "Proyecto base MVVM",
            message = "Estructura de la Guía 7 creada correctamente"
        )
    }
}
