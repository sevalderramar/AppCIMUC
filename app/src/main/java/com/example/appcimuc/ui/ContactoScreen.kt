package com.example.appcimuc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appcimuc.viewmodel.ContactoUiState

@Composable
fun ContactoFormularioScreen(
    estado: ContactoUiState,
    onNombre: (String) -> Unit, onCorreo: (String) -> Unit, onMensaje: (String) -> Unit,
    onRevisar: () -> Unit, onBack: () -> Unit
) {
    val foco = LocalFocusManager.current
    val teclado = LocalSoftwareKeyboardController.current
    val colores = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        errorContainerColor = MaterialTheme.colorScheme.surface
    )
    CatalogoLayout("Contacto", onBack) {
        val proyecto = estado.proyecto
        if (proyecto == null) {
            NoEncontrado("Proyecto")
        } else {
            Text(proyecto.titulo, style = MaterialTheme.typography.headlineMedium)
            Text("Demostración: utiliza datos ficticios. No se enviarán correos.")
            OutlinedTextField(
                value = estado.nombre, onValueChange = onNombre, modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") }, singleLine = true,
                isError = estado.errores.nombre != null,
                supportingText = { estado.errores.nombre?.let { Text(it) } },
                shape = MaterialTheme.shapes.small, colors = colores,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { foco.moveFocus(FocusDirection.Next) })
            )
            OutlinedTextField(
                value = estado.correo, onValueChange = onCorreo, modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") }, singleLine = true,
                isError = estado.errores.correo != null,
                supportingText = { estado.errores.correo?.let { Text(it) } },
                shape = MaterialTheme.shapes.small, colors = colores,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                    autoCorrectEnabled = false, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { foco.moveFocus(FocusDirection.Next) })
            )
            OutlinedTextField(
                value = estado.mensaje, onValueChange = onMensaje, modifier = Modifier.fillMaxWidth(),
                label = { Text("Mensaje") }, minLines = 4,
                isError = estado.errores.mensaje != null,
                supportingText = { estado.errores.mensaje?.let { Text(it) } },
                shape = MaterialTheme.shapes.small, colors = colores,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { teclado?.hide(); foco.clearFocus() })
            )
            AccionPrincipal("Revisar solicitud", onClick = onRevisar)
        }
    }
}

@Composable
fun ContactoResumenScreen(estado: ContactoUiState, onEditar: () -> Unit, onConfirmar: () -> Unit) {
    CatalogoLayout("Resumen", onEditar) {
        Text("Revisa tu solicitud", style = MaterialTheme.typography.headlineMedium)
        Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DatoResumen("Proyecto", estado.proyecto?.titulo.orEmpty())
                DatoResumen("Nombre", estado.nombre)
                DatoResumen("Correo electrónico", estado.correo)
                DatoResumen("Mensaje", estado.mensaje)
            }
        }
        Text("Esta operación es una demostración y no envía correos.")
        OutlinedButton(onClick = onEditar, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = MaterialTheme.shapes.small) { Text("Editar") }
        AccionPrincipal("Confirmar solicitud simulada", onClick = onConfirmar)
    }
}

@Composable
private fun DatoResumen(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ContactoConfirmacionScreen(onProyecto: () -> Unit, onExplorar: () -> Unit) {
    CatalogoLayout("Resultado", onProyecto) {
        Text("Solicitud simulada completada. No se envió un correo real",
            style = MaterialTheme.typography.headlineMedium)
        Text("Esta demostración no representa una solicitud recibida por CIM UC.")
        AccionPrincipal("Volver al proyecto", onClick = onProyecto)
        OutlinedButton(onClick = onExplorar, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = MaterialTheme.shapes.small) { Text("Volver a Explorar") }
    }
}
