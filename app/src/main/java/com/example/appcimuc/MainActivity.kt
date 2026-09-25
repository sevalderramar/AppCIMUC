package com.example.appcimuc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.appcimuc.ui.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // MaterialTheme entrega los estilos y colores
            // utilizados por los componentes de la aplicación.
            MaterialTheme {

                // Pantalla principal creada en HomeScreen.kt.
                HomeScreen()
            }
        }
    }
}
