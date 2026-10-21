package com.example.appcimuc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appcimuc.model.Recurso

@Composable
internal fun RecursoProyectoDialog(recurso: Recurso, onCerrar: () -> Unit) {
    val resources = LocalContext.current.resources
    val contenido = remember(resources, recurso.contenidoRes) {
        resources.openRawResource(recurso.contenidoRes).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
    Dialog(onDismissRequest = onCerrar) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(recurso.nombre, style = MaterialTheme.typography.titleLarge)
                Text(contenido, modifier = Modifier.weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()), style = MaterialTheme.typography.bodyLarge)
                TextButton(onClick = onCerrar, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cerrar recurso") }
            }
        }
    }
}
