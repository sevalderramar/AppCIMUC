# Pruebas del MVP AppCIMUC

## Ejecución registrada — 2026-10-21

**Resultado general: validación parcial, no aprobación completa del MVP.**

- **8 casos Aprobados**, **0 Fallidos reproducidos** y **22 No ejecutados** en esta matriz. Cada aprobado está limitado a lo que dice su caso: los harness de lógica no aprueban los recorridos Android.
- Git limpio al comenzar. AGENTS, historias del equipo (`Avance proyecto CIM UC/Historias de Usuarios.docx`), implementación relevante, README/DevLog y pruebas existentes revisados.
- Se ejecutaron compilación, lint y cuatro harness existentes. No se detectó un riesgo concreto de lógica sin cobertura que justificara agregar otra prueba, ni un defecto reproducido que justificara cambiar producción. No se actualizaron dependencias ni se rediseñaron pantallas.
- Teléfono **REA-NX9, Android 15**, vertical, `font_scale=0.85`. Sin AVD disponible. APK instalada con `adb install -r`, sin borrar datos ni desinstalar; MainActivity abrió correctamente. Se capturó Inicio y se inspeccionó su PNG real.
- El intento posterior de navegación no se completó: el selector de prueba no confirmó el CTA y después AppCIMUC dejó de estar al frente. La guarda detuvo las interacciones. **No se acusa al CTA de fallar, ni se aprueban navegación/formularios por ese intento incompleto.** No se tocaron otras apps ni se cambiaron orientación/fuente/ajustes. No se hizo kill/force-stop.
- Solo hay **una captura nueva** de esta ejecución: `01-inicio.png`. Las imágenes históricas de `validacion/correccion-visual` no acreditan la versión actual.

### Fuentes y nomenclatura

- **HU1:** búsqueda, filtros y sin coincidencias.
- **HU2:** ficha completa, recursos y relaciones con integrantes/capacidades.
- **HU3:** formulario de contacto y confirmación simulada.
- **HU4:** navegación, legibilidad y regreso conservando contexto.
- **PE:** postulación/aporte, propuestas del equipo autorizadas como simulaciones en el MVP.
- **PREF/EST:** alcance autorizado de preferencias y estado restaurable.
- **Aprobado:** se ejecutaron los pasos indicados y se obtuvo el resultado esperado, solo en el nivel especificado.
- **Fallido:** pasos ejecutados con un resultado incorrecto reproducido. Un bloqueo del entorno o del selector de automatización no es por sí mismo un defecto de la app.
- **No ejecutado:** no se completó la comprobación; se dan pasos reproducibles. NO-SOURCE no equivale a una suite aprobada.

### Precondiciones comunes

Repositorio raíz AppCIMUC, JDK/SDK y caché Gradle existentes. Para Android: teléfono desbloqueado y AppCIMUC estable al frente; usar la APK debug indicada en `validacion/mvp-20261021/version.txt`. No borrar datos, no desinstalar ni modificar catálogos para provocar estados vacíos.

Datos **solo ficticios** para los casos pendientes: nombre `Persona Demo`, correo válido `persona@example.test`, inválido `invalido`, institución `Institución de prueba`, mensaje/motivación/aporte `Contenido ficticio para validar el MVP`, asunto `Referencia de prueba`, respaldo `Referencia bibliográfica ficticia, 2026`. El dominio `.test` no representa un buzón real. No utilizar datos personales reales ni efectuar envíos externos.

## Matriz técnica y de lógica

Las referencias de evidencia son archivos reales de esta ejecución. Los pasos de los harness figuran completos al final.

