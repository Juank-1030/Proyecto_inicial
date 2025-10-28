# Script para ejecutar AppMapFullTest con AppMap en VS Code
# Uso: .\run_appmap.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "AppMap Test Execution" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Compilar
Write-Host "[1/3] Compilando..." -ForegroundColor Yellow
$curDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $curDir

javac -source 21 -target 21 -d bin shapes/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error compilando shapes" -ForegroundColor Red
    exit 1
}

javac -source 21 -target 21 -d bin -cp bin silkroad/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error compilando silkroad" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Compilación exitosa" -ForegroundColor Green
Write-Host ""

# Paso 2: Verificar AppMap
Write-Host "[2/3] Verificando AppMap..." -ForegroundColor Yellow
if (-not (Test-Path "appmap")) {
    Write-Host "⚠️  Directorio appmap no encontrado. Creando..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path "appmap" | Out-Null
}
Write-Host "✅ AppMap ready" -ForegroundColor Green
Write-Host ""

# Paso 3: Ejecutar con AppMap
Write-Host "[3/3] Ejecutando AppMapFullTest..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
java -cp bin silkroad.AppMapFullTest
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Ejecución completada" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Tip: Los archivos .appmap.json se generarán en el directorio appmap/" -ForegroundColor Cyan
