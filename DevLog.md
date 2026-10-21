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

## Etapa — flujo de contacto estrictamente simulado (2026-10-20)

### Implementado
- Leídos AGENTS, README y DevLog; Git limpio al inicio. Revisados detalle, navegación, modelo, diseño/dependencias y consultas existentes antes de editar. No existía flujo de contacto; se conserva el catálogo completo y su estado.
- Creados `viewmodel/ContactoUiState.kt`, `viewmodel/ContactoViewModel.kt` y `ui/ContactoScreen.kt` (formulario, resumen y confirmación).
- Modificados `ui/DetalleProyectoScreen.kt`, `ui/CimComponents.kt`, `ui/navigation/Routes.kt`, `ui/navigation/AppNavHost.kt`, README y DevLog. Sin borrar/renombrar archivos ni reemplazar pantallas existentes.
- Detalle conserva información/relaciones y representa imagen local si hay; estado ya existente mantenido. No hay recursos en el modelo actual: texto «Sin recursos disponibles», sin enlaces inventados. CTA Contactar por ID, también en proyectos finalizados sin asociarlo a cupos.
- UiState con campos, errores y paso; StateFlow de solo lectura y mutable privado. Validadores/transiciones puros para obligatorios, espacios, formato de correo, resumen válido, editar y confirmar una vez. Nombre/correo/mensaje se conservan ante errores, se recortan exteriormente al revisar y se borran al confirmar. Sin red/esperas/registros de solicitudes.
- Una sola ruta `contacto/{id}`: ViewModel explícitamente ligado a esa NavBackStackEntry mediante factory. Los tres pasos se representan dentro de ella, evitando tres instancias o acumulación de pantallas. Sin datos personales en argumentos, SavedStateHandle, archivos, logs o DataStore.
- Volver/Atrás de Resumen edita conservando campos; de Formulario sale y descarta al eliminar el propietario; de Resultado vuelve al proyecto. Salida a Explorar recupera su entrada o restaura/abre sobre Inicio cuando se llegó desde un destacado. Guardas de destino en entrada/salidas y compareAndSet de confirmación para pulsaciones repetidas.
- Pantallas con tema existente, etiquetas y errores por campo, tarjetas blancas, botones accesibles, scroll e IME del host; acciones Siguiente/Listo del teclado sin envío real. No se alteró el ViewModel Activity de Explorar ni sus rutas/relaciones.
- No se añadieron dependencias ni se cambiaron versiones, SDK, Gradle o paquete. Sin preferencias, persistencia, registro, autenticación, aportes o postulaciones.

### Verificado
- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: BUILD SUCCESSFUL, 48 tareas (20 ejecutadas, 28 al día).
- Lint: 0 errores y 8 advertencias previas. testDebugUnitTest NO-SOURCE: no hay suite integrada/framework configurado; no se presentan tests inexistentes como aprobados.
- Harness JVM temporal con datos ficticios sobre clases Kotlin reales de validación/transiciones: **31 comprobaciones correctas**. Incluye correo con alias/subdominio y 13 formatos inválidos, obligatorios, espacios, errores sin perder campos, trim/multilínea, editar, confirmación única/idempotente, borrado tras confirmar, resumen inválido y proyecto inexistente.
- Harness en `C:/Users/sebyv/AppData/Local/Temp/opencode/AppCimucContactoChecks_20261020.java`, Java source launcher usando clases debug y stdlib 2.0.21 de caché, sin framework nuevo. No verifica Android/Compose, concurrencia del CAS ni lifecycle real.
- Regresión lógica auxiliar de Explorar: 17 comprobaciones de filtrado reejecutadas, todas correctas.
- Revisados código, alcance de ViewModel, rutas (solo IDs), pila, transición única y ausencia de envíos/persistencia. Diff sin eliminaciones inesperadas; git diff --check sin errores de espacios. Sin commits ni push.

### Pendiente / límites reales
- No se ejecutó esta versión en teléfono/emulador, ni se capturaron las nuevas pantallas. Casos manuales de formulario, errores, edición, rotación, confirmación repetida, salida/reinicio vacío, posición de catálogo, fuente ampliada y teclado detallados en README.
- El borrador vive solo mientras exista su entrada/VM en memoria. Rotación implementada mediante alcance de Navigation; no se promete restauración tras muerte del proceso.
- `.make`, insignia/fuentes/fotos originales y material docente siguen pendientes; se mantiene el diseño disponible sin inventar referencia.
- Detener antes de preferencias y persistencia de semana 9; no se implementaron otras ampliaciones.

