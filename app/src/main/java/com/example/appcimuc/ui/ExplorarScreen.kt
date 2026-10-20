package com.example.appcimuc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcimuc.model.Proyecto
import com.example.appcimuc.model.Categoria

@Composable
fun ExplorarScreen(proyectos: List<Proyecto>, onProyecto: (String) -> Unit, onBack: () -> Unit,
    categorias: List<Categoria> = emptyList()) {
    CatalogoLayout("Explorar", onBack, scroll = false) {
        // El encabezado también se desplaza para no ocultar tarjetas en horizontal o con fuente grande.
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
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Proyectos", style = MaterialTheme.typography.headlineMedium)
                }
                if (proyectos.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("No hay proyectos disponibles en este catálogo.")
                    }
                }
                items(proyectos, key = { it.id }) { proyecto ->
                    ProyectoCard(proyecto, onProyecto, categorias.find { it.id == proyecto.categoriaId }?.nombre ?: "Proyecto")
                }
            }
        }
    }
}
