# AppCIMUC

Aplicación Android para explorar proyectos, capacidades de investigación y desarrollo e integrantes del Centro UC de Innovación en Madera (CIM UC).

Permite buscar contenidos, consultar sus relaciones y completar solicitudes simuladas de contacto, participación y aporte de información.

El proyecto corresponde a un MVP académico desarrollado para la asignatura **DSY1105 · Desarrollo de Aplicaciones Móviles**. Utiliza datos sintéticos y no realiza trámites ni envíos a sistemas institucionales.

## Objetivo

Facilitar el descubrimiento y consulta de información sobre proyectos y capacidades de innovación en madera mediante una experiencia móvil clara.

La aplicación permite identificar proyectos de interés, conocer sus capacidades y equipo asociado y expresar interés en colaborar mediante formularios simulados.

## Funcionalidades

### Inicio

- Presentación de la aplicación.
- Indicadores calculados desde el catálogo.
- Proyectos destacados.
- Acceso directo a la exploración de contenidos.

### Exploración

- Catálogo organizado en **Proyectos**, **Capacidades** e **Integrantes**.
- Búsqueda por texto.
- Filtros por categoría y año para proyectos.
- Contador de resultados.
- Limpieza de búsqueda y filtros.
- Mensajes cuando no existen coincidencias.

### Detalle de proyectos

- Título y descripción.
- Categoría, año y estado.
- Disponibilidad de cupos cuando se conoce.
- Capacidades e integrantes relacionados.
- Acceso a recursos de demostración disponibles.
- Acciones de contacto, postulación y aporte de información.

Las capacidades y los integrantes cuentan con fichas propias que permiten consultar sus proyectos relacionados.

### Contacto simulado

El formulario solicita:

- Nombre.
- Correo electrónico.
- Mensaje.

Los datos se validan antes de mostrar un resumen. El usuario puede regresar para editarlos y confirmar la solicitud simulada.

### Postulación a proyectos

Disponible para proyectos que aceptan postulaciones y tienen cupos.

El formulario solicita:

- Nombre completo.
- Correo electrónico.
- Institución o universidad.
- Motivación y competencias relevantes.

La confirmación es simulada y no modifica los cupos ni incorpora integrantes al proyecto.

### Aporte de información

El formulario solicita:

- Nombre.
- Correo de contacto.
- Asunto o tipo de información.
- Contenido del aporte.
- Fuente o respaldo opcional.

La aplicación valida los campos y muestra una confirmación simulada. No realiza una evaluación institucional ni envía información al equipo del centro.

## Flujo de uso

1. Abrir **Inicio**.
2. Seleccionar **Explorar proyectos** o la opción **Explorar** de la navegación inferior.
3. Elegir el tipo de contenido.
4. Buscar y aplicar los filtros disponibles.
5. Abrir una ficha para consultar su información.
6. Navegar hacia capacidades, integrantes o recursos relacionados.
7. Completar alguna de las acciones disponibles en el proyecto.
8. Regresar al catálogo para continuar la exploración.

## Catálogo de demostración

La muestra incluida contiene tres proyectos, dos categorías, dos integrantes y dos capacidades.

| Proyecto | Año | Estado | Cupos |
|---|---|---|---|
| Materiales reciclados | 2025 | Finalizado | Sin cupos |
| Eficiencia de procesos | 2026 | En desarrollo | 2 |
| Diseño de mezclas | 2026 | En desarrollo | 1 |

Los datos están definidos en:

`app/src/main/java/com/example/appcimuc/repository/CatalogoEjemplo.kt`

Las relaciones entre proyectos, capacidades e integrantes se representan mediante identificadores.

Los documentos de demostración se encuentran en:

`app/src/main/res/raw/`

## Arquitectura

La aplicación utiliza una separación de responsabilidades basada en **MVVM**.

| Capa | Responsabilidad |
|---|---|
| `model` | Define las entidades del catálogo, recursos y preferencias. |
| `repository` | Proporciona datos, resuelve relaciones y administra el acceso a DataStore. |
| `viewmodel` | Gestiona el estado, las búsquedas, los filtros, las validaciones y los formularios. |
| `ui` | Representa el estado mediante Jetpack Compose y comunica acciones mediante callbacks. |

El flujo de interacción es:

**Acción del usuario → callback → ViewModel → lógica o repositorio → actualización del estado → interfaz**

### ViewModels principales

- **MainViewModel:** administra el catálogo, la exploración y las preferencias de filtros.
- **ContactoViewModel:** comparte el estado entre formulario, resumen y confirmación del contacto.
- **ParticipacionViewModel:** gestiona los formularios de postulación y aporte asociados a un proyecto.

El estado de pantalla se expone mediante `StateFlow`.

## Estado y persistencia

La aplicación distingue entre estado temporal, restaurable y persistente.

| Información | Mecanismo | Comportamiento |
|---|---|---|
| Catálogo | Datos definidos en Kotlin | Se obtiene desde la muestra incluida en la aplicación. |
| Búsqueda, filtros y tipo de contenido | ViewModel y SavedStateHandle | Permite conservar valores durante el uso y restaurarlos cuando Android recupera la tarea. |
| Posición de los listados | Estado de interfaz restaurable | Mantiene el contexto de exploración según el ciclo de vida de la pantalla. |
| Preferencias de filtros | Preferences DataStore | Conserva la preferencia y los filtros habilitados entre aperturas. |
| Borradores de formularios | ViewModel en memoria | Se conservan durante rotación, pero pueden perderse si termina el proceso. |