## Ajuste — detalle visual con chips y equipo (2026-10-20)

### Implementado
- Releído AGENTS y revisados README/DevLog, Git y código. Se conservaron todas las modificaciones/no seguidos previos de contacto. Antes de editar se identificaron falta de fotos/cupos/cargo y ausencia de implementaciones de aportes/postulaciones (búsqueda en app/src/main sin resultados).
- Solo modificado en código `ui/DetalleProyectoScreen.kt`; actualizados README y este DevLog. No se reiniciaron pantallas, modelos, datos, navegación, contactos o estilos compartidos.
- Foto local opcional con Crop proporcional 16:9 y categoría superpuesta abajo; fallback compacto sin foto ficticia. Título y estado separados, indicadores de año/integrantes desde relaciones, descripción completa, capacidades con AssistChip y equipo con tarjetas blancas/avatares de iniciales y descripción existente.
- Se mantienen recursos, estado no encontrado y callbacks por ID a capacidades/integrantes. Una sola acción Volver compartida; navegación y conservación de filtros/posición no modificadas.
- CTA final «Contactar sobre este proyecto» reutiliza el contacto ya implementado. Dos botones deshabilitados con Próximamente, sin rutas ni formularios nuevos de aportes/postulaciones.
- Sin indicador de disponibilidad porque el modelo no la representa; no se convirtió desconocido en cero ni se derivó disponibilidad del estado. Sin cargos/especialidades inventados ni datos copiados de una referencia para todos los proyectos.
- Scroll existente, FlowRow para indicadores/chips, texto sin alturas rígidas y controles táctiles mínimos de 48 dp. Avatar no interactivo separado del nombre para evitar lecturas duplicadas; tarjeta de equipo completa pulsable.

### Verificado
- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: BUILD SUCCESSFUL, 48 tareas (18 ejecutadas, 30 al día). Lint 0 errores/8 advertencias previas; testDebugUnitTest NO-SOURCE.
- 29 comprobaciones JVM auxiliares correctas de los tres proyectos: identidad/metadatos, descripción/estado, conjuntos y cantidades de relaciones, consultas inversas y proyecto desconocido. Equipo/capacidades: reciclaje 2/1, energía 1/1 y mezclas 1/2. No hubo cambios en el repositorio ni su muestra.
- Harness temporal `C:/Users/sebyv/AppData/Local/Temp/opencode/AppCimucDetalleChecks_20261020.java`, sin framework/dependencias nuevas. Regresión: 31 comprobaciones de contacto y 17 de filtrado reejecutadas, correctas.
- Revisados callbacks de chips/equipo/contacto y regreso en código; no equivale a probar pulsaciones. Diff revisado sin eliminaciones inesperadas; git diff --check sin errores. Cambios previos y archivos conservados; sin commits ni push.

### Pendiente real
- Capturas adjuntas de detalle no accesibles en esta sesión/carpeta; .make y fotos originales ausentes. No se afirma comparación visual ni recuperación de fuentes/insignia nuevas. Se siguió la especificación escrita conservando el tema actual.
- No se ejecutó esta versión en dispositivo/emulador. Probar los tres detalles, chips/tarjetas, regreso con filtros/posición, contacto y scroll/fuente ampliada/horizontal según README. La rama de foto superpuesta queda pendiente de imagen local real.
- No se implementan aportes, postulaciones, preferencias o persistencia en este ajuste.

## Etapa — completar historia 2 y adaptación de participación (2026-10-20)

