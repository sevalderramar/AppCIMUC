# Estado temporal, restaurable y preferencias — 2026-10-21

## Fuentes y alcance

Se conservan los cambios de preferencias que ya estaban modificados/no seguidos al comenzar (Gradle, DataStore, VM, UI, pruebas y documentos). No se revierten ni se sustituyen. AGENTS y la implementación real se revisaron antes de editar. La guía docente de semana 9 sigue ausente de esta copia: no se inventa su contenido ni se certifica correspondencia curricular. El alcance procede de la solicitud del usuario.

## Tabla de estados

| Estado | Propietario | Mecanismo / contenido | Duración y límite |
|---|---|---|---|
| Tipo de contenido | MainViewModel de Activity | SavedStateHandle: nombre de enum validado | Recomposición, rotación y restauración de la misma tarea si Android dispone de snapshot |
| Tres consultas independientes | MainViewModel de Activity | SavedStateHandle: tres String, máximo 500 caracteres cada una | Igual que la tarea; no son preferencias ni historial permanente |
| Categoría/año de la tarea | MainViewModel de Activity | SavedStateHandle: ID String nullable y año Int nullable | Mismo estado restaurable; prevalece sobre DataStore incluso si representa Todos |
| Presencia de snapshot de Explorar | MainViewModel de Activity | Boolean `explorar.presente` | Diferencia tarea restaurada de inicio sin entradas; no es un evento de envío |
| Resultados, opciones, contadores y entidades de catálogo | Repositorio/MainViewModel | Datos en memoria, recalculados desde entradas/IDs | Se resuelven de nuevo en cada VM; no se serializan en SavedStateHandle |
| Posición de las tres cuadrículas | Composición de Explorar / entrada Navigation | Tres rememberLazyGridState, con Saver de índice/offset Int | Recomposición, rotación, regreso y snapshot de tarea según SaveableStateRegistry. Sin segunda copia en VM/DataStore; LazyGrid ajusta posiciones al contenido disponible |
| Destino/pila e IDs | NavController y entradas | rememberNavController/Navigation guardan rutas e IDs pequeños | Restauración de la tarea, no historial permanente. IDs inexistentes muestran «no encontrado» al consultar repositorio |
| Recurso abierto y scroll de detalle | Composición de detalle | ID de recurso con rememberSaveable y ScrollState con su Saver | Snapshot de entrada/tarea; recurso se resuelve de la lista actual. Sin texto del documento ni imágenes serializadas |
| Nombre/correo/mensaje, errores y Formulario/Resumen/Resultado de contacto | ContactoViewModel de la entrada `contacto/{id}` | StateFlow solo en memoria; una instancia para los tres pasos | Rotación mientras viva la entrada. Nuevo proceso → formulario vacío para el mismo ID. Salir elimina la entrada; confirmar borra campos. No se guardan datos ni paso/eventos en SavedStateHandle |
| Borradores/errores de postulación y aporte, formulario/confirmación | ParticipacionViewModel de la entrada del proyecto | StateFlow solo en memoria, estados separados | Rotación mientras viva el detalle. Cerrar limpia ambos borradores; nuevo proceso → modal cerrado y campos vacíos. Confirmación no se restaura como evento |
| Foco, teclado, solicitudes BringIntoView | Composición/ventana de formulario | remember y comportamiento nativo | Temporales; no se persisten. Perder foco no implica perder borrador del VM |
| Carga/guardado/error/reintento de preferencias | MainViewModel | UiState en memoria; coroutine viewModelScope | Rotación con VM. Nuevo proceso vuelve a leer; no se restaura una escritura pendiente ni un falso éxito |
| Recordar filtros y categoría/año persistidos | PreferenciasFiltrosRepository / archivo privado único | Preferences DataStore, tres claves autorizadas | Sobrevive inicio nuevo, cierre forzado y eliminación de tarea mientras exista el archivo de la app. No se promete continuidad tras desinstalar/reinstalar o borrar datos; no se probó ninguna de esas operaciones |

## Fuente de verdad y precedencia

1. **Dentro de una tarea restaurada**, SavedStateHandle es la entrada autoritativa de Explorar. MainViewModel expone una proyección StateFlow de solo lectura con resultados calculados; no hay otro formulario de filtros editable en paralelo. Todos los eventos se aplican sobre las entradas del handle, lo actualizan y recalculan la proyección desde el repositorio.
2. **Inicio nuevo sin snapshot:** se lee DataStore. Recordar activado → categoría/año válidos del archivo; desactivado → Todos. Las consultas comienzan vacías y el tipo inicial es Proyectos.
3. **Edición antes de terminar la lectura:** se conserva la selección de sesión. Una lectura tardía no la sustituye. La carga de preferencias no escribe defaults ni sobreescribe el archivo con el snapshot de una tarea; solo una edición explícita de filtros puede provocar el guardado posterior, si recordar corresponde.
4. **Valores obsoletos:** categoría/año se validan por separado contra las opciones de proyectos actuales. Un filtro inválido → Todos, sin perder el otro válido. Tipo inexistente → Proyectos; consultas con tipo inesperado → vacías. No se cae a filtros de DataStore para sustituir un snapshot de tarea inválido: se usa su fallback seguro.

