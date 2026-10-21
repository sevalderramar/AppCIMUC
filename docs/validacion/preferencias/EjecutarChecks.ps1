# Ejecutar desde la raíz después de assembleDebug. Usa exclusivamente dependencias ya resueltas por Gradle.
param(
    [string]$Prueba = 'docs/validacion/preferencias/PreferenciasChecks.kt',
    [string]$Clase = 'PreferenciasChecksKt'
)
$ErrorActionPreference = 'Stop'
$cache = "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1"
$temporal = Join-Path $env:LOCALAPPDATA 'Temp/opencode'
$salida = Join-Path $temporal ("preferencias-harness-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory $salida | Out-Null
function Jar([string]$grupo, [string]$artefacto, [string]$version) {
    $archivo = Get-ChildItem "$cache/$grupo/$artefacto/$version" -Recurse -Filter '*.jar' |
        Where-Object { $_.Name -notmatch 'sources|javadoc' } | Select-Object -First 1
    if (-not $archivo) { throw "Falta $artefacto $version en caché; compilar primero." }
    return $archivo.FullName
}
$stdlib = Jar 'org.jetbrains.kotlin' 'kotlin-stdlib' '2.0.21'
$coroutines = Jar 'org.jetbrains.kotlinx' 'kotlinx-coroutines-core-jvm' '1.7.3'
$annotations = Jar 'org.jetbrains' 'annotations' '13.0'
$compiler = @(
    (Jar 'org.jetbrains.kotlin' 'kotlin-compiler-embeddable' '2.0.21'), $stdlib,
    (Jar 'org.jetbrains.kotlin' 'kotlin-script-runtime' '2.0.21'),
    (Jar 'org.jetbrains.kotlin' 'kotlin-reflect' '1.6.10'),
    (Jar 'org.jetbrains.intellij.deps' 'trove4j' '1.0.20200330'), $coroutines, $annotations
) -join ';'
$libs = @($stdlib, $coroutines, $annotations,
    (Jar 'androidx.datastore' 'datastore-preferences-core-jvm' '1.1.1'),
    (Jar 'androidx.datastore' 'datastore-core-okio-jvm' '1.1.1'),
    (Jar 'com.squareup.okio' 'okio-jvm' '3.4.0'))
Add-Type -AssemblyName System.IO.Compression.FileSystem
foreach ($artefacto in @('datastore-android', 'datastore-core-android', 'datastore-preferences-android')) {
    $aar = Get-ChildItem "$cache/androidx.datastore/$artefacto/1.1.1" -Recurse -Filter '*.aar' | Select-Object -First 1
    $zip = [IO.Compression.ZipFile]::OpenRead($aar.FullName)
    $destino = Join-Path $salida "$artefacto.jar"
    try { [IO.Compression.ZipFileExtensions]::ExtractToFile($zip.GetEntry('classes.jar'), $destino) }
    finally { $zip.Dispose() }
    $libs += $destino
}
$aarViewModel = Get-ChildItem "$cache/androidx.lifecycle/lifecycle-viewmodel-android/2.8.7" -Recurse -Filter '*.aar' | Select-Object -First 1
$zip = [IO.Compression.ZipFile]::OpenRead($aarViewModel.FullName)
$destino = Join-Path $salida 'lifecycle-viewmodel.jar'
try { [IO.Compression.ZipFileExtensions]::ExtractToFile($zip.GetEntry('classes.jar'), $destino) }
finally { $zip.Dispose() }
$libs += $destino
foreach ($modulo in @(
    @('androidx.lifecycle', 'lifecycle-viewmodel-savedstate', '2.8.7'),
    @('androidx.savedstate', 'savedstate', '1.2.1'),
    @('androidx.lifecycle', 'lifecycle-livedata-core', '2.8.7')
)) {
    $aar = Get-ChildItem "$cache/$($modulo[0])/$($modulo[1])/$($modulo[2])" -Recurse -Filter '*.aar' | Select-Object -First 1
    $zip = [IO.Compression.ZipFile]::OpenRead($aar.FullName)
    $destino = Join-Path $salida "$($modulo[1]).jar"
    try { [IO.Compression.ZipFileExtensions]::ExtractToFile($zip.GetEntry('classes.jar'), $destino) }
    finally { $zip.Dispose() }
    $libs += $destino
}
$rjar = Get-ChildItem 'app/build/intermediates' -Recurse -Filter 'R.jar' |
    Where-Object { $_.FullName -match 'processDebugResources' } | Select-Object -First 1 -ExpandProperty FullName
$clasesApp = (Resolve-Path 'app/build/tmp/kotlin-classes/debug').Path
$android = "$env:LOCALAPPDATA/Android/Sdk/platforms/android-35/android.jar"
$cp = (@($clasesApp, $rjar, $android) + $libs) -join ';'
& java --class-path $compiler org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect `
    -jvm-target 17 "-Xfriend-paths=$clasesApp" -classpath $cp -d $salida `
    $Prueba
if ($LASTEXITCODE -ne 0) { throw 'Falló compilación del harness' }
& java --class-path "$salida;$cp" $Clase $temporal
if ($LASTEXITCODE -ne 0) { throw 'Falló harness de preferencias' }