### Realizado
- Git limpio al inicio, sin diff previo. Releídos AGENTS/README/DevLog, Gradle, código real, cuatro DOCX y modelo PNG. A diferencia de la documentación anterior, el código inicial ya tenía `ParticipacionUiState`, `ParticipacionViewModel`, ambos formularios/confirmaciones, cupos y aceptación sintéticos: se conservaron, no se recrearon.
- Historia 2 contrastada con el documento del equipo: tres criterios (metadatos/recursos, enlaces relacionados, navegación a sus detalles). Aportes/postulaciones son propuestas del equipo autorizadas ahora como simulaciones por el usuario, no evaluación/registro reales ni contenidos curriculares nuevos. Caso original y guías docentes continúan pendientes.
- Modificados `ui/ParticipacionModales.kt`, `DetalleProyectoScreen.kt`, `DetalleIntegranteScreen.kt`, `navigation/AppNavHost.kt`, `theme/Theme.kt`, `model/Integrante.kt`, `repository/AppRepository.kt`, `CatalogoEjemplo.kt`, `viewmodel/MainViewModel.kt`, README y DevLog. Agregados `model/Recurso.kt`, `ui/RecursoProyectoDialog.kt`, dos TXT en `res/raw`, cinco TTF en `res/font`, dos licencias en `assets/licenses` y `docs/validacion/historia2/{Historia2Checks.java,Fuentes.md}`.
- Modal compartido con encabezado separado, scroll limitado al espacio restante, botón dentro del scroll y safeDrawing. Se consultó el código fuente de Material 3 **existente** 1.3.1 para comprobar que su ventana ya aplica IME: no se duplicó. Eliminada dependencia de altura de configuración/pantalla; márgenes adaptables al espacio real. En altura extrema, scroll adicional del texto de encabezado limitado a media área y X siempre fuera del scroll. Campo enfocado y cambios IME solicitan visibilidad; se conserva foco al primer error, cierre limpio, confirmación diferenciada e idempotencia.
- Disponibilidad distingue cero/desconocido/positivo. Cargo/especialidad opcionales no rellenados; equipo original intacto, avatar azul. Recursos por proyecto leídos dentro de la app desde TXT de demostración empaquetados; Mezclas conserva estado vacío. No hay URLs inventadas ni descargas sin destino.
- Navegación de relaciones recupera entradas existentes por ruta/ID para no duplicar destinos; contacto/resumen y estado del catálogo conservados. Fuentes estáticas de los autores con licencias OFL empaquetadas, sin dependencia o red en la app. Paleta conservada.
- Paquete, estructura, versiones, SDK, Java, Gradle, dependencias, tres proyectos/IDs/descripciones/equipos/capacidades y flujos previos conservados. Sin backend, evaluación administrativa, autenticación, persistencia personal, commits ni push.

### Comprobado realmente
- `assembleDebug`, `lintDebug`, `testDebugUnitTest` ejecutados juntos. Dos primeras ejecuciones correctas; un ajuste posterior del encabezado produjo error Kotlin de receptor implícito `maxHeight` dentro de RowScope. Corregido capturando la cota en BoxWithConstraints antes del Row. Última ejecución tras ordenar la indentación: **BUILD SUCCESSFUL**, 48 tareas (13 ejecutadas, 35 al día), 13 s.
- Lint final: **0 errores / 8 advertencias existentes**: seis de versiones, MissingApplicationIcon y logo.xml sin uso (se conserva). Sin cambios de versiones para silenciar avisos. UnitTest **NO-SOURCE**: no se añadió framework ni se afirma suite inexistente aprobada.
- Harness reproducible `docs/validacion/historia2/Historia2Checks.java`: **80 comprobaciones correctas** sobre clases Kotlin reales y documentos/licencias del ZIP de la APK. Relaciones y contadores de tres proyectos, matriz de disponibilidad de nueve combinaciones, validación de ambos formularios, conservación/limpieza, no duplicación de envío, fuente libre/opcional, recursos propios/estado vacío y ausencia de modificaciones de cupos/equipo. Comando en README.
- Harness previos revisados y reejecutados: participación **51**, detalle **29**, contacto **31**, filtrado **17** correctos. Datos de prueba sintéticos. No verifican Compose, foco real, concurrencia CAS ni propietarios Android/rotación.
- Diff y estado final revisados; sin archivos eliminados/renombrados, sin eliminaciones inesperadas; `git diff --check` sin errores, solo avisos de conversión LF→CRLF locales.

### Pendiente / límites
- `adb devices` no detecta equipos y `emulator -list-avds` no lista AVD. No se instaló/ejecutó esta versión: teclado/Atrás nativo, gestos/exterior, adaptación visual, rotación, fuente ampliada, enlaces y conservación del catálogo requieren pruebas reales (ocho pasos en README). Capturas de etapas anteriores no son evidencia de esta etapa.
- `.make` solicitado no existe en esta copia, tampoco `docs/diseno`, insignia original ni fotografías/fuentes exportadas. Las capturas adjuntas no son accesibles en esta sesión. El enlace Figma del DOCX solo devuelve «Figma», sin código utilizable; no se ejecutaron scripts/instrucciones del prototipo. Insignia, imagen principal real y comparación exacta de medidas/formas permanecen bloqueadas por esas fuentes ausentes. Las fuentes agregadas proceden de los autores, no del prototipo.
- Disponibilidad desconocida comprobada en JVM, no en una ficha real de la muestra (se conservan sus cupos existentes). Estados de equipo/capacidades vacíos implementados, no probados visualmente; no se borraron relaciones para simularlos.
- Flujo oral y tabla de criterios implementado/verificado/pendiente en README. Etapa detenida aquí, sin nuevas funcionalidades. Commit sugerido, no realizado: `Completa recursos de ficha y adapta formularios de participación simulada`.

