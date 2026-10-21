package com.example.appcimuc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.example.appcimuc.ui.ContactoFormularioScreen
import com.example.appcimuc.ui.ContactoResumenScreen
import com.example.appcimuc.ui.ContactoConfirmacionScreen
import com.example.appcimuc.viewmodel.ContactoViewModel
import com.example.appcimuc.viewmodel.PasoContacto
import com.example.appcimuc.viewmodel.MainViewModel
import com.example.appcimuc.viewmodel.ParticipacionViewModel
import com.example.appcimuc.viewmodel.TipoParticipacion
import com.example.appcimuc.ui.ParticipacionModales

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Propietario Activity: catálogo estable durante todos los recorridos de este host.
    val mainViewModel: MainViewModel = viewModel()
    val teclado = LocalSoftwareKeyboardController.current
    val foco = LocalFocusManager.current
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val isPrincipal = currentRoute == Routes.INICIO || currentRoute == Routes.EXPLORAR
    val onPrincipal: (String) -> Unit = { route ->
        teclado?.hide()
        foco.clearFocus()
        if (navController.currentDestination?.route != route) {
            navController.navigate(route) {
                popUpTo(Routes.INICIO) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val onBack: () -> Unit = { navController.popBackStack() }
    // Si un enlace inverso apunta a un detalle ya abierto, recupera esa entrada
    // y su estado en lugar de añadir otra copia a la pila.
    val abrirDetalle: (String) -> Unit = { ruta ->
        teclado?.hide()
        foco.clearFocus()
        val existente = runCatching { navController.getBackStackEntry(ruta) }.getOrNull()
        if (existente != null) navController.popBackStack(ruta, false)
        else navController.navigate(ruta) { launchSingleTop = true }
    }
    val onProyecto: (String) -> Unit = { id ->
        abrirDetalle(Routes.proyecto(id))
    }

    // Las pantallas gestionan el área superior; la barra principal gestiona la inferior.
    Scaffold(modifier = Modifier.imePadding(), contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
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
                val estado by mainViewModel.explorarUiState.collectAsState()
                ExplorarScreen(
                    estado = estado,
                    onTipo = mainViewModel::seleccionarTipo,
                    onBuscar = mainViewModel::buscar,
                    onCategoria = mainViewModel::seleccionarCategoria,
                    onAnio = mainViewModel::seleccionarAnio,
                    onLimpiar = mainViewModel::limpiarExplorar,
                    onProyecto = onProyecto,
                    onCapacidad = { abrirDetalle(Routes.capacidad(it)) },
                    onIntegrante = { abrirDetalle(Routes.integrante(it)) },
                    onBack = onBack
                )
            }
            composable(Routes.PROYECTO, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                val proyecto = mainViewModel.proyecto(id)
                val participacionFactory = remember(entry) { ParticipacionViewModel.factory(id) }
                val participacionViewModel: ParticipacionViewModel = viewModel(
                    viewModelStoreOwner = entry, factory = participacionFactory)
                val participacion by participacionViewModel.uiState.collectAsState()
                DetalleProyectoScreen(
                    proyecto = proyecto,
                    categoria = proyecto?.let { mainViewModel.categoria(it.categoriaId) },
                    integrantes = mainViewModel.integrantes(id),
                    capacidades = mainViewModel.capacidades(id),
                    recursos = mainViewModel.recursos(id),
                    onIntegrante = { abrirDetalle(Routes.integrante(it)) },
                    onCapacidad = { abrirDetalle(Routes.capacidad(it)) },
                    onContactar = { proyectoId ->
                        if (navController.currentDestination?.route == Routes.PROYECTO) {
                            navController.navigate(Routes.contacto(proyectoId)) { launchSingleTop = true }
                        }
                    },
                    onUnirse = { proyectoId ->
                        if (proyectoId == id) participacionViewModel.abrir(TipoParticipacion.POSTULACION)
                    },
                    onAportar = { proyectoId ->
                        if (proyectoId == id) participacionViewModel.abrir(TipoParticipacion.APORTE)
                    },
                    onBack = onBack
                )
                ParticipacionModales(estado = participacion,
                    onCerrar = { teclado?.hide(); foco.clearFocus(); participacionViewModel.cerrar() },
                    onCampo = participacionViewModel::cambiar,
                    onEnviar = { tipo ->
                        if (participacionViewModel.enviar(tipo)) { teclado?.hide(); foco.clearFocus() }
                    })
            }
            composable(Routes.INTEGRANTE, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                DetalleIntegranteScreen(mainViewModel.integrante(id), mainViewModel.proyectosDeIntegrante(id), onProyecto, onBack)
            }
            composable(Routes.CAPACIDAD, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString(Routes.ID).orEmpty()
                DetalleCapacidadScreen(mainViewModel.capacidad(id), mainViewModel.proyectosDeCapacidad(id), onProyecto, onBack)
            }
            composable(Routes.CONTACTO, arguments = listOf(navArgument(Routes.ID) { type = NavType.StringType })) { entry ->
                val proyectoId = entry.arguments?.getString(Routes.ID).orEmpty()
                // Alcance explícito de la entrada del flujo, NO de la Activity ni de cada paso.
                // No hay SavedStateHandle con datos personales ni argumentos de borrador.
                val factory = remember(entry) { ContactoViewModel.factory(proyectoId) }
                val contactoViewModel: ContactoViewModel = viewModel(viewModelStoreOwner = entry, factory = factory)
                val estado by contactoViewModel.uiState.collectAsState()
                val ocultarTeclado = { teclado?.hide(); foco.clearFocus() }
                val volverProyecto: () -> Unit = {
                    // La única entrada anterior al flujo es el detalle que lo abrió.
                    // La guarda impide que una segunda pulsación quite también ese detalle.
                    if (navController.currentDestination?.route == Routes.CONTACTO) {
                        ocultarTeclado()
                        navController.popBackStack()
                    }
                }
                val editar: () -> Unit = { ocultarTeclado(); contactoViewModel.editar() }
                BackHandler(enabled = estado.paso != PasoContacto.FORMULARIO) {
                    if (estado.paso == PasoContacto.RESUMEN) editar() else volverProyecto()
                }
                when (estado.paso) {
                    PasoContacto.FORMULARIO -> ContactoFormularioScreen(
                        estado = estado,
                        onNombre = contactoViewModel::cambiarNombre,
                        onCorreo = contactoViewModel::cambiarCorreo,
                        onMensaje = contactoViewModel::cambiarMensaje,
                        onRevisar = { if (contactoViewModel.revisar()) ocultarTeclado() },
                        onBack = volverProyecto
                    )
                    PasoContacto.RESUMEN -> ContactoResumenScreen(
                        estado = estado, onEditar = editar,
                        onConfirmar = { ocultarTeclado(); contactoViewModel.confirmar() }
                    )
                    PasoContacto.CONFIRMACION -> ContactoConfirmacionScreen(
                        onProyecto = volverProyecto,
                        onExplorar = {
                            if (navController.currentDestination?.route == Routes.CONTACTO) {
                                ocultarTeclado()
                                if (!navController.popBackStack(Routes.EXPLORAR, false)) {
                                    // Entrada desde un destacado de Inicio: reutiliza Explorar si estaba guardado.
                                    // No guarda ni restaura la entrada del flujo de contacto finalizado.
                                    navController.navigate(Routes.EXPLORAR) {
                                        popUpTo(Routes.INICIO)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