| ID | Requisito / historia | Precondición | Pasos | Resultado esperado | Resultado obtenido | Estado | Evidencia real |
|---|---|---|---|---|---|---|---|
| TEC-01 | APK compilable | JDK/SDK/caché disponibles | Ejecutar `:app:assembleDebug` con Gradle wrapper | BUILD SUCCESSFUL y APK debug | BUILD SUCCESSFUL; 48 tareas totales en la ejecución conjunta, APK disponible | Aprobado | [gradle.txt](validacion/mvp-20261021/gradle.txt), [hash/commit](validacion/mvp-20261021/version.txt) |
| TEC-02 | Análisis estático | Mismo código de TEC-01 | Ejecutar `:app:lintDebug`; leer informe | Sin errores; advertencias identificadas | 0 errores / 9 advertencias: siete por versiones disponibles, icono ausente y logo.xml conservado sin uso | Aprobado | [lint.txt](validacion/mvp-20261021/lint.txt), [gradle.txt](validacion/mvp-20261021/gradle.txt) |
| TEC-03 | Suite Gradle relacionada | Tarea testDebugUnitTest disponible | Ejecutar `:app:testDebugUnitTest` | Ejecutar tests si existen fuentes | Tarea ejecutada devuelve **NO-SOURCE**; no existe suite integrada JUnit/Compose | No ejecutado | [gradle.txt](validacion/mvp-20261021/gradle.txt) |
| LOG-01 | HU1/HU3: filtrado y contacto, nivel JVM | Clases debug compiladas | Ejecutar `MvpChecks.java` | Búsqueda/filtros AND, vacíos, validaciones/transiciones correctas | 58 comprobaciones correctas; incluye correos inválidos, conservación, editar e idempotencia | Aprobado | [logica-mvp.txt](validacion/mvp-20261021/logica-mvp.txt), [fuente](validacion/mvp/MvpChecks.java) |
| LOG-02 | HU2/PE: relaciones, recursos y participación, nivel JVM/APK | Clases y APK debug | Ejecutar `Historia2Checks.java` | Relaciones por ID y recursos empaquetados correctos; obligatorios/fuente libre/envío único sin cambiar cupos | 86 comprobaciones correctas; documentos locales leídos del ZIP de APK, no visor Android | Aprobado | [historia2.txt](validacion/mvp-20261021/historia2.txt), [fuente](validacion/historia2/Historia2Checks.java) |
| LOG-03 | PREF: persistencia y errores | Dependencias resueltas, carpeta temporal de pruebas | Ejecutar `EjecutarChecks.ps1` | DataStore real conserva tres claves autorizadas; desactivar elimina filtros; VM maneja errores/reintento | 46 comprobaciones correctas con archivos temporales reales y VM con scope controlado | Aprobado | [preferencias.txt](validacion/mvp-20261021/preferencias.txt), [fuente](validacion/preferencias/PreferenciasChecks.kt) |
| LOG-04 | EST: precedencia y formularios nuevos | Mismo runner, clases savedstate existentes | Ejecutar `EstadosChecks.kt` mediante runner | Tarea > preferencias > defaults; inválidos seguros; nuevo VM de formulario vacío y sin repetir evento | 31 comprobaciones correctas; copiar valores a otro handle **no es muerte de proceso Android ni serialización Bundle** | Aprobado | [estado.txt](validacion/mvp-20261021/estado.txt), [fuente](validacion/estado/EstadosChecks.kt) |
| DEV-01 | Instalación y apertura de esta APK | Dispositivo ADB disponible | Instalar con `install -r`; abrir MainActivity con `am start -W` | Success y Status ok, sin borrar datos | Success; Status ok; COLD; apertura registrada en 2206 ms y captura de Inicio | Aprobado | [instalacion.txt](validacion/mvp-20261021/instalacion.txt), [apertura.txt](validacion/mvp-20261021/apertura.txt) |

## Matriz de recorridos Android

Los casos No ejecutados constituyen el plan para completar la validación en celular. No se rellenan sus resultados con lo deducido del código o de los harness.