## Auditoría MVP — semanas 6–8 e historias (2026-10-21)

### Realizado
- Git limpio al inicio, sin diff previo. Leídos AGENTS, README/DevLog completos y código actual de UI, navegación, tres ViewModels/UiState, modelos/repositorio, Gradle y manifiesto. Extraído como texto el contenido de los cuatro DOCX sin ejecutar macros/scripts; inspeccionado modelo PNG. Búsqueda local con ocultos no encontró material docente de semanas 6–8, README docente ni .make: no se dedujeron requisitos curriculares de comentarios del código o de los documentos del equipo.
- Se confirmó implementación mediante código de filtros AND, vacíos/limpieza, relaciones/recursos, IDs desconocidos, contacto/resumen/editar/confirmación y participación simulada. UiState/StateFlow privado-readonly, callbacks y propietarios coherentes: Activity para catálogo; una entrada para contacto completo; detalle para participación. Sin persistencia personal, red/envíos, cambios de cupos ni incorporación automática.
- No se detectó un fallo de lógica en los casos ejecutados que justificara modificar producción; no se tocaron pantallas para aparentar una corrección. Conservados diseño, fuentes, recursos, datos/relaciones, paquete y estructura. No se puede afirmar «todo verificado»: UI y contraste curricular/visual siguen pendientes.
- Creado `docs/validacion/mvp/MvpChecks.java` (58 comprobaciones puras de exploración/contacto) y `docs/validacion/mvp/Auditoria.md` (matriz completa y ocho pruebas manuales). Ampliado `docs/validacion/historia2/Historia2Checks.java` con seis casos (86 totales). README y DevLog ampliados, sin sustituir historial. No se agregó infraestructura/framework/dependencias: se reutiliza el Java source launcher existente.

### Comprobado realmente
- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: **BUILD SUCCESSFUL**. Ejecución inicial: 48 tareas (8 ejecutadas, 40 al día). Lint: **0 errores y 8 advertencias previas** (versiones, icono ausente, logo.xml no utilizado y conservado). UnitTest **NO-SOURCE**, no se afirma suite integrada aprobada.
- Nuevos harness reproducibles en el repositorio: **58** comprobaciones de exploración/contacto y **86** de historia 2/participación/recursos, todas correctas. Casos de búsqueda por nombre/descripción, normalización, filtros AND, origen vacío frente a cero coincidencias, consultas independientes, correos y longitudes, valores conservados, revisión/editar/confirmación única, ID desconocido, fuente libre/opcional, tipos/campos ajenos y no modificación de equipo/cupos. Los valores de limpieza se prueban como estado, no el callback del ViewModel.
- Harness temporales anteriores releídos y reejecutados: participación **51**, detalle **29**, contacto **31**, filtrado **17**, correctos. No prueban Compose, propietarios Android, CAS concurrente, rotación, foco ni scroll. Los conteos contienen casos superpuestos.
- Código y manifiesto fusionado revisados: sin permiso INTERNET, intent de correo, backend ni DataStore/Room. Hashes de cinco TTF coinciden con Fuentes.md; licencias y documentos de demostración se leen del ZIP de APK en el harness.
- Dispositivo ADB disponible. Instalación `adb install -r`: **Success**, sin desinstalar/borrar datos; lanzamiento de MainActivity. Inspección accesible parcial de postulación de Diseño de mezclas, sin completar caso interactivo. El teléfono cambió de app y el helper temporal propio `MvpAuditDevice_20261021.ps1` bloqueó la siguiente acción antes de capturar pantalla/tocar. Se detuvo la prueba. **No se verificaron navegación, formularios completos, teclado, gestos, rotación o fuente ampliada en ejecución.** No se cambiaron fuente/orientación/resolución del teléfono.
- Diff revisado sin archivos eliminados ni eliminaciones inesperadas; `git diff --check` sin errores (avisos locales LF→CRLF). No se modificaron archivos de producción, versiones o dependencias. Sin commits/push.