Los siete valores del handle son String/Int/Boolean/null. El límite de 500 caracteres de consulta se aplica tanto a la entrada visible como a la restaurada, evitando dos valores distintos y manteniendo el snapshot pequeño. No se guarda ExplorarUiState completo, listas, imágenes, proyectos, personas, solicitudes ni datos de los formularios.

La factory de MainViewModel utiliza `CreationExtras.createSavedStateHandle()` del propietario Activity existente. No se crea manualmente un handle vacío en la ruta normal de Android: ese fallback queda únicamente para invocaciones legacy de la factory sin extras. No se añadieron dependencias: Lifecycle savedstate ya era transitivo en la base.

## Formularios y seguridad de Resumen

No se añadió restauración de datos personales. Contacto sigue teniendo una sola ruta: Formulario/Resumen/Confirmación son pasos del VM, no tres destinos restaurables. Android puede restaurar `contacto/{id}`, pero el VM nuevo arranca en Formulario vacío y consulta el proyecto por ID. Por ello no aparece un Resumen vacío restaurado ni se repite el envío. La validación de `confirmarContacto` ya devuelve a Formulario si recibe un resumen sin datos válidos y se conserva esa defensa.

Postulación/aporte conservan el VM por detalle y borradores durante rotación; tras muerte del proceso los modales quedan cerrados. Una confirmación en memoria es una condición de UI, no un evento guardado/reproducido. Se mantienen las guardas contra doble confirmación, cierre limpio y ausencia de cambios en cupos/integrantes/envíos reales.

## Cambios de esta continuación

- Nuevo `app/src/main/java/com/example/appcimuc/viewmodel/ExplorarEstadoGuardado.kt`: lectura/escritura de siete entradas pequeñas, validación y límite de consulta.
- `MainViewModel.kt`: SavedStateHandle inyectado desde CreationExtras, snapshot previo a leer preferencias, prioridad de tarea/edición sobre DataStore y proyección calculada. Se conservan el repositorio de preferencias y su manejo de errores existentes.
- `PreferenciasFiltrosUiState.kt`: nombre/comentario del parámetro de la política aclara que conservar el estado aplica a tarea restaurada o edición, sin cambiar sus demás reglas.
- `ui/ExplorarScreen.kt`: solo comentario que documenta el Saver de posición existente; no se reemplazó ni se duplicó ese mecanismo.
- Nuevo `docs/validacion/estado/EstadosChecks.kt`. Runner existente de preferencias ampliado para aceptar otro harness y usar los AAR savedstate/Lifecycle ya presentes, sin descargas/dependencias nuevas. README y DevLog ampliados sin borrar historial.
- Sin cambios en campos/diseño, navegación pública, modelos de dominio, datos, formularios, recursos, SDK, versiones o dependencia DataStore existente. Sin commits/push ni eliminación inesperada de archivos.

## Verificación ejecutada y límites

- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: **BUILD SUCCESSFUL**. Lint **0 errores / 9 advertencias** conocidas. UnitTest **NO-SOURCE**: no hay suite integrada JUnit/Compose, no se afirma lo contrario.
- **31 comprobaciones JVM nuevas** con SavedStateHandle y VMs reales: snapshot pequeño, precedencia aun con Todos o recordar apagado, lectura tardía, filtro/tipo/valor inválido, consultas independientes y acotadas, datos por ID, nuevos VMs de formularios vacíos, resumen inválido seguro y confirmaciones no repetidas. Se pasa una copia de valores a un nuevo handle/VM: **no es una serialización Bundle del sistema ni una muerte real del proceso**.
- Regresiones ejecutadas: **46** checks de preferencias (incluyen DataStore real en archivos temporales), **58** de exploración/contacto y **86** de detalle/participación/recursos. Correctas; cuentan casos parcialmente superpuestos, no cobertura UI independiente.
- APK instalada mediante `adb install -r`: Success, sin desinstalar/borrar datos. MainActivity llegó al frente y se obtuvo PID. Después AppCIMUC dejó de estar al frente; el helper temporal `EstadoDevice_20261021.ps1` detuvo la lectura/interacción. No se completaron casos de UI ni se hizo kill/force-stop, rotación o modificación de ajustes en esta prueba. La instalación, PID y los checks JVM **no prueban los cinco escenarios Android**.
- `git diff --check` sin errores; cambios anteriores de DataStore conservados. No se borraron datos ni se usó «No conservar actividades».