| ID | Requisito / historia | Precondición | Pasos | Resultado esperado | Resultado obtenido | Estado | Evidencia real |
|---|---|---|---|---|---|---|---|
| UI-01 | HU4: presentación de Inicio | DEV-01 completado, vertical/fuente 0.85 | Abrir Inicio; capturar e inspeccionar composición visible y jerarquía accesible | CIM UC, presentación/CTA, totales 3/2/2, destacados y barra Inicio/Explorar visibles sin solapamientos en esta vista | Se observaron esos elementos en PNG/XML reales; la segunda tarjeta continúa debajo del viewport desplazable. No evalúa fidelidad Figma ni otros tamaños | Aprobado | [01-inicio.png](validacion/mvp-20261021/01-inicio.png), [01-inicio.xml](validacion/mvp-20261021/01-inicio.xml) |
| UI-02 | HU4: navegación principal y estado | Inicio visible, teléfono dedicado al recorrido | Pulsar CTA; alternar Inicio/Explorar; pulsar varias veces la pestaña elegida; dejar búsqueda/posición y alternar | Catálogo correcto, sin destinos duplicados, contexto conservado | Intento incompleto: no se confirmó el primer selector; después la app no estaba al frente. No se verificó navegación | No ejecutado | [dispositivo.txt](validacion/mvp-20261021/dispositivo.txt); no hay captura de navegación |
| UI-03 | HU1: filtros combinados | Explorar → Proyectos, restricciones limpias | Materiales → 2026 → buscar `ENSAYOS` | Solo Diseño de mezclas; categoría/año/consulta combinados con AND | No se ejecutaron los pasos en teléfono | No ejecutado | Sin evidencia Android; LOG-01 cubre lógica, no este caso |
| UI-04 | HU1: sin coincidencias | UI-03 o Proyectos limpio | Buscar `zzzinexistente`; repetir con Materiales/2025 y `ENSAYOS` | Cero resultados, mensaje comprensible y acción de limpieza, distinto de catálogo de origen vacío | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-05 | HU1: limpieza | Cero coincidencias y restricciones activas | Pulsar Limpiar búsqueda y filtros | Consulta vacía, categoría/año Todos, tres proyectos; otros tipos conservan sus consultas | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-06 | HU1/HU4: tipos, consultas y recomposición | Explorar accesible | Capacidades buscar `MODELADO`; Integrantes buscar `ANA`; volver a cada tipo; mostrar/ocultar teclado y editar consulta | Un resultado pertinente en cada búsqueda; consultas independientes sin reinicio por recomposición | No se completó interacción; no se deduce del StateFlow | No ejecutado | Sin evidencia Android |
| UI-07 | HU2: fichas diferentes | Proyectos sin restricciones | Abrir los tres proyectos y comparar título, descripción, categoría, año, estado, cupos/equipo | Reciclaje 2025/finalizado/sin cupos/Ana-Luis; Eficiencia 2026/2 cupos/Luis; Mezclas 2026/1 cupo/Ana; datos sintéticos propios | No ejecutado en teléfono | No ejecutado | Sin evidencia Android; LOG-02 solo prueba datos |
| UI-08 | HU2/HU4: capacidades y regreso | Ficha con chips; catálogo con contexto reconocible | Abrir chip; revisar capacidad/proyectos asociados; volver; seguir enlace inverso al proyecto abierto; regresar al catálogo | IDs y ficha correctos, sin duplicar el detalle; consulta/filtros/posición conservados | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-09 | HU2/HU4: integrantes y regreso | Ficha con equipo | Abrir tarjeta de Ana/Luis según proyecto; revisar proyectos relacionados; volver por Volver y Atrás | Integrante correcto, datos disponibles sin cargo/especialidad inventados y regreso al contexto previo | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-10 | HU2: recursos disponibles | Reciclaje o Eficiencia | Abrir cada texto de demostración; leer/desplazar y cerrar por botón/Atrás/exterior | Documento real, legible y marcado demostración; no descarga ficticia ni necesidad de red | No se abrió el visor Android | No ejecutado | Sin evidencia Android; LOG-02 sí leyó los archivos de la APK |
| UI-11 | HU2: recursos vacíos | Diseño de mezclas | Desplazar a Recursos asociados | «Sin recursos disponibles», sin acción ficticia | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-12 | HU3: validación de contacto | Contactar desde un proyecto | Revisar vacío; probar espacios; introducir datos ficticios con correo `invalido`; corregir correo | Errores junto a nombre/correo/mensaje; no avanzar inválido; valores conservados al corregir | No se abrió ni rellenó el formulario en esta ejecución | No ejecutado | Sin evidencia Android; LOG-01 cubre validación |
| UI-13 | HU3: Formulario → Resumen → Editar → Confirmación | Formulario con datos ficticios válidos | Revisar; comprobar resumen; Editar y volver con Atrás; revisar de nuevo y confirmar; volver al proyecto/catálogo | Datos conservados al editar; resultado explícitamente simulado/sin correos; proyecto y catálogo correctos | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-14 | PE: postulación | Eficiencia con 2 cupos; Reciclaje sin cupos | Verificar bloqueo en Reciclaje; abrir Solicitar unirse en Eficiencia; validar vacío/correo/datos ficticios; enviar | Cuatro obligatorios, proyecto correcto, confirmación azul con verificación/Entendido; cupos 2 y equipo 1 sin cambios | No ejecutado en teléfono | No ejecutado | Sin evidencia Android; LOG-02 prueba lógica/elegibilidad |
| UI-15 | PE: aporte y fuente opcional | Cualquier ficha | Abrir Entregar información; probar cuatro obligatorios/correo; enviar con fuente vacía, luego referencia libre y URL como texto | Validación correcta, no exige URL; confirmación naranja/simulada; Entendido conserva proyecto | No ejecutado en teléfono | No ejecutado | Sin evidencia Android; LOG-02 cubre fuente libre/opcional |
| UI-16 | HU3/PE: prevención de duplicados | Datos ficticios válidos en cada flujo | Pulsar rápidamente dos veces confirmar/enviar; rotar en resultado y volver | Una transición/confirmación, sin envío real ni cambio de cupos/equipo, sin repetición por recomposición | No se probaron pulsaciones ni rotación. Idempotencia de lógica aprobada solo en LOG-01/02/04 | No ejecutado | Sin evidencia Android |
| UI-17 | PE: cerrar y reabrir borradores | Ambos modales con datos ficticios | Cerrar por X, exterior/gesto y Atrás; reabrir. Repetir tras Entendido | Cancelar/finalizar limpia campos y errores; reapertura vacía, sin enviar | No ejecutado en teléfono | No ejecutado | Sin evidencia Android |
| UI-18 | HU4/PE: teclado y modales | Postulación y aporte abiertos por separado | Enfocar cada campo, incluido el último; desplazar hasta Enviar; Atrás con IME y después otra vez | Encabezado/X dentro del área segura, cuerpo/botón accesibles, foco visible; sin doble IME/insets; Atrás oculta teclado antes de cerrar | No se abrió IME ni se recorrieron modales | No ejecutado | Sin evidencia Android |
| UI-19 | HU4/EST: rotación/recreación Activity | Consultas/posición o formulario/resumen/errores activos | Rotar físicamente y regresar; revisar valores, errores y paso. Repetir en ambos modales | Se conserva el VM de cada propietario y posición; no se repite operación. Esto no prueba muerte de proceso | No se cambió orientación | No ejecutado | Sin evidencia Android; LOG-04 no sustituye esta prueba |
| UI-20 | HU4: fuente ampliada y horizontal | Registrar ajustes originales antes de probar | Ampliar fuente del sistema, probar horizontal y recorrer Inicio/catálogo/detalle/formularios; restaurar ajustes originales | Texto legible sin reducir fuente para encajar; scroll y cierre/envío accesibles, sin solapamientos/cortes | Solo se observó Inicio vertical a fuente 0.85; fuente ampliada/horizontal no probadas | No ejecutado | No hay captura en esas configuraciones |
| UI-21 | PREF: recordar filtros entre inicios nuevos | DataStore accesible; esperar cada guardado; sin borrar datos | Activar, Materiales/2026 y consulta; quitar solo tarea de Recientes y abrir desde icono. Desactivar: comprobar sesión; repetir inicio sin tarea | Activo restaura filtros y búsqueda vacía en inicio nuevo; desactivar mantiene sesión pero siguiente inicio usa Todos; no guarda texto libre | No se tocó el control ni se eliminó la tarea en esta ejecución | No ejecutado | Sin evidencia Android; LOG-03 prueba archivo/VM |
| UI-22 | EST: proceso nuevo con tarea restaurada | Tarea en Recientes con estado reconocible, recordar apagado y datos ficticios | HOME/onStop; anotar PID; `am kill` solo paquete en segundo plano; confirmar desaparición; abrir misma tarjeta y confirmar PID nuevo | Tipo/consultas/filtros/posición restaurados, preceden a DataStore. Contacto restaurado sin datos vuelve a formulario vacío; participación cerrada. Sin repetir eventos | No se mató proceso ni se recuperó tarea. «No conservar actividades» no se utilizó ni se considera equivalente | No ejecutado | Sin evidencia Android; LOG-04 solo simula entradas de handle |

