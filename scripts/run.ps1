<#
    Local run script (compile with javac, then start the console app).

    Usage:
        powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run.ps1

    Notes:
        - With Maven: mvn -q clean package && java -jar target/hospital-queue-system.jar
        - Any jar under lib/ is appended to the classpath (e.g. mysql-connector-j).
#>
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'build.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$classDir = Join-Path $root 'build\classes'
$classpath = $classDir
$libDir = Join-Path $root 'lib'
if (Test-Path $libDir) {
    $jars = @(Get-ChildItem -Path $libDir -Filter *.jar | ForEach-Object { $_.FullName })
    if ($jars.Count -gt 0) {
        $classpath = "$classDir;" + ($jars -join ';')
    }
}

& java "-Dfile.encoding=UTF-8" "-Dsun.stdout.encoding=UTF-8" "-Dsun.stderr.encoding=UTF-8" -cp $classpath com.lwf.hospital.Main
exit $LASTEXITCODE