### Bloqueos / entrega
- Aportar material docente original/rúbricas semanas 6–8 para certificar exigencias por semana. Solo están disponibles las historias/requisitos/visión del equipo y su modelo; no se inventó contenido del caso original ni de las guías.
- .make, insignia y fotos originales ausentes; se preservan fuentes locales y recursos existentes. No se declara fidelidad nueva ni recuperación del prototipo. Sin scripts del prototipo ni rediseño.
- Matriz requisito → archivo → implementado → verificado → pendiente y pasos manuales detallados en `docs/validacion/mvp/Auditoria.md`, enlazados desde README. La instalación y jerarquía parcial no sustituyen esos pasos. Continuar las pruebas de celular solo mientras AppCIMUC esté al frente.
- Flujo oral: repositorio resuelve datos/relaciones → VM filtra/valida y publica estado → UI emite callbacks → host conserva propietarios/navega por ID → confirmación local borra borrador, sin correos ni cambios de cupos/equipo. No se avanzó a otra etapa ni se implementó DataStore.
- Commit sugerido, no ejecutado: `Documenta auditoría del MVP y amplía comprobaciones de lógica`.

## Preferencia autorizada de filtros con DataStore (2026-10-21)

### Realizado
- Git limpio al inicio y sin diff previo. Releídos AGENTS, estado vigente README/DevLog, filtros/VM/navegación/Inicio y Gradle. No existe guía de semana 9 ni archivo identificable por semana en esta copia (búsqueda local con ocultos y materiales PDF/DOCX/MD/PPTX/TXT). No se le atribuyen requisitos a material ausente; autorización de DataStore y alcance proceden del usuario.
- Agregada únicamente `androidx.datastore:datastore-preferences:1.1.1`. POM Android oficial consultado: Kotlin stdlib 1.8.22 y coroutines 1.7.3 compatibles con la base. Resolución runtime confirma Kotlin 2.0.21, coroutines 1.7.3, Compose UI 1.7.6, Lifecycle VM Compose 2.8.7, DataStore 1.1.1; Okio 3.4.0 es transitivo. No se cambiaron versiones existentes, Java, SDK, Gradle ni AGP.
- Creados `model/PreferenciasFiltrosProyectos.kt`, `repository/PreferenciasFiltrosRepository.kt`, `viewmodel/PreferenciasFiltrosUiState.kt`, `docs/validacion/preferencias/PreferenciasChecks.kt` y `EjecutarChecks.ps1`. Modificados build.gradle de app, MainViewModel, ExplorarScreen, HomeScreen (factory por defecto), AppNavHost (factory compartido/callbacks), README/DevLog. No se borraron archivos o funcionalidades.
- Repositorio encapsula operaciones suspend; delegate Context de nivel superior privado mantiene una sola instancia por archivo `files/datastore/filtros_proyectos.preferences_pb`, con applicationContext. Solo bool recordar, ID de categoría y año. Preferencia apagada por defecto; desactivar escribe false/remueve claves de filtros, sin cambiar estado de sesión. No SharedPreferences, Room, catálogos, entidades, solicitudes ni datos personales/texto libre persistidos.
- VM con UiState/MutableStateFlow privado y StateFlow readonly. Lee antes de habilitar cambio de preferencia y nunca guarda defaults en el arranque. Política pura valida cada filtro por opciones presentes, sin modificar búsquedas/tipo/otros contenidos. Cambios de sesión anteriores a la lectura tienen prioridad y se persisten después de leer solo si corresponde.
- Guardados explícitos asíncronos en viewModelScope, sin efectos de recomposición. Durante escritura se bloquean temporalmente filtros/control de proyectos/limpieza, no búsquedas ni otros tipos. El interruptor muestra solo valor confirmado tras éxito. Errores visibles con reintento, sin falsas lecturas/escrituras correctas; se conservan filtros actuales y reintento guarda los actuales. Fallo de desactivación no afirma haber borrado. Cancelación se propaga. Scope opcional únicamente para pruebas del VM, sin nueva dependencia.
- Control discreto accesible en opciones de Proyectos: fila toggleable con Role.Switch, superficie táctil mínima, texto de qué recuerda y qué no, estado de carga/error anunciado. Paleta/fuentes/navegación/contacto/postulación/aporte intactos; sin ampliar otros flujos.

