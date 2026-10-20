package com.example.appcimuc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.appcimuc.R
import com.example.appcimuc.viewmodel.ExplorarUiState
import com.example.appcimuc.viewmodel.TipoContenido
import kotlinx.coroutines.launch

@Composable
fun ExplorarScreen(
    estado: ExplorarUiState,
    onTipo: (TipoContenido) -> Unit,
    onBuscar: (String) -> Unit,
    onCategoria: (String?) -> Unit,
    onAnio: (Int?) -> Unit,
    onLimpiar: () -> Unit,
    onProyecto: (String) -> Unit,
    onCapacidad: (String) -> Unit,
    onIntegrante: (String) -> Unit,
    onBack: () -> Unit
) {
    // rememberLazyGridState utiliza Saver: una posición independiente por tipo, restaurable
    // en la entrada de Navigation al regresar de detalle, cambiar de pestaña o rotar.
    val proyectosScroll = rememberLazyGridState()
    val capacidadesScroll = rememberLazyGridState()
    val integrantesScroll = rememberLazyGridState()
    val scroll = when (estado.tipo) {
        TipoContenido.PROYECTOS -> proyectosScroll
        TipoContenido.CAPACIDADES -> capacidadesScroll
        TipoContenido.INTEGRANTES -> integrantesScroll
    }
    val scope = rememberCoroutineScope()
    val teclado = LocalSoftwareKeyboardController.current
    val foco = LocalFocusManager.current
    val ocultarTeclado = { teclado?.hide(); foco.clearFocus() }
    val reiniciarPosicion: () -> Unit = { scope.launch { scroll.scrollToItem(0) } }

    CatalogoLayout("Explorar", { ocultarTeclado(); onBack() }, scroll = false) {
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
                state = scroll,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "controles", span = { GridItemSpan(maxLineSpan) }) {
                    ControlesExplorar(
                        estado = estado,
                        onTipo = { ocultarTeclado(); onTipo(it) },
                        onBuscar = { onBuscar(it); reiniciarPosicion() },
                        onCategoria = { ocultarTeclado(); onCategoria(it); reiniciarPosicion() },
                        onAnio = { ocultarTeclado(); onAnio(it); reiniciarPosicion() },
                        onLimpiar = { ocultarTeclado(); onLimpiar(); reiniciarPosicion() }
                    )
                }
                if (estado.cantidadResultados == 0) {
                    item(key = "vacio", span = { GridItemSpan(maxLineSpan) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(if (estado.catalogoVacio) "No hay contenidos disponibles de este tipo."
                                else "No encontramos coincidencias. Prueba con otra búsqueda o limpia los filtros.")
                            if (!estado.catalogoVacio) {
                                OutlinedButton(onClick = { ocultarTeclado(); onLimpiar(); reiniciarPosicion() },
                                    modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text("Limpiar búsqueda y filtros")
                                }
                            }
                        }
                    }
                }
                when (estado.tipo) {
                    TipoContenido.PROYECTOS -> items(estado.proyectos, key = { "proyecto-${it.id}" }) { proyecto ->
                        ProyectoCard(proyecto, { ocultarTeclado(); onProyecto(it) },
                            estado.categorias.find { it.id == proyecto.categoriaId }?.nombre ?: "Proyecto")
                    }
                    TipoContenido.CAPACIDADES -> items(estado.capacidades, key = { "capacidad-${it.id}" }) { capacidad ->
                        EntidadCatalogoCard("Capacidad I+D", capacidad.nombre, capacidad.descripcion, "Ver capacidad") {
                            ocultarTeclado(); onCapacidad(capacidad.id)
                        }
                    }
                    TipoContenido.INTEGRANTES -> items(estado.integrantes, key = { "integrante-${it.id}" }) { integrante ->
                        EntidadCatalogoCard("Integrante", integrante.nombre, integrante.descripcion, "Ver integrante") {
                            ocultarTeclado(); onIntegrante(integrante.id)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ControlesExplorar(
    estado: ExplorarUiState, onTipo: (TipoContenido) -> Unit, onBuscar: (String) -> Unit,
    onCategoria: (String?) -> Unit, onAnio: (Int?) -> Unit, onLimpiar: () -> Unit
) {
    val teclado = LocalSoftwareKeyboardController.current
    val foco = LocalFocusManager.current
    val nombreTipo = when (estado.tipo) {
        TipoContenido.PROYECTOS -> "proyectos"
        TipoContenido.CAPACIDADES -> "capacidades"
        TipoContenido.INTEGRANTES -> "integrantes"
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tipo de contenido", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OpcionExplorar("Proyectos", estado.tipo == TipoContenido.PROYECTOS) { onTipo(TipoContenido.PROYECTOS) }
            OpcionExplorar("Capacidades", estado.tipo == TipoContenido.CAPACIDADES) { onTipo(TipoContenido.CAPACIDADES) }
            OpcionExplorar("Integrantes", estado.tipo == TipoContenido.INTEGRANTES) { onTipo(TipoContenido.INTEGRANTES) }
        }
        OutlinedTextField(
            value = estado.texto, onValueChange = onBuscar, modifier = Modifier.fillMaxWidth(),
            singleLine = true, label = { Text("Buscar $nombreTipo") },
            supportingText = { Text(if (estado.tipo == TipoContenido.PROYECTOS) "Título o descripción" else "Nombre o descripción") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_explorar), contentDescription = null) },
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { teclado?.hide(); foco.clearFocus() })
        )
        if (estado.tipo == TipoContenido.PROYECTOS) {
            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionExplorar("Todos", estado.categoriaId == null) { onCategoria(null) }
                estado.categorias.forEach { categoria ->
                    OpcionExplorar(categoria.nombre, estado.categoriaId == categoria.id) { onCategoria(categoria.id) }
                }
            }
            Text("Año", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionExplorar("Todos", estado.anio == null) { onAnio(null) }
                estado.anios.forEach { anio -> OpcionExplorar(anio.toString(), estado.anio == anio) { onAnio(anio) } }
            }
        }
        Text("${estado.cantidadResultados} ${if (estado.cantidadResultados == 1) "resultado" else "resultados"}",
            style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = onLimpiar, enabled = estado.tieneBusquedaOFiltros,
            modifier = Modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.small) {
            Text("Limpiar búsqueda y filtros")
        }
    }
}

@Composable
private fun OpcionExplorar(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(selected = seleccionado, onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.small,
        label = { Text(texto) }, colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ))
}
