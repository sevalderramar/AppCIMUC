package com.example.appcimuc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.appcimuc.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.appcimuc.ui.DetalleCapacidadScreen
import com.example.appcimuc.ui.DetalleIntegranteScreen
import com.example.appcimuc.ui.DetalleProyectoScreen
import com.example.appcimuc.ui.ExplorarScreen
import com.example.appcimuc.ui.HomeRoute
import com.example.appcimuc.viewmodel.MainViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Propietario Activity: catálogo estable durante todos los recorridos de este host.
    val mainViewModel: MainViewModel = viewModel()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val isPrincipal = currentRoute == Routes.INICIO || currentRoute == Routes.EXPLORAR
    val onPrincipal: (String) -> Unit = { route ->
        if (navController.currentDestination?.route != route) {
            navController.navigate(route) {
                popUpTo(Routes.INICIO) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val onBack: () -> Unit = { navController.popBackStack() }
    val onProyecto: (String) -> Unit = { id ->
        navController.navigate(Routes.proyecto(id)) { launchSingleTop = true }
    }

    // Las pantallas gestionan el área superior; la barra principal gestiona la inferior.
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
        if (isPrincipal) {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                listOf(Routes.INICIO to "Inicio", Routes.EXPLORAR to "Explorar").forEach { (route, title) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = { onPrincipal(route) },
                        icon = {
                            Icon(painterResource(if (route == Routes.INICIO) R.drawable.ic_inicio else R.drawable.ic_explorar),
                                contentDescription = null)
                        },
                        label = { Text(title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }) { padding ->
        NavHost(navController = navController, startDestination = Routes.INICIO,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)) {
            composable(Routes.INICIO) {
                HomeRoute(onExplore = { onPrincipal(Routes.EXPLORAR) },
                    mainViewModel = mainViewModel, onProyecto = onProyecto)
            }
            composable(Routes.EXPLORAR) {
                ExplorarScreen(mainViewModel.proyectos, onProyecto, onBack, mainViewModel.categorias)
            }
            composable(Routes.PROYECTO, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                val proyecto = mainViewModel.proyecto(id)
                DetalleProyectoScreen(
                    proyecto = proyecto,
                    categoria = proyecto?.let { mainViewModel.categoria(it.categoriaId) },
                    integrantes = mainViewModel.integrantes(id),
                    capacidades = mainViewModel.capacidades(id),
                    onIntegrante = { navController.navigate(Routes.integrante(it)) { launchSingleTop = true } },
                    onCapacidad = { navController.navigate(Routes.capacidad(it)) { launchSingleTop = true } },
                    onBack = onBack
                )
            }
            composable(Routes.INTEGRANTE, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                DetalleIntegranteScreen(mainViewModel.integrante(id), mainViewModel.proyectosDeIntegrante(id), onProyecto, onBack)
            }
            composable(Routes.CAPACIDAD, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                DetalleCapacidadScreen(mainViewModel.capacidad(id), mainViewModel.proyectosDeCapacidad(id), onProyecto, onBack)
            }
        }
    }
}
