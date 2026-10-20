package com.example.appcimuc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appcimuc.ui.navigation.AppNavHost
import com.example.appcimuc.ui.theme.AppNombreGrupoXTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // El tema existente entrega los estilos y colores
            // utilizados por los componentes de la aplicación.
            AppNombreGrupoXTheme {

                // Pantalla principal creada en HomeScreen.kt.
                AppNavHost()
            }
        }
    }
}
