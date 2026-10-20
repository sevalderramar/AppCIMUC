package com.example.appcimuc.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appcimuc.model.Capacidad
import com.example.appcimuc.model.Categoria
import com.example.appcimuc.model.Integrante
import com.example.appcimuc.model.Proyecto

@Composable
fun DetalleProyectoScreen(
    proyecto: Proyecto?,
    categoria: Categoria?,
    integrantes: List<Integrante>,
    capacidades: List<Capacidad>,
    onIntegrante: (String) -> Unit,
    onCapacidad: (String) -> Unit,
    onBack: () -> Unit
) {
    CatalogoLayout("Detalle del proyecto", onBack) {
        if (proyecto == null) {
            NoEncontrado("Proyecto")
        } else {
            Text(proyecto.titulo, style = MaterialTheme.typography.headlineMedium)
            Text("${categoria?.nombre ?: "Categoría no disponible"} · ${proyecto.anio}")
            Text(proyecto.descripcion, style = MaterialTheme.typography.bodyLarge)
            TituloSeccion("Integrantes relacionados")
            if (integrantes.isEmpty()) Text("Sin integrantes relacionados.")
            integrantes.forEach { integrante ->
                OutlinedButton(onClick = { onIntegrante(integrante.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text(integrante.nombre)
                }
            }
            TituloSeccion("Capacidades relacionadas")
            if (capacidades.isEmpty()) Text("Sin capacidades relacionadas.")
            capacidades.forEach { capacidad ->
                OutlinedButton(onClick = { onCapacidad(capacidad.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text(capacidad.nombre)
                }
            }
        }
    }
}