| Escenario solicitado | Evidencia ejecutada | Estado real de prueba Android |
|---|---|---|
| 1. Recomposición | Código: VM estable y entradas autoritativas, sin escritura por recomposición | Pendiente de ejecutar interacción completa |
| 2. Rotación/recreación de Activity | Propietarios revisados; mismo VM retiene estado, Saver de Compose existente | Pendiente; no confundir con crear otro VM en JVM |
| 3. Navegar a detalle y regresar | Pila/SaveableStateHolder/Saver revisados, IDs/relaciones en JVM | Pendiente de verificar valores y posición en pantalla |
| 4. Proceso nuevo con tarea restaurada | 31 checks incluyen nueva instancia de handle/VM con snapshot | Pendiente Android: no se ejecutó muerte de proceso real |
| 5. Inicio nuevo con preferencias | DataStore real reabierto y nuevos VMs sin snapshot en JVM | Pendiente inicio sin snapshot de tarea en celular |

Repetir harness desde la raíz después de compilar:

```powershell
& ./docs/validacion/preferencias/EjecutarChecks.ps1
& ./docs/validacion/preferencias/EjecutarChecks.ps1 -Prueba docs/validacion/estado/EstadosChecks.kt -Clase EstadosChecksKt
```

## Pasos manuales separados, sin borrar datos ni desinstalar

Usar solo consultas y borradores ficticios; detener cualquier automatización si AppCIMUC no está al frente. La APK debug ya se instaló; reinstalar con `install -r` solo si se necesita actualizarla, no desinstalar. Esperar a que termine Guardando antes de evaluar preferencias.

### 1. Recomposición
Elegir filtros de Proyectos y una consulta en cada tipo. Escribir/corregir la consulta y mostrar/ocultar teclado para provocar actualizaciones de UI: los otros valores no deben reiniciarse. Esto no es recreación de Activity ni prueba de proceso. No debe haber un envío o guardado de filtros provocado únicamente por recomposición.

### 2. Rotación / recreación de Activity
Con selección, consultas y posición, rotar físicamente el teléfono y regresar a vertical. Revisar cada tipo. Repetir con contacto en Formulario, con errores y en Resumen, y con borradores/errores de ambos modales. Deben conservarse mientras viva su VM de entrada; confirmar no debe repetirse. **No usar «No conservar actividades» como evidencia de muerte de proceso**: solo fuerza recreación de Activity.

### 3. Navegación y regreso
En cada tipo dejar consulta y posición reconocible; abrir un detalle y volver por Volver/Atrás. Consultas, filtros y posición deben conservarse. Recorrer chips/equipo/proyectos inversos y regresar; entradas existentes no deben duplicarse. Desactivar recordar no debe limpiar los filtros de esa tarea.

### 4. Muerte del proceso con tarea conservada (si el dispositivo lo permite)
1. Con recordar apagado, dejar tipo, consultas y filtros reconocibles; desplazar. Opcionalmente abrir un detalle o el Resumen de contacto con datos ficticios para verificar su reinicio seguro.
2. Anotar el PID (`adb shell pidof com.example.appcimuc`) y enviar la app a Inicio con HOME para que Android alcance onStop/guarde snapshot. **No quitar la tarjeta de Recientes, no force-stop, no borrar datos**.
3. Ya en segundo plano, ejecutar únicamente `adb shell am kill com.example.appcimuc`. Comprobar que el PID desaparece. Si el OEM no lo termina, la prueba no se realizó; no sustituirla por «No conservar actividades» ni afirmar éxito.
4. Reabrir la **misma tarjeta de Recientes**. Confirmar un PID nuevo y que la tarea se recupera. Al volver a Explorar deben aparecer sus valores y posición aun con recordar apagado. Los catálogos se consultan de nuevo; IDs obsoletos deben manejarse de forma segura.
5. Si se restauró `contacto/{id}`, debe mostrarse el formulario vacío del proyecto, no Resumen con datos faltantes ni Resultado. Los modales de participación deben quedar cerrados; abrirlos muestra campos vacíos. No debe repetirse ninguna operación.

El sistema/OEM puede no conservar la tarea o su snapshot. Un fallo de ese requisito previo no acredita que se haya probado restauración de proceso. Para Android, escribir en SavedStateHandle sin una posterior fase de guardado/onStop tampoco garantiza recuperar el último valor.

### 5. Inicio nuevo sin estado restaurable
Activar recordar, elegir Materiales/2026 y esperar guardado. Escribir una consulta distinta. Quitar **solo la tarjeta de esta tarea de Recientes** y abrir desde el icono, sin borrar datos de la app: se espera Proyectos, búsqueda vacía y filtros de DataStore. Esto es inicio nuevo, **no restauración de tarea tras muerte de proceso**. Repetir desactivando recordar: el inicio sin snapshot usa Todos. Cierre forzado/eliminación de tarea no prometen el estado restaurable; desinstalar/reinstalar no es parte de la prueba y puede borrar/restaurar archivos según políticas del sistema.

## Defensa oral

SavedStateHandle guarda entradas pequeñas de la tarea; el VM calcula la UI desde ellas y el repositorio. Compose guarda únicamente posiciones donde es su propietario. DataStore es la preferencia entre inicios nuevos y no sustituye una tarea restaurada. Los formularios son temporales: rotación conserva VM, proceso nuevo permite borrador vacío y nunca reproduce eventos de envío.