### Comprobado realmente
- `assembleDebug`, `lintDebug`, `testDebugUnitTest`: **BUILD SUCCESSFUL** en ejecuciones durante la implementación. Ejecución que recompiló Kotlin: 48 tareas (13 ejecutadas, 35 al día), 10 s; repetición final: 48 tareas (1 ejecutada, 47 al día), 1 s, seguida nuevamente por los 46 checks correctos. Lint **0 errores / 9 advertencias**: ocho anteriores + disponibilidad de versión nueva de DataStore, no se actualiza por conveniencia. Primera compilación informa `libdatastore_shared_counter.so` empaquetada sin strip de símbolos; no es fallo de build.
- UnitTest **NO-SOURCE**, no se añadió framework y no se afirma suite integrada aprobada. Harness reproducible Kotlin con dependencias ya resueltas: **46 comprobaciones correctas** sobre DataStore real, restauración y VM con scope controlado. Cierre/cancelAndJoin antes de recrear DataStore sobre el mismo archivo; nunca dos instancias simultáneas. Archivo temporal contiene exclusivamente preferencias sintéticas.
- Casos: apagado inicial; activado/categoría/año y reapertura real; borrar claves al desactivar y preservar sesión; Todos/nulos; inválidos por separado; consultas fuera del archivo y búsqueda vacía en VM nuevo; carga tardía y edición anterior sin defaults sobrescritos; eventos de otros tipos no escriben. Fallos de lectura/escritura inyectados en DataStore: error observable, sin éxito, sesión utilizable y reintento tanto de activar como de desactivar.
- Harness de regresión del proyecto reejecutados: **58** comprobaciones exploración/contacto y **86** historia 2/participación/recursos, correctas. Sin prueba de UI, lifecycle Android real, rotación o proceso del teléfono en esta etapa. El dispositivo aparece conectado por ADB, pero no se instaló/ejecutó esta versión; no se reutiliza como evidencia la instalación de la auditoría anterior.
- Diff revisado y `git diff --check` sin errores (avisos locales LF→CRLF); sin eliminaciones inesperadas, sin modificaciones a SDK/Gradle/Kotlin/BOM ni dependencias previas. Sin commits/push.

### Pendiente y defensa
- Seis pasos manuales y matriz de los seis casos solicitados en el nuevo apartado de README: control/apagado inicial, activar y esperar guardado, filtros + reinicio de proceso, búsqueda vacía, desactivar manteniendo sesión/reiniciar Todos, limpiar/otros tipos y accesibilidad. Errores e IDs/años inválidos cubiertos por JVM, sin manipular el archivo del teléfono para forzarlos.
- Guía semana 9 pendiente de aportar; no se certifica su contenido. Figma Make/insignia/fotos originales conservan sus pendientes; no se rediseñan pantallas ni se ejecutan scripts del prototipo.
- Flujo oral: repositorio de preferencias → lectura/validación en VM compartido → UI readonly/callbacks → escritura atómica asíncrona → confirmar solo tras éxito. Se guarda únicamente la preferencia y dos filtros; el resto sigue en memoria. Etapa detenida aquí.
- Commit sugerido, no realizado: `Agrega preferencia DataStore para recordar filtros de proyectos`.

## Estado restaurable frente a preferencias y formularios temporales (2026-10-21)

