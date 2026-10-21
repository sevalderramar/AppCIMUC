package com.example.appcimuc.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appcimuc.model.Integrante
import com.example.appcimuc.model.Proyecto

@Composable
fun DetalleIntegranteScreen(
    integrante: Integrante?, proyectos: List<Proyecto>, onProyecto: (String) -> Unit, onBack: () -> Unit
) {
    CatalogoLayout("Detalle del integrante", onBack) {
        if (integrante == null) {
            NoEncontrado("Integrante")
        } else {
            Text(integrante.nombre, style = MaterialTheme.typography.headlineMedium)
            integrante.cargo?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
            integrante.especialidad?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
            Text(integrante.descripcion, style = MaterialTheme.typography.bodyLarge)
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
