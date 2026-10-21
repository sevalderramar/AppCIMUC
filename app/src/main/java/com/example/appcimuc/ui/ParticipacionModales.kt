package com.example.appcimuc.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.appcimuc.R
import com.example.appcimuc.ui.theme.ParticipacionEstilo
import com.example.appcimuc.viewmodel.*

private data class CampoModal(val campo: CampoParticipacion, val etiqueta: String,
    val placeholder: String, val multilinea: Boolean = false)

private fun camposModal(tipo: TipoParticipacion): List<CampoModal> = if (tipo == TipoParticipacion.POSTULACION) listOf(
    CampoModal(CampoParticipacion.NOMBRE, "Nombre completo", "Ej: Juan Pérez González"),
    CampoModal(CampoParticipacion.CORREO, "Correo electrónico", "correo@uc.cl"),
    CampoModal(CampoParticipacion.INSTITUCION, "Institución / Universidad", "Ej: Pontificia Universidad Católica"),
    CampoModal(CampoParticipacion.MOTIVACION, "Motivación y competencias relevantes",
        "Describe brevemente tu experiencia y por qué quieres unirte a este proyecto…", true)
) else listOf(
    CampoModal(CampoParticipacion.NOMBRE, "Tu nombre", "Nombre y apellido"),
    CampoModal(CampoParticipacion.CORREO, "Correo de contacto", "correo@ejemplo.com"),
    CampoModal(CampoParticipacion.ASUNTO, "Asunto / Tipo de información", "Ej: Datos de ensayo, Referencia bibliográfica…"),
    CampoModal(CampoParticipacion.INFORMACION, "Información / Aporte",
        "Describe la información que deseas entregar al equipo del proyecto…", true),
    CampoModal(CampoParticipacion.FUENTE, "Fuente o respaldo (opcional)", "URL, referencia bibliográfica, institución…", true)
)

