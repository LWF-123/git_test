<#
    Local build script that only needs JDK 17 (no Maven required).
    If Maven is available, prefer: mvn -q clean package

    Usage:
        powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1
#>
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$sourceDir = Join-Path $root 'src\main\java'
$resourceDir = Join-Path $root 'src\main\resources'
$classDir = Join-Path $root 'build\classes'

if (Test-Path $classDir) { Remove-Item -Recurse -Force $classDir }
New-Item -ItemType Directory -Force -Path $classDir | Out-Null

$sources = @(Get-ChildItem -Path $sourceDir -Recurse -Filter *.java | ForEach-Object { $_.FullName })
if ($sources.Count -eq 0) { throw "No java sources found under $sourceDir" }

# javac cannot parse a BOM in the argument file, so write it as BOM-less UTF-8.
$argFile = Join-Path $root 'build\sources.txt'
New-Item -ItemType Directory -Force -Path (Split-Path $argFile) | Out-Null
[System.IO.File]::WriteAllLines($argFile, $sources, [System.Text.UTF8Encoding]::new($false))

Write-Host "Compiling $($sources.Count) source files ..." -ForegroundColor Cyan
& javac -encoding UTF-8 -d $classDir "@$argFile"
if ($LASTEXITCODE -ne 0) { throw "javac failed (exit=$LASTEXITCODE)" }

if (Test-Path $resourceDir) {
    Copy-Item -Path (Join-Path $resourceDir '*') -Destination $classDir -Recurse -Force
}

Write-Host "Build finished: $classDir" -ForegroundColor Green
Write-Host "Hint: put mysql-connector-j-*.jar into lib/ (or use Maven) to run against MySQL." -ForegroundColor Yellow