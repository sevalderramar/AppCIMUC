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

## Etapa — base visual y navegación principal (2026-10-20)

Esta sección actualiza el estado vigente sin sustituir los registros anteriores.

### Alcance actual y objetivo del MVP
- **Operativo en código:** Inicio → Explorar (tipo Proyectos) → Detalle de proyecto, con detalles relacionados de integrantes y capacidades y regreso por pila. Inicio también abre proyectos destacados por ID.
- **Objetivo completo, todavía no implementado:** Inicio → Explorar → Detalle de proyecto → Contacto → Resumen → Confirmación simulada. Esta etapa no incluye formulario, resumen ni confirmación y no muestra controles que aparenten enviarlos. Cualquier confirmación futura debe aclarar que CIM UC no recibió una solicitud real.
- Barra principal con **Inicio y Explorar**, visible en esos dos destinos; los detalles conservan Volver y Atrás del sistema. No hay pestaña Proyectos duplicada ni Mi cuenta en el menú de este MVP.
- Registro, autenticación, aportes, postulaciones y evaluaciones son ampliaciones futuras. Se conservan sus documentos y el modelo relacional; no se implementan ni se descartan.
- Búsqueda, filtros y listados generales de capacidades/integrantes quedan pendientes. Sin DataStore, base de datos, red de aplicación ni hardware; muestra sintética local.

### Realizado
- Tema existente centralizado: azul oscuro `#142E49`, naranja oscuro `#984200` (texto/acento con contraste sobre blanco), naranja claro `#FFDCC4` (botón principal), fondo cálido `#F7F4EE`, tarjetas blancas y esquinas de 12/16/24 dp. Tipografía sans serif del sistema, titular 28/36 sp, título 22/30 sp, cuerpo 16/24 sp; demás roles Material 3. **Todos son valores aproximados, no extraídos de Figma.** Se mantiene una paleta clara fija, sin colores dinámicos.
- Inicio presenta CIM UC y el Centro de Innovación en Madera UC, aviso de demostración, botón «Explorar proyectos», indicadores del catálogo completo y dos destacados. No se inventan cupos, colaboradores ni actividad reciente.
- Totales derivados del repositorio: 3 proyectos, 2 capacidades y 2 integrantes. Destacados: los dos de mayor año, desempate por ID; selección demostrativa, no relevancia institucional. Con la muestra actual: Eficiencia de procesos y Diseño de mezclas.
- `ProyectoCard` se reutiliza en Inicio y Explorar. Se conserva el gráfico `logo.xml`, identificado como ejemplo y separado de la ubicación de la insignia.
- Inicio tiene scroll y ancho máximo de 1200 dp; indicadores con salto de línea. Explorar conserva una/dos/tres columnas y su encabezado ahora se desplaza con la lista; detalles conservan scroll y ancho máximo de 840 dp. Textos sin alturas fijas; botón principal de al menos 48 dp y barra Material con selección accesible. Insets gestionados por Scaffold, sin copiar hora, batería o señal.
- `AppNavHost` obtiene el ViewModel en el propietario de la Activity y lo pasa explícitamente a `HomeRoute`: Inicio y catálogo comparten esa instancia. Las pantallas siguen recibiendo datos y callbacks.
- Navegación principal con comprobación de destino actual, `launchSingleTop`, `popUpTo` y restauración de estado. Se conservan todas las rutas por IDs codificados y los estados «no encontrado».
- Sin cambios en paquete, SDK, Java, Gradle, Kotlin o dependencias.

### Fuentes y recursos pendientes
- Disponibles y revisados en la auditoría: cuatro DOCX del equipo y `Avance proyecto CIM UC/Modelo_de_datos_CIM_UC.png`. Los registros previos que los daban por ausentes son históricos. El diagrama es más amplio que los modelos actuales; sus entidades y relaciones no implementadas permanecen pendientes.
- No hay capturas del mockup en la carpeta ni adjuntas legibles en esta sesión. El enlace Figma del documento no entregó contenido visual utilizable en la auditoría: esta implementación sigue la descripción de identidad, **no acredita fidelidad visual a las capturas**.
- Falta la insignia PUC correcta. Aportar el archivo oficial de la referencia como `app/src/main/res/drawable/insignia_puc.png` (o vector Android equivalente). La ubicación en el bloque principal contiene solo el marcador textual «Insignia PUC · recurso pendiente»; no se dibujó ni sustituyó por el gráfico de ejemplo.
- Faltan capturas legibles/valores originales, caso original y material docente de semanas 6–9. No se atribuyen requisitos a esos materiales ausentes.

