# Auditoría del MVP — 2026-10-21

## Alcance y fuentes

Se revisaron el código actual, AGENTS, README/DevLog completos, Gradle, los cuatro DOCX de `Avance proyecto CIM UC` y su modelo PNG. Git estaba limpio al inicio. No se asume implementación por menciones históricas ni se deduce una semana de comentarios como «Guía 8» en el código.

- **Historias del equipo:** HU1 búsqueda/filtros; HU2 detalle/relaciones; HU3 contacto simulado; HU4 navegación/legibilidad. Se contrastan con la muestra MVP de proyectos, capacidades e integrantes, no con contenidos adicionales fuera del alcance solicitado.
- **Propuestas del equipo:** aporte y postulación, sujetos a evaluación en un proceso real. Se conserva únicamente su simulación autorizada, sin registro ni administración.
- **Material docente:** no se encontraron guías, rúbricas, README docente ni materiales identificables de semanas 6, 7 u 8 en esta copia, incluidos archivos ocultos. Los documentos del equipo no son sustitutos de esas fuentes. **No se certifica cumplimiento curricular por semana.** Pendiente aportar originales para contrastarlos, sin implementar requisitos supuestos.
- **Diseño:** no existe `docs/diseno/Revista musical independiente.make`, ni insignia original ni fotografías de proyectos. Se conservan paleta, fuentes locales, licencias, iconos y recursos existentes. Los hashes de las cinco fuentes coinciden con `../historia2/Fuentes.md`. No se declara recuperación del prototipo ni fidelidad exacta al Figma.

## Matriz requisito → archivo → implementación → verificación → pendiente

Las rutas Kotlin de la tabla son relativas a `app/src/main/java/com/example/appcimuc/`. **Código** significa lectura estática, no interacción ejecutada. **JVM** significa harness auxiliar sobre las clases compiladas reales, no Compose ni lifecycle Android.

