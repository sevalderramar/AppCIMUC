package com.example.appcimuc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val onBack: () -> Unit = { navController.popBackStack() }
    val onProyecto: (String) -> Unit = { id ->
        navController.navigate(Routes.proyecto(id)) { launchSingleTop = true }
    }

    NavHost(navController = navController, startDestination = Routes.INICIO) {
        composable(Routes.INICIO) {
            HomeRoute(onExplore = {
                navController.navigate(Routes.EXPLORAR) { launchSingleTop = true }
            })
        }
        composable(Routes.EXPLORAR) {
            ExplorarScreen(mainViewModel.proyectos, onProyecto, onBack)
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