### Flujo para defensa oral
`CatalogoEjemplo` → `AppRepository` → `MainViewModel` calcula totales y selecciona destacados → Inicio/Explorar muestran datos → callbacks envían IDs al host → el host abre detalles y resuelve sus relaciones. Volver recupera el destino anterior. No se envía ni persiste ninguna solicitud.

### Comprobado
- `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: ejecución correcta; informe lint con 0 errores y 7 advertencias (seis de versiones disponibles, una de icono de aplicación ausente). No se modificaron versiones para silenciarlas.
- Revisión de código de callbacks, alcance del ViewModel e IDs. Sin pruebas automatizadas nuevas ni prueba visual/interacción en emulador o teléfono: compilación y lint no las sustituyen.

### Comprobación manual pendiente en celular
1. Instalar/ejecutar la variante debug. En Inicio, comprobar presentación, marcador PUC, aviso sintético, indicadores 3/2/2 y ambos destacados; desplazar hasta el final.
2. Pulsar «Explorar proyectos»: debe abrir el mismo catálogo que la barra Explorar, con tres proyectos y sin controles de búsqueda/contacto aparentes.
3. Alternar Inicio/Explorar y pulsar repetidamente el destino seleccionado: no debe acumular pantallas. Comprobar que la lista conserva su desplazamiento al cambiar de pestaña.
4. Abrir un destacado y regresar: debe volver a Inicio. Abrir un proyecto desde Explorar y regresar: debe volver al catálogo.
5. En Materiales reciclados, abrir Ana Ejemplo y Ensayos de materiales; recorrer sus proyectos relacionados. Probar Volver de la barra y Atrás del sistema.
6. Repetir en horizontal, con fuente ampliada y navegación del sistema por gestos/botones. Revisar scroll, barra inferior, áreas del sistema y texto sin cortes. En tablet/emulador revisar anchos <600, 600–839 y ≥840 dp de contenido.

## Ajuste de identidad confirmada — referencia .make pendiente (2026-10-20)

Este apartado actualiza la paleta y ubicación del marcador de la etapa anterior; conserva sus registros históricos.

### Realizado
- `ui/theme/Theme.kt` utiliza los valores confirmados por el usuario: azul `#1A3C5E`, naranja `#B5621A`, fondo `#F4F1EC`, tarjetas `#FFFFFF`, texto `#1A1A1A`, texto secundario `#7A7267`, bordes `#DDD8CE` y fondo secundario `#EAE5DC`. Texto sobre azul/naranja: blanco. Paleta fija, sin colores dinámicos ni descarga de fuentes.
- Roles de `lightColorScheme` explícitos, incluidas superficies, contenedores, inversos, bordes y scrim; colores de error locales claramente separados de la identidad confirmada. No se dejan superficies moradas predeterminadas de Material.
- Botón «Explorar proyectos» naranja/blanco y bordes compartidos en tarjetas e indicadores. Catálogo y detalles heredan la misma paleta del tema existente.
- Encabezado con CIM UC y marcador textual «Insignia PUC pendiente», trasladado desde el bloque de presentación. Encabezado fluido con salto de línea e insets superiores/laterales, sin dibujar elementos del sistema. El bloque azul conserva título y descripción del centro.
- Se conservan los dos destacados, indicadores calculados 3/2/2, navegación principal Inicio/Explorar, ViewModel compartido, IDs y relaciones de todos los detalles. No se añade búsqueda, filtros, contacto, registro o DataStore.

