package com.example.appcimuc.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.appcimuc.model.AppInfo
import com.example.appcimuc.model.Categoria
import com.example.appcimuc.model.Proyecto
import com.example.appcimuc.ui.theme.AppNombreGrupoXTheme
import com.example.appcimuc.viewmodel.MainViewModel

// Lifecycle conserva el ViewModel en el propietario actual entre recomposiciones.
@Composable
fun HomeRoute(
    onExplore: () -> Unit,
    mainViewModel: MainViewModel = viewModel(),
    onProyecto: (String) -> Unit = {}
) {
    HomeScreen(
        appInfo = mainViewModel.appInfo,
        onExplore = onExplore,
        destacados = mainViewModel.destacados,
        totalProyectos = mainViewModel.totalProyectos,
        totalIntegrantes = mainViewModel.totalIntegrantes,
        totalCapacidades = mainViewModel.totalCapacidades,
        onProyecto = onProyecto,
        categorias = mainViewModel.categorias
    )
}

/*
 * Guía 8 - Pantalla principal de la aplicación.
 * Esta pantalla utiliza componentes básicos de Jetpack Compose:
 * Scaffold, TopAppBar, Column, Row, Text, Button e Image.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    appInfo: AppInfo, onExplore: () -> Unit,
    destacados: List<Proyecto> = emptyList(),
    totalProyectos: Int = 0, totalIntegrantes: Int = 0, totalCapacidades: Int = 0,
    onProyecto: (String) -> Unit = {},
    categorias: List<Categoria> = emptyList()
) {

    // Scaffold entrega una estructura base para la pantalla.
    Scaffold(

        // Barra ubicada en la parte superior de la aplicación.
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Añadir aquí la insignia original cuando se disponga del recurso verificado.
                    Text("CIM UC", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }

    ) { innerPadding ->

        // Column organiza los elementos verticalmente.
        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Column(Modifier.fillMaxWidth().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Centro de Innovación en Madera UC", style = MaterialTheme.typography.labelLarge)
                        Text(appInfo.title, style = MaterialTheme.typography.headlineMedium)
                        Text(appInfo.message,
                            style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = onExplore, modifier = Modifier.heightIn(min = 48.dp),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )) {
                            Text("Explorar proyectos →", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
                    // Tres tarjetas contiguas; en anchos mínimos se apilan para evitar recortes.
                    if (maxWidth >= 320.dp) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IndicadorCatalogo(totalProyectos, "Proyectos", Modifier.weight(1f))
                            IndicadorCatalogo(totalCapacidades, "Capacidades", Modifier.weight(1f))
                            IndicadorCatalogo(totalIntegrantes, "Integrantes", Modifier.weight(1f))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            IndicadorCatalogo(totalProyectos, "Proyectos", Modifier.fillMaxWidth())
                            IndicadorCatalogo(totalCapacidades, "Capacidades", Modifier.fillMaxWidth())
                            IndicadorCatalogo(totalIntegrantes, "Integrantes", Modifier.fillMaxWidth())
                        }
                    }
                }
                TituloSeccion("Proyectos destacados")
                if (destacados.isEmpty()) Text("No hay proyectos destacados disponibles.")
                destacados.forEach { proyecto ->
                    ProyectoCard(proyecto, onProyecto, categorias.find { it.id == proyecto.categoriaId }?.nombre ?: "Proyecto")
                }

                Text("Prototipo académico · Datos de demostración",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun IndicadorCatalogo(total: Int, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(total.toString(), style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}


/*
 * Preview permite visualizar la pantalla
 * sin ejecutar completamente la aplicación.
 */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppNombreGrupoXTheme {
        HomeScreen(
            appInfo = AppInfo(
                title = "Explora proyectos, capacidades e investigación",
                message = "Descubre el ecosistema de innovación del CIM UC y conecta con oportunidades de colaboración."
            ),
            onExplore = {}
        )
    }
}