| Requisito | Archivo | Implementado | Verificado | Pendiente |
|---|---|---|---|---|
| UI representa estado y emite callbacks | `ui/ExplorarScreen.kt`, `ContactoScreen.kt`, `ParticipacionModales.kt`, detalles | Sí; pantallas sin validadores ni consultas de dominio al repositorio | Código | Interacción de callbacks en celular |
| Validaciones, filtrado y transiciones fuera de composables | `viewmodel/ContactoUiState.kt`, `ParticipacionUiState.kt`, `ExplorarUiState.kt` y sus ViewModels | Sí; funciones puras invocadas desde VM | Código y JVM | Eventos concurrentes/Android |
| Repositorio proporciona catálogo y relaciones | `repository/AppRepository.kt`, `CatalogoEjemplo.kt` | Sí; relaciones por ID y consultas inversas | JVM de tres proyectos e IDs inexistentes | No requiere backend ni base de datos |
| UiState comprensible; MutableStateFlow privado / StateFlow readonly | `viewmodel/{Explorar,Contacto,Participacion}UiState.kt`, `{Main,Contacto,Participacion}ViewModel.kt` | Sí, mediante `asStateFlow()` | Código de los tres productores | No se probó colección/lifecycle en ejecución |
| Un propietario por flujo compartido | `ui/navigation/AppNavHost.kt`, `ui/HomeScreen.kt` | MainVM Activity pasado explícitamente a Inicio; ContactoVM por entrada para formulario/resumen/resultado; ParticipacionVM por detalle | Código: un `viewModelStoreOwner = entry` por flujo | Rotación/recreación reales; no se promete recuperar borradores tras muerte del proceso |
| Listados de proyectos/capacidades/integrantes (HU1) | `ui/ExplorarScreen.kt`, `viewmodel/MainViewModel.kt` | Sí, tres tipos y posiciones independientes | Código; JVM de listas 3/2/2 | Pulsaciones y scroll |
| Búsqueda, categoría y año combinados (HU1) | `viewmodel/ExplorarUiState.kt`, `MainViewModel.kt` | Sí; AND, normalización de espacios/mayúsculas, filtros solo para proyectos | JVM de título/descripción/categoría/año y combinaciones | Evento UI de edición |
| Sin coincidencias y limpiar (HU1) | `ui/ExplorarScreen.kt`, `viewmodel/MainViewModel.kt` | Sí; distingue origen vacío, limpia solo el tipo actual | Código; JVM del estado sin coincidencias y de los valores de limpieza | Pulsar limpiar en celular; JVM no invoca ese callback |
| Conservar búsqueda/filtros/posición y evitar destinos duplicados (HU4) | `MainViewModel.kt`, `ui/ExplorarScreen.kt`, `ui/navigation/AppNavHost.kt` | Sí; VM compartido, tres `rememberLazyGridState`, claves, singleTop/restauración y recuperación de detalles | Código; JVM conserva consultas sin mutarlas | Regresar desde detalle y alternar Inicio/Explorar; posiciones reales no probadas |
| Ficha de proyecto con título, descripción, categoría, año y estado (HU2) | `ui/DetalleProyectoScreen.kt`, `model/Proyecto.kt` | Sí, desde proyecto seleccionado; fallback de categoría | Código y JVM de datos diferentes | Lectura visual; fotografías originales ausentes |
| Recursos asociados o estado vacío (HU2) | `model/Recurso.kt`, `repository/AppRepository.kt`, `ui/RecursoProyectoDialog.kt`, `res/raw/nota_*.txt` | Sí, dos textos de demostración locales; Mezclas sin recursos | JVM lee ambos documentos y licencias dentro del ZIP de la APK | Apertura/cierre/scroll del visor Android |
| Capacidades/equipo y enlaces inversos (HU2) | `ui/Detalle{Proyecto,Capacidad,Integrante}Screen.kt`, `repository/AppRepository.kt` | Sí; chips/tarjetas por ID, proyectos relacionados | JVM de relaciones e inversas; código de callbacks | Pulsaciones y regreso real |
| Indicadores reales e IDs inexistentes | `ui/DetalleProyectoScreen.kt`, `CatalogoLayout.kt`, `repository/AppRepository.kt`, `model/Proyecto.kt` | Sí; cantidad desde integrantes resueltos, cupos propios, nulos sin confundir con cero, «no encontrado» | Código y JVM de disponibilidad/IDs nulos/listas vacías | Pantalla de ID inválido por depuración; no se añadió acceso de depuración |
| Contacto: nombre/correo/mensaje y errores (HU3) | `ui/ContactoScreen.kt`, `viewmodel/ContactoUiState.kt`, `ContactoViewModel.kt` | Sí; obligatorios y formato local sin dominio UC obligatorio | JVM de espacios, correos, valores retenidos y validación | Errores junto a campos y teclado en ejecución |
| Contacto: Resumen/Editar/confirmación simulada única (HU3) | `ui/navigation/AppNavHost.kt`, `ui/ContactoScreen.kt`, `viewmodel/ContactoUiState.kt` | Sí; una entrada/VM; editar conserva, confirmar borra datos; guarda de paso y CAS | Código y JVM de transiciones/idempotencia, incluido resumen inválido | Doble pulsación y rotación reales; no se probó CAS concurrente |
| Postulación/aporte: campos, fuente opcional y validación | `ui/ParticipacionModales.kt`, `viewmodel/ParticipacionUiState.kt` | Sí; cuatro obligatorios por formulario, fuente libre/opcional | JVM de vacíos/espacios/correo, texto/URL/fuente vacía, eventos de tipo/campo ajeno | Presentación y foco reales; contraste del diseño con .make ausente |
| Simulación sin cambiar cupos/equipo ni enviar correos | `viewmodel/{Contacto,Participacion}UiState.kt`, sus ViewModels, `app/src/main/AndroidManifest.xml` | Sí; solo transiciones locales, sin almacenamiento/envío | Código y JVM de no modificación; manifiesto fusionado sin permiso INTERNET | No se efectuaron envíos reales ni se requiere hacerlos |
| Modal limitado al área disponible, encabezado y cuerpo scroll | `ui/ParticipacionModales.kt` | Sí; ModalBottomSheet expandida, constraints finitas, `weight(fill=false)`, envío dentro del scroll, X fuera; encabezado con scroll propio en altura extrema | Código y compilación | Validación visual y acceso a todos los campos/botón |
| IME/barras sin duplicación, foco y sin alturas de teléfono | `ui/ParticipacionModales.kt`, `ui/navigation/AppNavHost.kt` | Sí; Material gestiona IME en ventana modal; safeDrawing y BringIntoView; sin offsets de teléfono | Código; fuente local de Material 3 1.3.1 contrastada en auditoría previa y configuración sin cambios | Teclado/Atrás nativo, cámara y horizontal en celular |
| Rotación conserva borrador; cerrar descarta | `viewmodel/ParticipacionViewModel.kt`, `ParticipacionUiState.kt`, `ui/navigation/AppNavHost.kt` | Sí; estado solo en memoria del VM de entrada, cerrar limpia ambos formularios | Código y JVM de cierre/reapertura/confirmación sin repetición | Rotación, X/exterior/gesto/Atrás reales |
| Conservar paleta/fuentes/recursos (HU4) | `ui/theme/Theme.kt`, `res/font`, `assets/licenses`, `res/drawable`, `res/raw` | Sí; azul #1A3C5E, naranja #B5621A, fondo #F4F1EC; sin rediseño | Código, hashes de fuentes y documentos/licencias empaquetados | Insignia/fotos originales y comparación Figma bloqueadas |
| Semanas 6–8 | Material docente no disponible | No certificable | Búsqueda local y separación de fuentes | Aportar guías/rúbrica originales |

