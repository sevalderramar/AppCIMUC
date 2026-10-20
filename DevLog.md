# DevLog de AppCIMUC

## Etapa: integrar responsabilidades existentes

### Realizado
- Ediciones puntuales en `MainActivity.kt`, `ui/HomeScreen.kt` y `repository/AppRepository.kt`; ampliación de README.
- Se conservan `AppInfo` y `MainViewModel` sin cambios; el ViewModel se obtiene mediante Lifecycle, no con construcción directa durante la composición.
- UI parametrizada, tema existente y Preview independiente de Activity.
- Callback de Explorar preparado sin navegación ni envíos reales. Imagen y componentes conservados.
- Sin nuevas dependencias, cambios de configuración, renombres ni eliminación de archivos.

### Comprobado
- Git limpio antes de editar; sin cambios ajenos detectados.
- `gradlew.bat :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL`, 35 tareas (9 ejecutadas, 26 al día).
- `gradlew.bat :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`. Informe en `app/build/reports/lint-results-debug.html`; seis advertencias, sin errores: cinco avisos de versiones más recientes de dependencias existentes y uno por falta de `android:icon`. No se modificaron versiones ni recursos para resolverlos, por estar fuera del alcance.
- Diff revisado: solo cambios de esta etapa, sin eliminaciones inesperadas ni archivos eliminados. `git diff --check` sin errores de espacios; Git avisa de conversión futura LF a CRLF según su configuración local.
- No se ejecutaron pruebas visuales, de interacción ni de ciclo de vida en dispositivo/emulador; la compilación y lint no las sustituyen. Sin commits ni push.

### Pendiente
- Navegación en la siguiente etapa autorizada.
- Verificación visual, de interacción y de conservación del ViewModel durante recomposición en ejecución.
- Contrastar semana 6 con material original y README docente, ausentes. No se equipara el número de guía con semana.
- Recuperar caso, propuestas y modelo relacional; mantener aportes y postulaciones pendientes de especificación, no descartados.

## Prompt 2 — catálogo sintético y navegación

### Realizado
- Carpeta confirmada: `C:\Users\sebyv\Desktop\AppCIMUC`, rama `main`. Se preservaron los cambios locales de la integración previa; no se cambió de rama ni se descartó trabajo.
- Antes de esta etapa solo estaba implementado el prompt 1. Los prompts 2–4 habían quedado bloqueados, no implementados. Se implementa ahora únicamente el prompt 2 por la instrucción expresa de continuar con catálogo sintético y navegación.
- Creados `model/Proyecto.kt`, `Categoria.kt`, `Integrante.kt`, `Capacidad.kt` y `repository/CatalogoEjemplo.kt`.
- Creados `ui/navigation/Routes.kt`, `AppNavHost.kt`, `ui/ExplorarScreen.kt`, `DetalleProyectoScreen.kt`, `DetalleIntegranteScreen.kt`, `DetalleCapacidadScreen.kt` y el layout compartido `CatalogoLayout.kt`.
- Ediciones puntuales de `app/build.gradle.kts`, `MainActivity.kt`, `AppRepository.kt`, `MainViewModel.kt` y `HomeScreen.kt`; ampliación acumulativa de README y este DevLog. `AppInfo` y la función de tema se conservan sin cambios.
- Navigation Compose 2.8.5 es la única dependencia directa agregada. Consulta de POM oficiales: 2.10.1 solicita Compose 1.10.5 y Kotlin stdlib 2.1.20, por lo que no se incorporó esa versión. Se mantienen las versiones existentes.
- Tres destinos principales y dos relacionados, IDs simples codificados, datos nulos con estado comprensible, callbacks y regreso por pila. La muestra permanece separada de los composables.
- Adaptación por ancho interior con una/dos/tres columnas (<600/600–839/>=840 dp), ancho máximo, texto sin truncamiento y scroll. Sin dependencia adaptativa extra. Home conserva ubicación, imagen, botón y resto de componentes.

### Comprobado
- `gradlew.bat :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL`, 35 tareas (13 ejecutadas, 22 al día).
- `gradlew.bat :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`, siete advertencias sin errores: seis de versiones disponibles y una por icono no declarado.
- Consultada la resolución de `debugRuntimeClasspath`: Navigation Compose 2.8.5; Kotlin stdlib 2.0.21, Lifecycle ViewModel Compose 2.8.7 y Compose UI/runtime/animation 1.7.6 se mantienen en la base existente.
- Revisadas las relaciones sintéticas y rutas en código; no hay archivos eliminados ni renombrados ni eliminaciones inesperadas. `git diff --check` sin errores de espacios (avisos de conversión LF/CRLF de Git).
- No se ejecutó la aplicación ni existen pruebas unitarias/instrumentadas. Compilación y lint no acreditan pruebas visuales, adaptabilidad ni navegación en ejecución.

