package com.example.appcimuc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogoLayout(
    titulo: String,
    onBack: () -> Unit,
    scroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(titulo) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.primary
            ),
            navigationIcon = { TextButton(onClick = onBack) {
                Text("Volver", color = MaterialTheme.colorScheme.primary)
            } }
        )
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            val scrollModifier = if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier
            Column(
                modifier = Modifier.widthIn(max = if (scroll) 840.dp else 1200.dp)
                    .fillMaxSize().then(scrollModifier).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Datos de demostración", style = MaterialTheme.typography.bodySmall)
                content()
            }
        }
    }
}

@Composable
internal fun NoEncontrado(tipo: String) {
    Text("$tipo no encontrado", style = MaterialTheme.typography.headlineSmall)
    Text("El identificador no existe en este catálogo. Usa Volver para regresar.")
}

@Composable
internal fun TituloSeccion(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
}
