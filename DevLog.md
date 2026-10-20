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