### Pendiente
- Prueba manual de navegación, Volver/Atrás, relaciones, ID inválido y anchos/fuente ampliada según la lista de README.
- Diagrama del equipo, caso y materiales originales no disponibles: relaciones por ID documentadas como propuesta provisional. Semana 6 y README docente siguen pendientes de contraste.
- Prompts 3 y 4 no implementados: sin búsqueda/filtros, formulario ni contacto/resumen. No se avanzó a otra etapa.
- Sin base de datos, DataStore, red de aplicación ni hardware. Sin commits ni push.

## Etapa — base visual, Inicio y navegación principal (2026-10-20)

### Realizado
- Releído `AGENTS.md` y revisados estado real y diff antes de editar. Único cambio previo: `AM AGENTS.md`; se conserva sin tocarlo ni modificar el índice.
- Modificados: `ui/theme/Theme.kt`, `ui/HomeScreen.kt`, `ui/ExplorarScreen.kt`, `ui/CatalogoLayout.kt`, `ui/navigation/AppNavHost.kt`, `repository/AppRepository.kt`, `viewmodel/MainViewModel.kt`, README y este DevLog.
- Creado: `ui/CimComponents.kt`, con tarjeta de proyecto compartida. Sin eliminación/renombre de archivos, pantallas, clases públicas, rutas, modelos o recursos; reorganizados los accesos de Inicio conforme al permiso de esta etapa. El gráfico de ejemplo y la información de presentación se conservan.
- Tema claro centraliza paleta aproximada azul/naranja/fondo cálido, tipografía y formas. Valores y recursos pendientes documentados en README; no se afirma haber reproducido capturas no disponibles.
- Inicio incorpora presentación del Centro de Innovación en Madera UC, marcador textual para insignia oficial pendiente, indicadores calculados y dos proyectos recientes (año descendente/ID ascendente). Datos sintéticos, sin métricas inventadas.
- Barra principal Inicio/Explorar sin catálogo duplicado. Selección por ruta, comprobación de destino actual, `launchSingleTop` y `popUpTo` con restauración de estado; detalles mantienen navegación por ID y regreso. La barra principal no ocupa espacio en los detalles.
- `HomeRoute` recibe explícitamente el ViewModel obtenido por el host fuera de las entradas de navegación: se evita obtener otra instancia con alcance de la entrada Inicio. UI parametrizada y Preview sin ViewModel.
- Encabezado de Explorar dentro de la cuadrícula desplazable para mejorar horizontal/fuente ampliada; se conservan umbrales de columnas. Inicio usa ancho máximo, scroll e indicadores que pasan a otra línea. Insets inferiores consumidos por el contenedor de navegación y superiores por las pantallas.
- Objetivo futuro documentado: Contacto → Resumen → Confirmación simulada, sin implementar aún esos destinos ni controles ficticios. Registro/autenticación/aportes/postulaciones/evaluaciones quedan como ampliaciones futuras y se conserva su documentación.
- Sin nuevas dependencias, cambios de versiones, configuración, servicios, DataStore ni hardware.

### Comprobado
- Primera ejecución de `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`, 46 tareas (19 ejecutadas, 27 al día).
- Repetición tras el ajuste final del catálogo y revisión de código: `BUILD SUCCESSFUL`, 46 tareas (12 ejecutadas, 34 al día).
- Diff revisado sin eliminaciones inesperadas; `git diff --check` sin errores de espacios. Git advierte conversión futura LF→CRLF, sin cambiar la configuración del repositorio. El cambio previo de `AGENTS.md` se mantiene intacto.
- Informe `app/build/reports/lint-results-debug.txt`: 0 errores, 7 advertencias; seis `GradleDependency` y una `MissingApplicationIcon`. No se actualizaron dependencias ni se improvisó icono/insignia.
- Revisión estática de datos: muestra con 3 proyectos, 2 integrantes y 2 capacidades; destacados determinados por la regla documentada. No equivale a ejecutar un test automatizado.
- No se ejecutó la app ni se probaron navegación, orientación, escalado de fuente, accesibilidad o conservación de estado en dispositivo/emulador. Lista manual en README.