Las fichas de capacidades/integrantes conservan los campos que sus modelos sí tienen. No se inventaron categoría, año o estado para entidades cuyo modelo actual no los define.

## Resultados ejecutados

- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: **BUILD SUCCESSFUL**. Lint: **0 errores y 8 advertencias preexistentes** (seis de versiones, icono ausente y logo.xml sin uso). No se actualizaron dependencias ni se ocultaron advertencias.
- `testDebugUnitTest`: **NO-SOURCE**. No hay suite JUnit/Compose configurada; no se agregó framework. Los harness siguientes son comprobaciones auxiliares JVM explícitas, no una suite Gradle aprobada.
- `MvpChecks.java`: **58 correctas**, búsqueda/filtros/estados vacíos y transiciones de contacto. Se incorporan casos reproducibles al repositorio, antes disponibles principalmente como harness temporales.
- `../historia2/Historia2Checks.java`: **86 correctas** (80 existentes + 6 nuevas). Incluye relaciones, recursos del ZIP de APK, validación, disponibilidad, estados separados, no envío de tipo/campo ajeno y fuente libre/opcional.
- Harness temporales anteriores revisados y reejecutados: participación **51**, detalle **29**, contacto **31**, exploración **17**, todos correctos. Casos parcialmente superpuestos; no se suman como cobertura independiente ni verifican eventos UI.
- Inspección estática del código y manifiesto fusionado: no backend, DataStore, Room, autenticación, intent de correo ni permiso INTERNET. El visor solo lee recursos locales de demostración, no guarda datos personales.
- `adb install -r`: **Success**, sin desinstalar ni borrar datos. Se lanzó MainActivity y se obtuvo una jerarquía accesible parcial de postulación de Diseño de mezclas. No se generó evidencia visual completa ni se completó un caso interactivo: el teléfono cambió de aplicación y la guarda del helper detuvo las acciones. **No se declaran aprobadas navegación, campos, envíos, teclado, rotación ni fuentes ampliadas en dispositivo.** No se alteraron fuente, orientación o tamaño del dispositivo.

## Correcciones y archivos

No se detectó un fallo de lógica en los casos ejecutados que justificara cambiar producción. Se mantuvieron pantallas, modelo/datos, tema, recursos, ViewModels, repositorio y navegación existentes. La corrección de esta etapa es de **cobertura reproducible y trazabilidad**, no un rediseño ni una funcionalidad nueva:

- Creado `docs/validacion/mvp/MvpChecks.java`.
- Ampliado `docs/validacion/historia2/Historia2Checks.java` con seis comprobaciones.
- Creado este informe; ampliados README y DevLog sin sustituir contenido previo.
- Helper ADB temporal propio: `C:/Users/sebyv/AppData/Local/Temp/opencode/MvpAuditDevice_20261021.ps1`. No ejecuta scripts del prototipo. No realizó acciones de formulario cuando AppCIMUC dejó de estar al frente.

Sin cambios de paquete, SDK, Java, Gradle/AGP/Kotlin/BOM ni dependencias; sin borrados, commits o push.

## Cómo repetir las comprobaciones de lógica

