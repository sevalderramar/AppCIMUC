package com.example.appcimuc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcimuc.model.Proyecto

@Composable
fun ExplorarScreen(proyectos: List<Proyecto>, onProyecto: (String) -> Unit, onBack: () -> Unit) {
    CatalogoLayout("Explorar", onBack, scroll = false) {
        Text("Proyectos", style = MaterialTheme.typography.headlineMedium)
        Text("Búsqueda y filtros por categoría y año: pendientes de la siguiente etapa.")
        if (proyectos.isEmpty()) {
            Text("No hay proyectos disponibles en este catálogo.")
        } else {
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                // Equivalente local a Compact (<600), Medium (600–839) y Expanded (>=840 dp).
                val columnas = when {
                    maxWidth < 600.dp -> 1
                    maxWidth < 840.dp -> 2
                    else -> 3
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnas),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(proyectos, key = { it.id }) { proyecto ->
                        Card(onClick = { onProyecto(proyecto.id) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(proyecto.titulo, style = MaterialTheme.typography.titleLarge)
                                Text(proyecto.anio.toString(), style = MaterialTheme.typography.labelLarge)
                                Text(proyecto.descripcion)
                                Text("Ver proyecto", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