### Pendiente
- Aportar capturas legibles y el recurso oficial `res/drawable/insignia_puc.png` para sustituir el marcador; el `logo.xml` existente es solo un gráfico de ejemplo y no se usa como insignia.
- Material docente de semanas 6–9 y caso original ausentes. Los DOCX y diagrama del equipo sí están disponibles desde la auditoría, corrigiendo ese pendiente histórico sin borrar registros previos.
- Implementar en etapas posteriores búsqueda/filtros y el flujo de contacto/resumen/confirmación simulada. No se realizó commit ni push.

## Etapa — paleta confirmada y bloqueo de referencia Figma Make (2026-10-20)

### Realizado
- Leídos AGENTS, README y DevLog; revisados Git y código actual. Se conservaron todos los cambios de la etapa anterior, incluido `AM AGENTS.md` y el archivo no seguido `ui/CimComponents.kt`. No se modificó el índice Git.
- Modificados únicamente en esta continuación: `ui/theme/Theme.kt`, `ui/HomeScreen.kt`, `ui/CimComponents.kt`, README y DevLog. No se crearon recursos ni se borraron/renombraron archivos o funcionalidades.
- Sustituida la paleta aproximada anterior por los ocho colores confirmados en el mensaje del usuario. Roles Material explícitos, incluidos error e inversos, sin colores dinámicos. Colores de error locales no atribuidos a Figma.
- CTA naranja con texto blanco, borde compartido en tarjetas/indicadores y marcador de insignia trasladado al encabezado junto a CIM UC. Encabezado con altura de contenido, salto de línea e insets seguros para no depender de dimensiones rígidas de teléfono.
- Se mantienen tipografía del sistema, medidas y formas de la base anterior como alternativas temporales. No se atribuyen pesos ni tamaños al exportado no leído.
- Revisados alcance Activity del ViewModel del host, paso explícito a Inicio, callbacks e IDs. No fue necesario modificar de nuevo la navegación principal ya implementada: conserva comprobación de destino actual, singleTop, popUpTo y restauración de estado. Detalles y relaciones intactos.

### Bloqueado / pendiente
- `docs/diseno` no existe. Búsqueda de `**/*.make` con archivos ocultos: sin resultados. No se pudieron inspeccionar el contenedor solicitado, objetos Git/LFS ni código/recursos internos. No se ejecutaron scripts del prototipo ni se recuperaron objetos en el repositorio Android; no hubo original que extraer o modificar.
- Recursos recuperados: ninguno. Pendientes `attach5.png`, SVG, fotografías, fuentes Plus Jakarta Sans/Inter y capturas. No se conocen URLs de fotografías, nombres de archivos de fuentes o pesos originales. README detalla qué aportar y las sustituciones temporales.
- No se afirma fidelidad visual completa. La adaptación exacta del prototipo requiere aportar `docs/diseno/Revista musical independiente.make` y contrastar capturas reales de la app.
- No se implementaron búsqueda/filtros, contacto, registro ni DataStore. Se conserva el alcance futuro y la documentación del equipo.

### Comprobado
- `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`, 46 tareas (17 ejecutadas, 29 al día).
- `app/build/reports/lint-results-debug.txt`: 0 errores, 7 advertencias; seis por versiones y una `MissingApplicationIcon`. Sin actualizaciones para silenciarlas.
- `git diff --check` sin errores de espacios (solo avisos de futura conversión LF→CRLF). Revisión de diff sin eliminaciones inesperadas; cambios previos conservados. No se realizaron commits ni push.
- No se ejecutó la aplicación ni se probaron interacciones, accesibilidad, rotación o escalado de fuente en dispositivo/emulador. Compilación y lint no sustituyen esa validación.

## Etapa — corrección de composición y prueba visual parcial (2026-10-20)