### Bloqueo concreto y recursos pendientes
- **No existe `docs/diseno` ni `docs/diseno/Revista musical independiente.make` en esta copia del proyecto.** La búsqueda de `*.make`, incluidos archivos ocultos, no encontró resultados. No se pudo abrir el ZIP, inspeccionar `make_repos`, objetos Git/LFS ni recuperar `src/index.css`, `src/App.tsx` o `public/assets/attach5.png`. No se ejecutaron scripts del prototipo ni se hicieron operaciones de recuperación Git en el repositorio Android.
- Recursos recuperados: **ninguno**. Falta aportar el `.make` en la ruta indicada y las capturas legibles. El único PNG encontrado es el diagrama de datos del equipo, no una captura de interfaz.
- La insignia real sigue pendiente: no se incorporó ningún gráfico sin verificarlo visualmente. Una vez recuperado `attach5.png`, podrá incorporarse como `app/src/main/res/drawable/insignia_puc.png` conservando su proporción y colores. `logo.xml` permanece como ejemplo separado, sin presentarlo como insignia.
- No hay archivos de Plus Jakarta Sans ni Inter. Para integración local faltan los TTF/OTF y su licencia de redistribución, con los pesos que determine el exportado. Posibles nombres Android: `res/font/plus_jakarta_sans_*.ttf` y `res/font/inter_*.ttf`. No se pudieron determinar sus nombres originales ni pesos reales.
- Sustitución temporal: sans serif del sistema. Se mantienen tamaños/pesos Android previos: titular 28/36 sp negrita, título 22/30 sp seminegrita y cuerpo 16/24 sp normal. **No son medidas recuperadas del prototipo**; tampoco lo son los radios 12/16/24 dp conservados.
- SVG, fotografías y posibles URLs externas: no identificables sin `App.tsx`/assets. No se inventan rutas, fotografías o iconos, ni se añaden emojis o dependencias de imágenes remotas. Se conserva el recurso local de ejemplo existente.

### Diferencias y comprobación
- La paleta procede de los valores del mensaje del usuario, no de una lectura del exportado. Composición exacta, tipografías originales, iconos, fotografías, insignia y medidas quedan pendientes. El diseño Android adaptable actual no acredita fidelidad visual completa al mockup.
- `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`; lint 0 errores, 7 advertencias (seis de versiones disponibles y una de icono ausente). Sin cambios en versiones/dependencias.
- Revisión estática del tema, callbacks, ViewModel y navegación. `git diff --check` sin errores; sin eliminaciones inesperadas ni modificaciones a los cambios previos fuera de las ediciones descritas. No hubo prueba en dispositivo/emulador ni comparación de capturas de la app.
- Para prueba manual, seguir los seis pasos anteriores, comprobando ahora el marcador en el encabezado, botón naranja con texto blanco y la paleta confirmada. La aplicación no debe necesitar red para mostrar el contenido.
- Alcance vigente: Inicio → Explorar → Detalle está implementado; Contacto → Resumen → Confirmación simulada sigue previsto para otra etapa. Registro, autenticación, aportes, postulaciones y evaluaciones siguen como ampliaciones futuras documentadas. Material docente 6–9 y caso original continúan pendientes.

## Corrección de composición visual (2026-10-20)

Este es el estado más reciente; los apartados anteriores se conservan como historial.

### Corregido
- Encabezado blanco, compacto y adaptable, con CIM UC azul e insets seguros. Se retiró el texto visible «Insignia PUC pendiente»; **el encabezado no está terminado: falta la insignia original**.
- Bloque azul: nombre del centro arriba, título «Explora proyectos, capacidades e investigación» y descripción «Descubre el ecosistema de innovación del CIM UC y conecta con oportunidades de colaboración.». El repositorio proporciona esos textos; Inicio solo los representa.
- CTA «Explorar proyectos →» naranja/blanco, etiqueta en negrita y forma rectangular con radio de 12 dp, no píldora; altura táctil mínima de 48 dp.
- Indicadores directamente después del bloque: números azules centrados, en negrita, etiquetas pequeñas y tarjetas de igual ancho. Valores derivados 3 proyectos / 2 capacidades / 2 integrantes. En ancho interior <320 dp se apilan. Se retiró «Nuestro catálogo de demostración».
- Tarjeta compartida de Inicio/Explorar con categoría real, título, etiqueta de estado, año, descripción conservada y acceso por ID. `Proyecto` incorpora `estado` e `imagenRes` opcional sin cambiar sus campos, IDs o relaciones anteriores. Estados sintéticos: Materiales reciclados «Finalizado»; los otros dos «En desarrollo». El estado también se muestra en el detalle.
- Si hay un drawable fotográfico, la tarjeta admite imagen superior 16:9 y categoría superpuesta. **Actualmente las tres fotografías faltan y `imagenRes` es null**: se muestra solo una franja compacta de categoría, no un gran espacio vacío ni un gráfico que aparente ser fotografía. Este estado no reproduce todavía la tarjeta fotográfica del mockup.
- Barra inferior blanca con casa/lupa sobre las etiquetas Inicio/Explorar, selección azul y sin accesos extra. Los vectores Android son provisionales locales, **no recuperados de los SVG del prototipo**. Se conservan estado, singleTop, pila, ViewModel compartido y rutas de detalles.
- Eliminados del recorrido los mensajes sobre próximas etapas y la explicación técnica de selección de destacados. Aviso discreto de datos de demostración, sin repetirlo en cada tarjeta. `logo.xml` se conserva como recurso, pero ya no se muestra en Inicio ni se presenta como insignia.

