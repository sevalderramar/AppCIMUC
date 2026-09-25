package com.example.appcimuc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.appcimuc.R

/*
 * Guía 8 - Pantalla principal de la aplicación.
 *
 * Esta pantalla utiliza componentes básicos de Jetpack Compose:
 * Scaffold, TopAppBar, Column, Row, Text, Button e Image.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    // Scaffold entrega una estructura base para la pantalla.
    Scaffold(

        // Barra ubicada en la parte superior de la aplicación.
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mi App Kotlin")
                }
            )
        }

    ) { innerPadding ->

        // Column organiza los elementos verticalmente.
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),

            // Espacio uniforme entre los elementos.
            verticalArrangement = Arrangement.spacedBy(20.dp),

            // Centra los elementos horizontalmente.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Texto principal.
            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Texto secundario.
            Text(
                text = "Aplicación desarrollada con Jetpack Compose",
                style = MaterialTheme.typography.bodyLarge
            )

            // Botón de ejemplo.
            Button(
                onClick = {
                    // Aquí se podrá agregar una acción posteriormente.
                }
            ) {
                Text(text = "Presióname")
            }

            // Imagen cargada desde res/drawable/logo.xml.
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            // Row organiza elementos horizontalmente.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    text = "Inicio",
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Información",
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
    MaterialTheme {
        HomeScreen()
    }
}