Desde la raíz, en PowerShell con el JDK existente:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain
$stdlib = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\2.0.21" -Recurse -Filter 'kotlin-stdlib-2.0.21.jar' | Select-Object -First 1 -ExpandProperty FullName
$rjar = Get-ChildItem 'app/build/intermediates' -Recurse -Filter 'R.jar' | Where-Object { $_.FullName -match 'processDebugResources' } | Select-Object -First 1 -ExpandProperty FullName
$cp = "app/build/tmp/kotlin-classes/debug;$stdlib;$rjar"
java --class-path $cp docs/validacion/mvp/MvpChecks.java
java --class-path $cp docs/validacion/historia2/Historia2Checks.java
```

## Pruebas manuales pendientes en celular

Usar solamente datos ficticios. La APK comprobada está en `app/build/outputs/apk/debug/app-debug.apk`; ya se instaló en el teléfono conectado. Reinstalar con `adb install -r` o Run, sin desinstalar, si se necesita repetir.

1. **HU1 / filtros:** Explorar → Proyectos. Categoría Materiales → 2; año 2026 → solo Diseño de mezclas; búsqueda `ENSAYOS` conserva ese proyecto; año 2025 → cero coincidencias con limpieza. Limpiar → tres. Buscar `  MATERIALES    RECICLADOS  ` → solo reciclaje. Capacidades: `MODELADO` → una; Integrantes: `ANA` → una. Cambiar de tipo debe conservar sus consultas independientes.
2. **HU2/HU4 / detalle y regreso:** con búsqueda/filtros y lista desplazada abrir un resultado; comprobar sus datos, chips y tarjetas. Abrir cada detalle relacionado y regresar por Volver y Atrás. Un enlace inverso al proyecto abierto debe recuperar su entrada, no añadir otra copia. Volver a Explorar y alternar Inicio/Explorar debe conservar filtros, consultas y posición. Repetir con los tres proyectos, sin confundir sus relaciones.
3. **Recursos y vacíos:** abrir los textos de demostración de Materiales reciclados y Eficiencia de procesos sin red; leer y cerrar por botón/Atrás/exterior. Diseño de mezclas muestra «Sin recursos disponibles». No hay descarga ficticia ni enlace institucional inventado. Los estados de equipo/capacidades vacíos e IDs inválidos necesitan depuración para reproducirse visualmente; no se borraron datos ni se añadió una pantalla de pruebas.
4. **HU3 / contacto:** Contactar → Revisar vacío y con espacios → errores junto a los tres campos. Datos ficticios y correo `invalido` → sin avance y sin perder valores; `persona@example.test` → Resumen. Editar y Atrás conservan campos. Confirmar rápidamente dos veces → un resultado explícitamente simulado; rotar en Resultado no debe confirmarlo nuevamente. Probar salida al proyecto y a Explorar.
5. **Postulación:** Eficiencia de procesos → Solicitar unirse. Probar cuatro obligatorios, espacios/correo inválido y datos válidos. Resultado azul, icono y Entendido; cupos siguen en 2 y equipo en 1. Materiales reciclados no permite postular pero conserva aporte/contacto.
6. **Aporte:** cuatro obligatorios; fuente vacía válida, luego repetir con referencia bibliográfica y con URL como texto, sin abrirla ni exigir URL. Resultado naranja, simulado/sin correos; Entendido conserva el mismo proyecto.
7. **Cierre/rotación:** en ambos formularios escribir borrador, rotar y cambiar tamaño: debe conservarse. Cerrar con X, exterior, gesto y Atrás, y reabrir: vacío. Con teclado abierto, Atrás debe ocultarlo antes de cerrar. Repetir con errores y confirmación; no se debe repetir la operación por recomposición/rotación.
8. **Adaptabilidad:** repetir en horizontal y con fuente ampliada. Encabezado dentro del área segura, X accesible, cuerpo/Enviar desplazables, campo enfocado visible, sin doble espacio IME/sistema. En altura extrema el texto del encabezado tiene scroll propio. Restaurar las preferencias del teléfono que se modifiquen durante la prueba.

## Defensa oral

Repositorio entrega datos sintéticos y relaciones → ViewModel filtra/valida y publica UiState readonly → pantallas representan datos y emiten callbacks → host navega por IDs manteniendo propietarios → confirmación local única borra borrador, sin enviar ni cambiar cupos/equipo. Los harness prueban lógica; las interacciones Android y la correspondencia curricular quedan distinguidas como pendientes.