### Realizado
- Leídos AGENTS y documentación vigente; Git y código revisados antes de editar. Conservados todos los cambios anteriores, el índice y `AGENTS.md` intactos.
- Modificados en esta continuación: `model/Proyecto.kt`, `repository/CatalogoEjemplo.kt`, `repository/AppRepository.kt`, `viewmodel/MainViewModel.kt`, `ui/HomeScreen.kt`, `ui/CimComponents.kt`, `ui/ExplorarScreen.kt`, `ui/CatalogoLayout.kt`, `ui/DetalleProyectoScreen.kt`, `ui/navigation/AppNavHost.kt`, `ui/theme/Theme.kt`, README y este DevLog.
- Añadidos `res/drawable/ic_inicio.xml` y `ic_explorar.xml`: casa/lupa vectoriales provisionales, no extraídas de SVG ausentes; sin emojis ni dependencias. Capturas de la app en `docs/validacion/correccion-visual/inicio.png` y `explorar.png`.
- Encabezado blanco con CIM UC azul y sin marcador técnico visible; hero con jerarquía y textos solicitados; botón naranja/blanco en negrita y radio 12 dp; indicadores centrados azules de igual ancho inmediatamente después del bloque (3/2/2 calculados).
- Tarjeta compartida con categoría resuelta desde datos del repositorio, estado sintético y año. Se añaden solo `estado` e `imagenRes` opcional al modelo; IDs, descripciones y relaciones intactos. Estado visible también en detalle. Listados/categorías se entregan por parámetros, sin consultar repositorio desde la UI.
- Fotografía superior/categoría superpuesta soportadas si hay drawable local; al no haber fotos, cabecera compacta de categoría. No se inventaron fotografías ni URLs y no hay grandes huecos vacíos.
- Retirados avisos de próximas etapas y selección técnica de destacados del recorrido. Se conserva un aviso discreto de demostración, sin repetirlo por tarjeta. Se retira el gráfico de ejemplo de Inicio, pero su archivo `logo.xml` permanece intacto.
- Barra inferior con iconos sobre etiquetas y selección azul. Opciones y callbacks de navegación existentes conservados, sin búsqueda, contacto, registro o DataStore. Tema mantiene la paleta fija confirmada y centraliza nuevos roles tipográficos provisionales.

### Recursos bloqueados
- `Test-Path` del `.make` solicitado: False. Búsqueda con ocultos: solo el PNG del modelo de datos, sin `.make`, fotos ni fuentes. No se pudo inspeccionar el contenedor ni objetos Git/LFS. No se recuperó ningún recurso original y no se ejecutaron scripts del prototipo.
- Encabezado y círculo del bloque siguen incompletos por falta de `attach5.png`. Plus Jakarta Sans/Inter no están cargadas: se usa sans serif del sistema. SVG originales, fotografías y medidas exactas pendientes del exportado. README registra diferencias sin declarar fidelidad completa.

### Comprobado
- `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`, 46 tareas (23 ejecutadas, 23 al día).
- Lint: 0 errores, 8 advertencias; seis de versiones, una de icono ausente y una nueva de recurso `logo.xml` no usado, conservado por restricción del proyecto. No se silenciaron avisos ni se actualizaron versiones.
- Dispositivo REA-NX9 detectado. Instalación debug con `adb install -r`: Success; apertura de MainActivity: Status ok. El teléfono se bloqueó durante la prueba; el usuario lo desbloqueó para continuar.
- Capturas reales de Inicio y Explorar inspeccionadas: textos del hero, indicadores, tarjetas/estados y barra inferior visibles en vertical. Se observó scroll en Inicio y acceso al catálogo. La prueba posterior de detalles/regreso quedó inconclusa al cambiar el teléfono de aplicación; se detuvieron las interacciones para no actuar fuera de AppCIMUC. No se afirma que esos casos pasaron.
- Sin referencia visual accesible para comparación directa. No se probaron rotación, tablet o fuente ampliada ni se añadieron tests automatizados. La prueba visual parcial no sustituye esos casos.
- Revisión de código/diff y `git diff --check` sin errores de espacios; avisos LF→CRLF de Git. No se borraron/renombraron archivos ni relaciones ni se descartaron cambios previos. Sin commits ni push.

### Pendiente
- Aportar el `.make` en `docs/diseno` para recuperar y verificar insignia/fotos/fuentes/SVG; completar entonces la comparación visual.
- Completar pruebas de botón principal, detalles/relaciones, Volver/Atrás, pulsaciones repetidas, fuente ampliada y orientación horizontal según README. Detener esta etapa antes de búsqueda y filtros.

## Etapa — Explorar: tres tipos, búsqueda y filtros (2026-10-20)