### Recordar filtros de proyectos

La opción está desactivada por defecto.

Cuando se activa, la aplicación guarda:

- La preferencia de recordar filtros.
- La categoría seleccionada.
- El año seleccionado.

Al desactivarla se eliminan los filtros persistidos.

La búsqueda libre, los nombres, los correos y el contenido de los formularios no se guardan en DataStore.

La restauración de una tarea tiene prioridad sobre los filtros persistidos. En un inicio nuevo se aplican las preferencias guardadas cuando corresponde.

## Estructura del proyecto

```text
AppCIMUC/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/appcimuc/
│       │   ├── MainActivity.kt
│       │   ├── model/
│       │   ├── repository/
│       │   ├── viewmodel/
│       │   └── ui/
│       │       ├── navigation/
│       │       └── theme/
│       ├── assets/licenses/
│       └── res/
│           ├── drawable/
│           ├── font/
│           ├── raw/
│           └── values/
├── docs/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## Tecnologías

| Componente | Versión o configuración |
|---|---|
| Lenguaje | Kotlin 2.0.21 |
| Interfaz | Jetpack Compose |
| Componentes visuales | Material 3 |
| Android Gradle Plugin | 8.7.3 |
| Gradle Wrapper | 8.9 |
| Java / JVM | 17 |
| Compose BOM | 2024.12.01 |
| Navigation Compose | 2.8.5 |
| Lifecycle ViewModel Compose | 2.8.7 |
| Preferences DataStore | 1.1.1 |
| Android mínimo | API 24 · Android 7.0 |
| compileSdk | 35 |
| targetSdk | 35 |

**Identificador de aplicación:** `com.example.appcimuc`

## Requisitos de ejecución

- Android Studio compatible con la configuración del proyecto.
- JDK 17.
- Android SDK Platform 35.
- Dispositivo o emulador con Android 7.0 o superior.
- Conexión a internet para descargar Gradle y dependencias durante la configuración inicial.

Las funciones actuales utilizan datos y recursos locales y no requieren un servidor.

## Instalación y ejecución

### Desde Android Studio

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/sevalderramar/AppCIMUC.git
   ```

2. Abrir la carpeta `AppCIMUC` en Android Studio.
3. Configurar el JDK de Gradle en Java 17.
4. Sincronizar el proyecto.
5. Instalar los componentes del SDK solicitados.
6. Seleccionar un emulador o dispositivo físico.
7. Ejecutar la configuración `app`.

### Dispositivo físico

Activar las opciones de desarrollador y la depuración correspondiente.

Puede utilizarse:

- Conexión USB, autorizando el computador desde el teléfono.
- Depuración inalámbrica, si el dispositivo la admite.

## Compilación

Desde PowerShell, en la raíz del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
```

En Linux o macOS:

```bash
bash gradlew :app:assembleDebug
```

## Análisis estático

Desde PowerShell:

```powershell
.\gradlew.bat :app:lintDebug
```

En Linux o macOS:

```bash
bash gradlew :app:lintDebug
```

El informe se genera en:

`app/build/reports/lint-results-debug.html`

## Pruebas

Los casos y evidencias disponibles se encuentran en:

[Pruebas del MVP](docs/PRUEBAS_MVP.md)

Las verificaciones contemplan:

- Búsqueda y combinación de filtros.
- Resultados vacíos y limpieza.
- Consulta de fichas y relaciones.
- Visualización de recursos.
- Validaciones de formularios.
- Edición desde el resumen de contacto.
- Confirmaciones simuladas.
- Prevención de envíos duplicados.
- Adaptación de ventanas al teclado.
- Rotación y conservación del estado.
- Preferencias guardadas con DataStore.

El registro del 21 de octubre de 2026 documenta compilación, análisis estático, comprobaciones de lógica y apertura en un dispositivo Android. También identifica recorridos de interfaz pendientes de ejecución.

La compilación y el análisis estático no sustituyen las pruebas de interacción en dispositivo.

## Diseño

La interfaz utiliza la siguiente paleta:

| Uso | Color |
|---|---|
| Principal | `#1A3C5E` |
| Acento | `#B5621A` |
| Fondo | `#F4F1EC` |
| Tarjetas | `#FFFFFF` |
| Texto principal | `#1A1A1A` |
| Texto secundario | `#7A7267` |
| Bordes | `#DDD8CE` |

Las tipografías incluidas son:

- **Plus Jakarta Sans:** títulos y elementos destacados.
- **Inter:** texto y campos.

Sus licencias se encuentran en:

`app/src/main/assets/licenses/`

## Alcance y limitaciones

- El catálogo utiliza información sintética para demostración.
- La aplicación no reemplaza los canales institucionales del CIM UC.
- No incluye autenticación ni registro de cuentas.
- No envía correos ni solicitudes reales.
- No ejecuta evaluaciones administrativas.
- No almacena permanentemente contactos, postulaciones o aportes.
- No utiliza bases de datos ni servicios productivos.
- Las fotografías de proyectos son opcionales y no están asignadas en la muestra actual.
- La cobertura completa de interacción y adaptación visual requiere finalizar las pruebas pendientes.