package com.example.appcimuc.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appcimuc.model.Capacidad
import com.example.appcimuc.model.Proyecto

@Composable
fun DetalleCapacidadScreen(
    capacidad: Capacidad?, proyectos: List<Proyecto>, onProyecto: (String) -> Unit, onBack: () -> Unit
) {
    CatalogoLayout("Detalle de la capacidad", onBack) {
        if (capacidad == null) {
            NoEncontrado("Capacidad")
        } else {
            Text(capacidad.nombre, style = MaterialTheme.typography.headlineMedium)
            Text(capacidad.descripcion, style = MaterialTheme.typography.bodyLarge)
            TituloSeccion("Proyectos relacionados")
            if (proyectos.isEmpty()) Text("Sin proyectos relacionados.")
            proyectos.forEach { proyecto ->
                OutlinedButton(onClick = { onProyecto(proyecto.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text(proyecto.titulo)
                }
            }
        }
    }
}
