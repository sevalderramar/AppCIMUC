package com.example.appcimuc.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.appcimuc.model.Capacidad
import com.example.appcimuc.model.Categoria
import com.example.appcimuc.model.Integrante
import com.example.appcimuc.model.Proyecto

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun DetalleProyectoScreen(
    proyecto: Proyecto?,
    categoria: Categoria?,
    integrantes: List<Integrante>,
    capacidades: List<Capacidad>,
    onIntegrante: (String) -> Unit,
    onCapacidad: (String) -> Unit,
    onContactar: (String) -> Unit,
    onUnirse: (String) -> Unit,
    onAportar: (String) -> Unit,
    onBack: () -> Unit
) {
    CatalogoLayout("Detalle del proyecto", onBack) {
        if (proyecto == null) {
            NoEncontrado("Proyecto")
        } else {
            val nombreCategoria = categoria?.nombre ?: "Categoría no disponible"
            val imagen = proyecto.imagenRes
            if (imagen != null) {
                Box(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large)) {
                    Image(painterResource(imagen), contentDescription = "Imagen del proyecto ${proyecto.titulo}",
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentScale = ContentScale.Crop)
                    Box(Modifier.matchParentSize().background(Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = .65f)))))
                    Surface(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                        color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small) {
                        Text(nombreCategoria, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                // Sin fotografía local disponible: no se inventa una imagen ni se reserva un gran hueco vacío.
                Surface(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small) {
                    Text(nombreCategoria, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                }
            }
            // Bloques separados: el título nunca queda debajo de una etiqueta superpuesta.
            Text(proyecto.titulo, style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.small) {
                Text(proyecto.estado, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge)
            }
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IndicadorProyecto("Año", proyecto.anio.toString())
                IndicadorProyecto("Disponibilidad", proyecto.cuposDisponibles?.let {
                    "$it ${if (it == 1) "cupo" else "cupos"}"
                } ?: "No informada")
                IndicadorProyecto("Integrantes", integrantes.size.toString())
            }
            TituloSeccion("Descripción")
            Text(proyecto.descripcion, style = MaterialTheme.typography.bodyLarge)
            TituloSeccion("Capacidades I+D")
            if (capacidades.isEmpty()) Text("Sin capacidades relacionadas.")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                capacidades.forEach { capacidad ->
                    AssistChip(onClick = { onCapacidad(capacidad.id) }, label = { Text(capacidad.nombre) },
                        modifier = Modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.small,
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary))
                }
            }
            TituloSeccion("Equipo")
            if (integrantes.isEmpty()) Text("Sin integrantes relacionados.")
            integrantes.forEach { integrante ->
                IntegranteProyectoCard(integrante, onClick = { onIntegrante(integrante.id) })
            }
            TituloSeccion("Recursos asociados")
            Text("Sin recursos disponibles")
            TituloSeccion("Participa en este proyecto")
            Button(onClick = { onUnirse(proyecto.id) }, enabled = proyecto.puedePostular,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary)) {
                Text("Solicitar unirse al proyecto")
            }
            if (!proyecto.puedePostular) {
                Text(when {
                    proyecto.aceptaPostulaciones == false -> "Este proyecto no acepta postulaciones."
                    proyecto.aceptaPostulaciones == null || proyecto.cuposDisponibles == null -> "Disponibilidad de postulación no informada."
                    else -> "No hay cupos disponibles."
                }, style = MaterialTheme.typography.bodySmall)
            }
            AccionPrincipal("Entregar información al proyecto", onClick = { onAportar(proyecto.id) })
            OutlinedButton(onClick = { onContactar(proyecto.id) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium) {
                Text("Contactar sobre este proyecto")
            }
        }
    }
}

@Composable
private fun IndicadorProyecto(etiqueta: String, valor: String) {
    Surface(modifier = Modifier.widthIn(min = 120.dp), color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(valor, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun IntegranteProyectoCard(integrante: Integrante, onClick: () -> Unit) {
    val iniciales = integrante.nombre.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        .take(2).joinToString("") { it.take(1).uppercase(Locale.ROOT) }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).clearAndSetSemantics { },
                shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary) {
                Box(Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                    Text(iniciales, style = MaterialTheme.typography.titleMedium)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(integrante.nombre, style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
                if (integrante.descripcion.isNotBlank()) Text(integrante.descripcion, style = MaterialTheme.typography.bodyLarge)
                Text("Ver integrante", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