### Realizado
- Releído AGENTS y revisados código de preferencias, navegación, formularios, README/DevLog y Gradle. Git inicialmente con modificaciones/no seguidos de la etapa anterior de DataStore: conservados todos, sin restaurar/revertir ni tocar el índice. Guía semana 9 no encontrada entre materiales locales con ocultos; no se atribuyen requisitos a una fuente ausente.
- Faltante real: consultas/tipo/filtros estaban únicamente en el VM Activity, no restaurables tras proceso. Posiciones ya tenían tres rememberLazyGridState; formularios ya tenían VMs de alcance correcto y datos solo en memoria. Se reutilizaron estos mecanismos en vez de duplicarlos.
- Nuevo `viewmodel/ExplorarEstadoGuardado.kt` con siete entradas pequeñas en SavedStateHandle: tipo, tres consultas acotadas a 500 caracteres, ID categoría, año, marcador. Valida tipos/categoría/año y no guarda entidades/listas/imágenes ni datos personales de formularios. El límite es igual para UI/handle, no una copia truncada distinta del valor visible.
- `MainViewModel.kt`: factory Android recoge SavedStateHandle desde CreationExtras del propietario actual. Entradas del handle son autoritativas; el StateFlow es proyección calculada desde el repositorio. Se captura presencia anterior a registrar defaults para que una tarea con Todos gane a preferencias activas. Precedencia tarea > DataStore > defaults; edición antes de lectura sigue ganando, sin guardados de defaults ni de snapshots por recomposición.
- `PreferenciasFiltrosUiState.kt`: parámetro/comentario de política clarifica conservación por tarea o edición. `ui/ExplorarScreen.kt`: solo comentario del Saver existente, no otra copia de posición. Contacto/aporte/postulación no modificados: VM y errores/borradores de sesión siguen en memoria; nuevo proceso crea VM vacío, sin restaurar Resumen/Resultado o evento. La guarda de resumen inválido existente se probó de nuevo.
- Nuevo `docs/validacion/estado/EstadosChecks.kt` y `EstadoYRestauracion.md` con tabla de propietarios/mecanismos/duración y cinco procedimientos separados. Runner existente `docs/validacion/preferencias/EjecutarChecks.ps1` parametrizado y ampliado con AAR savedstate/Lifecycle ya transitivos; no añade dependencias. README/DevLog ampliados, sin reemplazar historial.
- Sin cambios de diseño, recursos, rutas públicas, datos/relaciones, DataStore/clave persistente, versiones, SDK o Gradle en esta continuación. Sin backend, persistencia de formularios, commits o push.

### Comprobado realmente
- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: **BUILD SUCCESSFUL**. Última ejecución con recompilación: 48 tareas (13 ejecutadas, 35 al día), 6 s. Lint **0 errores / 9 advertencias** conocidas, sin silenciarlas. UnitTest **NO-SOURCE**, no hay suite integrada de interacción Android.
- Harness nuevo: **31 comprobaciones JVM correctas** con SavedStateHandle/VM reales y scope controlado. Copiar valores a nuevo handle/VM simula entradas recibidas; **no serializa Bundle Android ni acredita muerte real de proceso**. Cubre precedencia incluso Todos/recordar apagado, reconstrucción desde repositorio, IDs/tipos/filtros inválidos, lectura tardía, límite de consulta, entradas pequeñas, nuevos formularios vacíos y no repetición de confirmaciones/resumen inválido seguro.
- Regresiones ejecutadas: **46** preferencias/DataStore real, **58** exploración/contacto, **86** detalle/participación/recursos, correctas. Conteos parcialmente superpuestos; no se convierten en pruebas de lifecycle o UI.
- APK instalada con `adb install -r`: Success, sin borrar datos/desinstalar; MainActivity llegó al frente y mostró PID. Luego la guarda del helper temporal propio `EstadoDevice_20261021.ps1` detectó que AppCIMUC no estaba al frente y detuvo la lectura/interacción. No se completó ninguno de los cinco casos Android. No se hizo kill/force-stop de la app, rotación, cambio de ajustes ni uso de «No conservar actividades». Instalación/PID no se presentan como pruebas de restauración.
- Diff revisado y git diff --check sin errores (avisos LF→CRLF locales), sin archivos eliminados ni eliminaciones inesperadas. Trabajo previo conservado.

### Pendiente / defensa
- Recomposición, rotación/recreación Activity, navegación/regreso con posición, proceso muerto con tarea restaurada e inicio nuevo con preferencias: los cinco procedimientos quedan diferenciados y pendientes en `docs/validacion/estado/EstadoYRestauracion.md`, enlazado desde README. No borrar datos ni desinstalar para probar. `am kill` solo paquete en segundo plano + PID nuevo + misma tarea no es igual a force-stop/eliminar tarea; No conservar actividades solo recrea Activity.
- Datos de formularios pueden perderse tras proceso: comportamiento documentado y deliberado por privacidad. Rotación se implementa con retención del VM, no se da por probada visualmente. Task snapshot requiere que Android haya guardado el estado; OEM puede no conservarlo. DataStore mantiene preferencias entre inicios nuevos mientras exista su archivo, sin prometer comportamiento de reinstalación/backup.
- Guía de semana 9/material original sigue pendiente; no se inventó contenido. Tabla completa y flujo oral documentados: entradas pequeñas de tarea → VM/proyección → repositorio por IDs; posiciones propiedad de Compose; DataStore solo entre inicios sin snapshot; formularios temporales sin eventos reproducibles.
- Commit sugerido, no ejecutado: `Distingue estado restaurable de exploración y preferencias persistentes`.

## Pruebas del MVP y evidencia real (2026-10-21)

