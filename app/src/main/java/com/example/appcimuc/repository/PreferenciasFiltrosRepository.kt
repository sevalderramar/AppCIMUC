package com.example.appcimuc.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appcimuc.model.PreferenciasFiltrosProyectos
import kotlinx.coroutines.flow.first

// Delegate de nivel superior: una sola instancia por archivo en el proceso.
private val Context.filtrosProyectosDataStore by preferencesDataStore(name = "filtros_proyectos")

class PreferenciasFiltrosRepository(private val dataStore: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.filtrosProyectosDataStore)

    suspend fun leer(): PreferenciasFiltrosProyectos {
        val preferencias = dataStore.data.first()
        val recordar = preferencias[RECORDAR] ?: false
        return PreferenciasFiltrosProyectos(recordar,
            if (recordar) preferencias[CATEGORIA] else null,
            if (recordar) preferencias[ANIO] else null)
    }

    // edit es atómico/asíncrono. Los errores se propagan al VM, no se simula éxito.
    suspend fun guardar(valor: PreferenciasFiltrosProyectos) {
        dataStore.edit { preferencias ->
            preferencias[RECORDAR] = valor.recordar
            preferencias.remove(CATEGORIA)
            preferencias.remove(ANIO)
            if (valor.recordar) {
                valor.categoriaId?.let { preferencias[CATEGORIA] = it }
                valor.anio?.let { preferencias[ANIO] = it }
            }
        }
    }

    private companion object {
        val RECORDAR = booleanPreferencesKey("recordar_filtros_proyectos")
        val CATEGORIA = stringPreferencesKey("categoria_proyecto")
        val ANIO = intPreferencesKey("anio_proyecto")
    }
}