### Límites de fidelidad y recursos pendientes
- Nueva comprobación: `docs/diseno/Revista musical independiente.make` no existe; búsqueda de `.make`, imágenes y fuentes (incluidos ocultos) solo encontró el diagrama del equipo. No se leyeron `src/index.css`/`src/App.tsx`, objetos Git/LFS o `attach5.png`; tampoco hay capturas de referencia utilizables. No se ejecutaron scripts del prototipo.
- Faltan insignia PUC (encabezado y círculo blanco del bloque), fotos locales relacionadas con reciclaje de materiales, eficiencia de procesos y diseño de mezclas, SVG originales y archivos Plus Jakarta Sans/Inter con sus pesos. Sin el exportado no se pueden identificar las URLs de las fotografías ni los nombres originales de fuentes/archivos. Aportar el `.make` completo en la ruta indicada.
- Tipografía realmente utilizada: sans serif del sistema, no Plus Jakarta Sans ni Inter. Pesos configurados en Compose: titulares Bold, títulos de tarjetas Bold, título grande SemiBold, etiqueta/CTA Bold y cuerpo normal. Valores provisionales: titular 28/36 sp, tarjeta 18/26 sp, cuerpo 16/24 sp, etiquetas 14/20 sp y texto pequeño 12/18 sp. No son medidas extraídas de Figma y la apariencia puede variar con la fuente del teléfono. Se respeta fontScale y no se descarga ninguna fuente.
- La composición se corrigió siguiendo la descripción del usuario; los espaciados/radios mantenidos son adaptaciones Android. No se declara fidelidad completa ni el diseño terminado.

### Comprobado realmente
- `gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`: `BUILD SUCCESSFUL`; **0 errores y 8 advertencias**. Seis por versiones, una por icono ausente y una nueva por `logo.xml` no utilizado: se conserva por indicación del usuario, sin ocultar el aviso ni borrar el recurso.
- APK instalada con `adb install -r`, conservando datos, y actividad abierta correctamente en teléfono REA-NX9. Se revisaron capturas reales de Inicio y Explorar: `docs/validacion/correccion-visual/inicio.png` y `explorar.png`. En esta configuración vertical se observan título/descripción completos al desplazar al inicio, CTA, indicadores, tarjetas, estados y navegación inferior. El corte de una tarjeta en el borde inferior del viewport corresponde a contenido desplazable, no a texto truncado.
- Se observó desplazamiento de Inicio y acceso a Explorar desde la barra. La prueba posterior de detalle/regreso no produjo evidencia válida porque el teléfono cambió de aplicación; se detuvo la interacción. No se da por aprobada esa prueba ni la de pulsaciones repetidas. Rutas/relaciones revisadas estáticamente.
- No se compararon capturas con el mockup ausente, ni se probaron horizontal, tablet o cambios de fuente. No se efectuaron envíos reales. Sin commits, push, cambios de versiones ni eliminaciones inesperadas; cambios previos conservados.

### Prueba breve en celular pendiente de completar
1. Abrir Inicio y desplazar hasta arriba: revisar encabezado blanco, textos del bloque, CTA y tarjetas 3/2/2 inmediatamente debajo. Desplazar hasta el aviso final.
2. Pulsar «Explorar proyectos →» y abrir cada tarjeta; verificar categoría, estado sintético, año y descripción.
3. Desde Materiales reciclados, abrir Ana Ejemplo y Ensayos de materiales. Probar Volver y Atrás del sistema para recuperar el destino previo.
4. Alternar Inicio/Explorar, repetir pulsaciones en el destino seleccionado y comprobar que no se acumulan pantallas. Repetir con fuente ampliada y orientación horizontal.
- Búsqueda/filtros y Contacto → Resumen → Confirmación simulada siguen pendientes únicamente en documentación, sin controles ficticios. Registro, autenticación, aportes, postulaciones y evaluaciones continúan fuera de esta etapa.
