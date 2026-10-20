# AppCIMUC

## Integración de responsabilidades

### Realizado
- Se mantienen el paquete `com.example.appcimuc` y las carpetas `model`, `repository`, `viewmodel` y `ui`.
- `MainActivity` utiliza `AppNombreGrupoXTheme` y abre `HomeRoute`.
- `HomeRoute` obtiene `MainViewModel` con `viewModel()` de Lifecycle, que reutiliza la instancia del propietario actual entre recomposiciones.
- `HomeScreen` recibe `AppInfo` y `onExplore` por parámetros, sin acceder a Activity, ViewModel ni Repository.
- Se conservan la imagen y los componentes de ejemplo. El botón delega en el callback; la Activity lo deja sin navegación en esta etapa.
- El Preview utiliza datos de ejemplo locales y no obtiene un ViewModel.
- No se agregaron dependencias ni se cambiaron versiones.

### Flujo para defensa oral
`AppRepository.getAppInfo()` crea información fija en memoria → `MainViewModel` expone `appInfo` → `HomeRoute` entrega esos datos a `HomeScreen` → Compose los muestra. En sentido inverso, el botón invoca `onExplore`, suministrado desde la Activity; no navega ni envía solicitudes.

### Comprobado
- `gradlew.bat :app:assembleDebug --console=plain`: compilación correcta.
- `gradlew.bat :app:lintDebug --console=plain`: correcto, con seis advertencias (cinco de versiones disponibles y una de icono no declarado). No se actualizaron versiones ni recursos. Detalles en `DevLog.md`.
- Diff revisado sin eliminaciones inesperadas. No se realizó prueba visual ni de interacción.

### Pendiente
- Navegación de Explorar: corresponde a la etapa siguiente; los textos Inicio e Información CIM UC siguen siendo etiquetas.
- Prueba visual e interacción en emulador o dispositivo.
- Semana 6 pendiente de contraste: no están disponibles su material, el README docente ni las guías 6–9. Los comentarios que mencionan guías no acreditan semanas ni requisitos.
- Faltan el caso, las propuestas del equipo y el modelo relacional para contrastar el alcance. Aportes y postulaciones no se descartan ni se implementan en esta etapa.

## Prompt 2 — catálogo y navegación

Esta sección actualiza el estado posterior a la integración anterior, cuyo registro se conserva arriba. Explorar ya tiene navegación; los prompts 3 (búsqueda) y 4 (contacto) siguen sin implementarse.

### Realizado
- Inicio → Explorar → DetalleProyecto, con botones Volver y pila gestionada por Navigation Compose. Los integrantes y capacidades relacionados abren sus detalles, que enlazan a los proyectos asociados.
- `ui/navigation/Routes.kt` contiene las rutas y `AppNavHost.kt` conecta las pantallas. Solo se transmiten IDs String codificados, no objetos ni datos personales. Un ID desconocido muestra «no encontrado» y permite volver.
- `AppRepository` conserva `getAppInfo` y resuelve el catálogo y relaciones en memoria; `MainViewModel` entrega los datos a las pantallas. Las pantallas reciben datos y callbacks, no un controlador de navegación.
- Muestra sintética separada en `repository/CatalogoEjemplo.kt`: tres proyectos, dos categorías, dos integrantes ficticios y dos capacidades; categorías y años distintos para la futura historia de filtros.
- **Modelo provisional:** cada proyecto referencia una categoría y listas de IDs de integrantes y capacidades. Las consultas inversas se derivan de esas listas, evitando duplicar relaciones. No se afirma equivalencia con el diagrama del equipo, que sigue ausente.
- Única dependencia directa agregada: `navigation-compose:2.8.5`. El POM oficial de 2.10.1 requiere Kotlin stdlib 2.1.20 y Compose 1.10.5; se eligió 2.8.5 para no arrastrar esos cambios. No se cambiaron versiones existentes, SDK ni Java. No se requiere dependencia adaptativa adicional.
- Adaptación equivalente con `BoxWithConstraints`: ancho disponible del contenido <600 dp → una columna; 600–839 dp → dos; desde 840 dp → tres. El catálogo limita su ancho máximo a 1200 dp. Los detalles son fluidos hasta 840 dp, con scroll y sin alturas de texto fijas. Home conserva su imagen y componentes, y ahora permite scroll. Estos umbrales son del contenido disponible, no mediciones de la ventana completa.
- Búsqueda y filtros permanecen visibles como texto de pendiente, sin controles que aparenten funcionar. Sin formulario, base de datos, red de aplicación ni hardware.

### Comprobado
- `gradlew.bat :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL`.
- `gradlew.bat :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`, siete advertencias (seis por versiones disponibles y una por icono ausente), sin errores.
- Resolución `debugRuntimeClasspath`: Navigation 2.8.5, Kotlin stdlib 2.0.21, Lifecycle ViewModel Compose 2.8.7 y Compose UI/runtime/animation 1.7.6, coherentes con la base y su BOM.
- Revisión estática de rutas, callbacks y referencias de la muestra. Diff sin eliminaciones inesperadas. No se ejecutó la app ni existen pruebas unitarias/instrumentadas.

### Pendiente y prueba manual
1. Abrir Inicio, pulsar Explorar y abrir cada proyecto. Verificar título, descripción, categoría y año.
2. En Materiales reciclados, abrir Ana Ejemplo y volver; abrir Ensayos de materiales y volver. Sus proyectos relacionados deben corresponder a las mismas relaciones por ID.
3. Probar tanto Volver de la barra como Atrás del sistema: deben regresar al destino anterior hasta Inicio.
4. Probar catálogo y detalles en anchos Compact, Medium y Expanded, además de fuente ampliada y orientación horizontal: comprobar columnas, scroll y ausencia de cortes. El cambio de columnas usa el ancho interior tras padding.
5. El manejo de ID desconocido está implementado pero pendiente de prueba en ejecución mediante un argumento de navegación de prueba; no se agregó un control de depuración ni un deep link externo.
- Contrastar modelo provisional con diagrama, caso y material docente; semana 6 y README docente siguen pendientes. No se deduce semana a partir del número de una guía.
- Búsqueda, filtros y contacto quedan para sus respectivas etapas. Aportes y postulaciones siguen pendientes de especificación, no descartados.