### Implementado
- Releídos AGENTS, README y DevLog; Git inicialmente limpio y código real revisado. El catálogo, tarjetas, rutas/detalles y métodos de listado del repositorio ya existían: no se regeneraron ni duplicaron.
- Creado `viewmodel/ExplorarUiState.kt`: selector, consultas independientes, filtros/opciones de proyectos, resultados y totales; propiedades derivadas para contador/vacío/limpieza y función pura `filtrarExplorar` con normalización por Locale.ROOT, trim y espacios repetidos.
- Ampliado `viewmodel/MainViewModel.kt`: datos del repositorio existentes, StateFlow de solo lectura y MutableStateFlow privado, eventos de selección/búsqueda/categoría/año/limpieza; opciones obtenidas del catálogo completo y filtrado fuera de UI. Mantiene datos/funciones previos de Inicio y detalles.
- Adaptado `ui/ExplorarScreen.kt`, no regenerado: selector, búsqueda, chips de categoría/año solo para proyectos, contador, limpieza por tipo, estados vacíos distintos y tres cuadrículas con claves estables. Una posición guardable por tipo; se reinicia por eventos de edición/limpieza, no por entrada/recomposición. Todo el encabezado de controles desplaza junto con resultados para evitar una zona fija demasiado alta.
- Ampliado `ui/CimComponents.kt` con tarjeta reutilizada por capacidades/integrantes, conservando la tarjeta de proyectos. Sin cambios en modelos, muestra sintética, recursos o repositorio: las consultas necesarias ya estaban disponibles.
- Modificado `ui/navigation/AppNavHost.kt`: recoge StateFlow mediante collectAsState compatible con la base, conecta eventos y detalles por ID, mantiene la instancia Activity compartida e incorpora insets IME/ocultación del teclado al cambiar de sección.
- Actualizados README y este DevLog, diferenciando implementación, comprobación estática/JVM y pruebas manuales pendientes. Conservados todos los apartados anteriores.
- Paquete, estructura, dependencias, versiones, tema, iconos y datos intactos. Sin contacto, registro, aportes, postulaciones, DataStore, red o base de datos.

### Verificado
- Primera ejecución `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: BUILD SUCCESSFUL, 48 tareas (16 ejecutadas, 32 al día).
- Segunda ejecución tras ajuste del contador singular/plural: BUILD SUCCESSFUL, 48 tareas (13 ejecutadas, 35 al día).
- Lint: 0 errores y 8 advertencias previas; seis de versiones disponibles, MissingApplicationIcon y UnusedResources de logo.xml conservado. Sin actualizaciones para silenciarlas.
- testDebugUnitTest NO-SOURCE: no hay tests integrados/framework configurado. No se afirma que se ejecutara una suite inexistente.
- Harness JVM temporal sin framework sobre las clases Kotlin reales compiladas: **17 comprobaciones correctas**. Comprueba título/descripción, mayúsculas, espacios iniciales/finales/repetidos, categoría, año, combinación AND, cero coincidencias, estado equivalente a limpieza, capacidades/integrantes por nombre/descripción, consultas independientes y fuente vacía. No prueba el evento limpiar del ViewModel ni interacciones de Compose.
- Harness en `C:/Users/sebyv/AppData/Local/Temp/opencode/AppCimucExplorarChecks_20261020.java`; comando Java source launcher con classpath de `app/build/tmp/kotlin-classes/debug` y stdlib 2.0.21 ya instalada en caché. Sin dependencias/framework/archivos de tests añadidos al proyecto. Casos reproducibles detallados en README.
- Revisados alcance de ViewModel, callbacks, IDs, Saver de listas y ausencia de filtrado/acceso al repositorio en composables. Diff sin eliminaciones inesperadas y git diff --check sin errores de espacios. Sin commits ni push.

### Pendiente / límites
- No se ejecutó esta versión en dispositivo/emulador ni se tomaron capturas nuevas. Navegación, conservación de posición/estado, rotación, teclado, fuente ampliada y horizontal están implementados pero necesitan validación de interacción según README. Las capturas previas no prueban los nuevos controles.
- `.make` y fuentes originales siguen ausentes; revisada la captura previa de Explorar. Insignia, fotografías, tipografías originales y material docente 6–9 siguen pendientes; no se atribuyen valores/requisitos a fuentes no disponibles.
- Estado en memoria durante la Activity y restauración de listas Compose/Navigation, no persistencia permanente ni promesa de recuperación de consultas tras muerte del proceso.
- Etapa detenida antes de formulario de contacto; no se implementaron resumen/confirmación, registro, aportes ni postulaciones.