@Composable
fun ParticipacionModales(
    estado: ParticipacionUiState,
    onCerrar: () -> Unit,
    onCampo: (TipoParticipacion, CampoParticipacion, String) -> Unit,
    onEnviar: (TipoParticipacion) -> Unit
) {
    val proyecto = estado.proyecto ?: return
    when (estado.modal) {
        ModalParticipacion.CERRADO -> Unit
        ModalParticipacion.POSTULACION -> HojaParticipacion(TipoParticipacion.POSTULACION, proyecto.titulo,
            estado.postulacion, onCerrar, onCampo, onEnviar)
        ModalParticipacion.APORTE -> HojaParticipacion(TipoParticipacion.APORTE, proyecto.titulo,
            estado.aporte, onCerrar, onCampo, onEnviar)
        ModalParticipacion.EXITO_POSTULACION -> ExitoParticipacion(TipoParticipacion.POSTULACION, onCerrar)
        ModalParticipacion.EXITO_APORTE -> ExitoParticipacion(TipoParticipacion.APORTE, onCerrar)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HojaParticipacion(
    tipo: TipoParticipacion, proyecto: String, formulario: FormularioParticipacion,
    onCerrar: () -> Unit, onCampo: (TipoParticipacion, CampoParticipacion, String) -> Unit,
    onEnviar: (TipoParticipacion) -> Unit
) {
    val esPostulacion = tipo == TipoParticipacion.POSTULACION
    val campos = remember(tipo) { camposModal(tipo) }
    val focos = remember(tipo) { campos.associate { it.campo to FocusRequester() } }
    val posiciones = remember(tipo) { campos.associate { it.campo to BringIntoViewRequester() } }
    var campoEnFoco by remember(tipo) { mutableStateOf<CampoParticipacion?>(null) }
    val foco = LocalFocusManager.current
    val teclado = LocalSoftwareKeyboardController.current
    val colorAccion = if (esPostulacion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetMaxWidth = 640.dp, dragHandle = null,
        contentWindowInsets = { WindowInsets.safeDrawing },
        shape = ParticipacionEstilo.formaModal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = Color.Black.copy(alpha = .5f)
    ) {
        // Material 3 1.3.1 aplica y consume IME en su ventana. safeDrawing protege
        // barras/cámara; no agregamos otro imePadding ni usamos la altura del teléfono.
        val alturaIme = WindowInsets.ime.getBottom(LocalDensity.current)
        LaunchedEffect(campoEnFoco, alturaIme) {
            campoEnFoco?.let {
                withFrameNanos { }
                posiciones.getValue(it).bringIntoView()
            }
        }
        LaunchedEffect(tipo, formulario.intento) {
            if (formulario.intento > 0) {
                val primero = campos.firstOrNull { it.campo in formulario.errores }?.campo
                if (primero != null) {
                    withFrameNanos { } // Espera de layout, no una simulación de red.
                    focos.getValue(primero).requestFocus()
                    posiciones.getValue(primero).bringIntoView()
                    teclado?.show()
                }
            }
        }
        // Las restricciones finitas de la ventana (ya sin IME/insets) acotan la hoja.
        // El encabezado se mide primero; solo el cuerpo usa el espacio restante.
        // fill=false permite ajustar al contenido cuando este es corto.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val margen = minOf(ParticipacionEstilo.padding, maxHeight * .04f)
            val separacion = minOf(20.dp, maxHeight * .03f)
            val alturaEncabezado = maxHeight * .5f
            Column(Modifier.fillMaxWidth().padding(margen),
                verticalArrangement = Arrangement.spacedBy(separacion)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    // En una ventana excepcionalmente baja el encabezado puede desplazarse
                    // dentro de su propia zona: no trunca texto ni deja el cuerpo con altura cero.
                    // La X permanece fuera de ambos scrolls.
                    Column(Modifier.weight(1f).heightIn(max = alturaEncabezado)
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (esPostulacion) "Solicitar unirse" else "Entregar información",
                            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text(proyecto, style = MaterialTheme.typography.bodyLarge)
                    }
                    IconButton(onClick = onCerrar, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
                        Icon(painterResource(R.drawable.ic_cerrar), contentDescription = "Cerrar formulario")
                    }
                }
                Column(Modifier.weight(1f, fill = false).fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Surface(shape = MaterialTheme.shapes.small,
                        color = if (esPostulacion) ParticipacionEstilo.avisoPostulacionFondo else ParticipacionEstilo.avisoAporteFondo,
                        contentColor = if (esPostulacion) ParticipacionEstilo.avisoPostulacionTexto else MaterialTheme.colorScheme.primary,
                        border = BorderStroke(1.dp, if (esPostulacion) ParticipacionEstilo.avisoPostulacionBorde else ParticipacionEstilo.avisoAporteBorde)) {
                        Text(if (esPostulacion)
                            "En un proceso real, el líder del proyecto y un administrador revisarían tu solicitud. Esta es una simulación académica: no se ejecuta una evaluación institucional ni se enviarán datos o correos reales."
                            else "En un proceso real, el aporte se evaluaría por su veracidad y pertinencia. Esta es una simulación académica: no se realiza una evaluación institucional ni se enviarán datos o correos reales.",
                            Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                    campos.forEachIndexed { indice, campo ->
                        val ultimo = indice == campos.lastIndex
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(campo.etiqueta, style = MaterialTheme.typography.labelLarge)
                            OutlinedTextField(
                                value = formulario.valores[campo.campo].orEmpty(),
                                onValueChange = { onCampo(tipo, campo.campo, it) },
                                modifier = Modifier.fillMaxWidth().focusRequester(focos.getValue(campo.campo))
                                    .bringIntoViewRequester(posiciones.getValue(campo.campo))
                                    .onFocusChanged {
                                        if (it.isFocused) campoEnFoco = campo.campo
                                        else if (campoEnFoco == campo.campo) campoEnFoco = null
                                    }
                                    .semantics { contentDescription = campo.etiqueta },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                placeholder = { Text(campo.placeholder) },
                                isError = campo.campo in formulario.errores,
                                supportingText = { formulario.errores[campo.campo]?.let { Text(it) } },
                                singleLine = !campo.multilinea, minLines = if (campo.multilinea) 3 else 1,
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    errorContainerColor = MaterialTheme.colorScheme.background,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor = ParticipacionEstilo.placeholder,
                                    unfocusedPlaceholderColor = ParticipacionEstilo.placeholder
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = if (campo.campo == CampoParticipacion.CORREO) KeyboardType.Email else KeyboardType.Text,
                                    capitalization = if (campo.campo == CampoParticipacion.CORREO) KeyboardCapitalization.None else KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = campo.campo != CampoParticipacion.CORREO,
                                    imeAction = if (ultimo) ImeAction.Done else ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { foco.moveFocus(FocusDirection.Next) },
                                    onDone = { teclado?.hide(); foco.clearFocus() }
                                )
                            )
                        }
                    }
                    Button(onClick = { onEnviar(tipo) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = ParticipacionEstilo.formaBoton,
                        colors = ButtonDefaults.buttonColors(containerColor = colorAccion, contentColor = Color.White)) {
                        Text(if (esPostulacion) "Enviar solicitud" else "Enviar información", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExitoParticipacion(tipo: TipoParticipacion, onCerrar: () -> Unit) {
    val esPostulacion = tipo == TipoParticipacion.POSTULACION
    val color = if (esPostulacion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Dialog(onDismissRequest = onCerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val ventana = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { ventana?.setDimAmount(.5f) }
        Surface(modifier = Modifier.padding(24.dp).widthIn(max = 480.dp),
            shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(ParticipacionEstilo.padding),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Surface(shape = CircleShape, color = color.copy(alpha = .12f)) {
                    Icon(painterResource(R.drawable.ic_verificado), contentDescription = null, tint = color,
                        modifier = Modifier.padding(20.dp).size(32.dp))
                }
                Text(if (esPostulacion) "¡Solicitud simulada registrada!" else "¡Información registrada en la simulación!",
                    style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, color = color)
                Text("No se enviaron datos ni correos. La evaluación institucional no se ejecuta realmente.",
                    style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Button(onClick = onCerrar, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = ParticipacionEstilo.formaBoton,
                    colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White)) {
                    Text("Entendido", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