### Realizado
- Git limpio al inicio, sin diff previo. Leídos AGENTS, historias de usuario DOCX, README/DevLog vigentes, código de preferencias/formularios/detalle y configuración de pruebas. Se mantienen HU1 búsqueda, HU2 detalle/relaciones, HU3 contacto y HU4 navegación/legibilidad; postulación/aporte se identifican como propuestas del equipo autorizadas, no requisitos curriculares inventados.
- Creado `docs/PRUEBAS_MVP.md` con 30 casos y todos los campos solicitados; cada caso marca Aprobado/Fallido/No ejecutado con alcance explícito. Nuevo directorio `docs/validacion/mvp-20261021` con salidas reales, identificación de APK/commit, instalación/apertura y una captura/jerarquía accesible de Inicio. README ampliado sin reemplazar historial.
- No se añadió otra prueba de lógica: los riesgos revisados ya tenían cobertura en los cuatro harness existentes y no se reprodujo un defecto concreto que justificara cambiar producción. No se rediseñó, actualizó dependencia, modificó catálogo o amplió funcionalidad.

### Verificaciones reales
- `gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain`: BUILD SUCCESSFUL, 1 s, 48 tareas (1 ejecutada, 47 al día). Informe lint copiado: **0 errores / 9 advertencias** conocidas (siete de versiones, icono ausente, logo.xml no utilizado y conservado). UnitTest **NO-SOURCE**: no una suite aprobada.
- Reejecutados y guardados: **58** checks JVM de MvpChecks, **86** Historia2Checks, **46** preferencias/DataStore real y **31** estado/VM/handle, correctos. Casos parcialmente superpuestos; no se presentan como pruebas de Compose, navegación, foco o lifecycle real.
- REA-NX9 Android 15 conectado; sin AVD. Ajustes iniciales leídos: fuente 0.85, vertical/rotación manual. `adb install -r`: Success; `am start -W`: Status ok, arranque COLD, 2206 ms. No desinstalación ni borrado de datos. Capturados e inspeccionados `01-inicio.png` y XML reales: CIM UC, presentación/CTA, totales 3/2/2, destacados y barra visibles en esta configuración.
- Recorrido posterior incompleto: el selector no confirmó el CTA y no pulsó una ubicación supuesta. En el siguiente intento la guarda detectó que AppCIMUC no estaba al frente y detuvo las interacciones. `dispositivo.txt` registra observación real posterior `AppCIMUCAlFrente=False`. No se capturaron otras apps. No se trataron estos bloqueos como defecto del producto ni como navegación aprobada.
- No se cambiaron ajustes de fuente/rotación, no se rellenaron formularios, no se envió nada ni se hizo kill/force-stop. Los datos previstos para los casos pendientes son exclusivamente ficticios. Helper temporal propio `PruebasMvpDevice_20261021.ps1`, sin ejecutar código del prototipo.
- Resultado de matriz: **8 Aprobados, 0 Fallidos reproducidos, 22 No ejecutados**. Un aprobado de JVM no aprueba el recorrido Android equivalente. Solo UI-01 (presentación visible de Inicio) está aprobado en dispositivo; DEV-01 cubre instalación/apertura.
- Evidencias identificadas por SHA-256 de APK y HEAD Git, conservadas sin inventar capturas/resultados. Diff revisado y git diff --check sin errores; sin eliminaciones inesperadas. Sin cambios de producción ni versiones; sin commits/push.

### Pendiente y entrega
- Completar los 22 casos No ejecutados según `docs/PRUEBAS_MVP.md`; lista de capturas por obtener separada de la única captura nueva existente. Requiere teléfono dedicado al recorrido y estable al frente. Fuente ampliada/horizontal, IME, rotación, posición, procesos y duplicados necesitan evidencia específica, no deducción de compilación ni captura estática.
- Mantener separación: recomposición ≠ Activity recreada ≠ proceso con tarea restaurable ≠ inicio nuevo con DataStore. No borrar datos/desinstalar ni usar No conservar actividades como prueba de proceso. No se declara MVP completamente aprobado.
- Advertencias de lint y ausencia de Make/insignia/fotos originales permanecen; no se solucionaron actualizando versiones o inventando recursos. Flujo oral de la entrega: requisitos → caso/precondiciones → ejecución → evidencia → estado real, distinguiendo prueba de lógica y prueba de interacción.
- Commit sugerido, no ejecutado: `Documenta pruebas del MVP y registra evidencia real de Inicio`.