## Evidencias y comandos ejecutados

Carpeta [validacion/mvp-20261021](validacion/mvp-20261021):

- `gradle.txt`: ejecución real de assembleDebug/lintDebug/testDebugUnitTest, BUILD SUCCESSFUL (1 s), 48 tareas (1 ejecutada, 47 al día).
- `lint.txt`: copia del informe de esta variante, 0 errores/9 advertencias.
- `logica-mvp.txt`, `historia2.txt`, `preferencias.txt`, `estado.txt`: salidas reales de los cuatro harness, no resúmenes inventados.
- `instalacion.txt`, `apertura.txt`: instalación y arranque Android reales.
- `version.txt`: SHA-256 de la APK y HEAD Git para identificar qué se probó.
- `dispositivo.txt`: entorno y observación real de que AppCIMUC no estaba al frente al detener los recorridos. No incluye contenidos de otras apps.
- `01-inicio.png` y `01-inicio.xml`: captura y jerarquía accesible reales de Inicio. El XML se obtuvo por UIAutomator y el PNG por screencap, no se generaron a partir del diseño.

Comandos desde la raíz (PowerShell), usando la infraestructura existente:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain
& ./docs/validacion/preferencias/EjecutarChecks.ps1
& ./docs/validacion/preferencias/EjecutarChecks.ps1 -Prueba docs/validacion/estado/EstadosChecks.kt -Clase EstadosChecksKt
$stdlib = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\2.0.21" -Recurse -Filter 'kotlin-stdlib-2.0.21.jar' | Select-Object -First 1 -ExpandProperty FullName
$rjar = Get-ChildItem 'app/build/intermediates' -Recurse -Filter 'R.jar' | Where-Object { $_.FullName -match 'processDebugResources' } | Select-Object -First 1 -ExpandProperty FullName
$cp = "app/build/tmp/kotlin-classes/debug;$stdlib;$rjar"
java --class-path $cp docs/validacion/mvp/MvpChecks.java
java --class-path $cp docs/validacion/historia2/Historia2Checks.java
```

El helper ADB de esta ejecución es temporal (`%LOCALAPPDATA%/Temp/opencode/PruebasMvpDevice_20261021.ps1`). No es código del prototipo. Comprueba la ventana al frente antes/después de lectura/captura y antes de tocar; acepta ventanas de Activity y Dialog del paquete. No se guardaron imágenes de otras aplicaciones. El intento de selección no confirmado no produjo una pulsación en otra ubicación supuesta.

## Capturas que faltan obtener

**Los nombres siguientes son una lista de trabajo; no existen como evidencias aprobadas.** Capturar solo después de ejecutar el caso y registrar el resultado. No sustituirlas por mockups ni por imágenes históricas.

| Capturas por obtener | Casos | Qué deben mostrar |
|---|---|---|
| `02-explorar-filtros.png` | UI-02/03 | Tipo Proyectos, Materiales/2026, consulta ENSAYOS y único resultado |
| `03-sin-coincidencias.png`, `04-limpieza.png` | UI-04/05 | Mensaje/limpieza y catálogo restablecido con tres proyectos |
| `05-capacidades.png`, `06-integrantes.png` | UI-06 | Búsqueda pertinente de cada tipo y consulta propia |
| `07-detalle-reciclaje.png`, `08-detalle-energia.png`, `09-detalle-mezclas.png` | UI-07 | Metadatos/contadores distintos; ampliar con captura inferior si no cabe la ficha |
| `10-capacidad-relacionada.png`, `11-integrante-relacionado.png`, `12-regreso-catalogo.png` | UI-08/09 | Detalles por ID y contexto/posición al volver |
| `13-recurso-demo.png`, `14-recursos-vacios.png` | UI-10/11 | Texto local real de demostración y estado vacío de Mezclas |
| `15-contacto-errores.png`, `16-resumen.png`, `17-contacto-editado.png`, `18-contacto-confirmado.png` | UI-12/13 | Errores junto a campos, datos ficticios conservados y simulación explícita |
| `19-postulacion.png`, `20-postulacion-confirmada.png` | UI-14 | Campos/proyecto, resultado azul y contadores sin cambios tras volver |
| `21-aporte.png`, `22-aporte-confirmado.png` | UI-15 | Respaldo libre/opcional, resultado naranja y sin correos |
| `23-postulacion-teclado.png`, `24-aporte-teclado.png`, `25-reapertura-vacia.png` | UI-17/18 | Encabezado/X, último campo/Enviar accesibles y borrador descartado |
| `26-rotacion.png`, `27-fuente-ampliada-horizontal.png` | UI-19/20 | Estado retenido y superficies adaptables con configuración anotada |
| `28-preferencia-activa.png`, `29-inicio-nuevo.png`, `30-preferencia-desactivada.png` | UI-21 | Valores antes/después de iniciar sin tarea y tras desactivar |
| `31-tarea-antes.png`, `32-tarea-restaurada.png` + registro PID/tarea | UI-22 | Mismo contexto con PID nuevo; las capturas solas no prueban muerte de proceso |

UI-16 requiere además un registro de pasos/pulsaciones rápidas y verificación de un solo resultado; una captura estática no demuestra prevención de duplicados. Para UI-19 se debe distinguir recreación Activity de proceso nuevo; para UI-21/22 seguir también [Estado y restauración](validacion/estado/EstadoYRestauracion.md#pasos-manuales-separados-sin-borrar-datos-ni-desinstalar).

## Errores pendientes, límites y correcciones

- **No hay un defecto funcional reproducido en los casos ejecutados.** Esto no significa que no haya errores en los 22 casos No ejecutados. No se modificó producción ni se agregaron pruebas por cantidad sin un riesgo nuevo identificado.
- Permanecen las nueve advertencias de lint, sin cambio de versiones ni eliminación de logo.xml para ocultarlas.
- Insignia/fotografías originales y Figma Make siguen ausentes; no se acredita fidelidad al prototipo. Inicio conserva el fallback visual existente y los recursos de demostración siguen identificados. No se amplía el producto para cubrir esas fuentes ausentes.
- No hay suite instrumentada integrada ni emulador configurado. El teléfono estuvo disponible para instalar/abrir/capturar Inicio, pero no se mantuvo al frente para completar los recorridos.
- No se probaron fuente ampliada, teclado, rotación, regreso con posición, DataStore en UI ni restauración real del proceso. No se borraron datos, desinstaló la app, cambió ajustes o efectuó envío real.
- Archivos de esta etapa: este informe, carpeta de evidencias `validacion/mvp-20261021`, README y DevLog. No hay cambios de código de producción, dependencias, versiones, modelo relacional ni datos de muestra.
- Mensaje de commit sugerido, **no ejecutado**: `Documenta pruebas del MVP y registra evidencia real de Inicio`.
