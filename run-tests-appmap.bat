@echo off
REM Script para ejecutar tests con AppMap en Windows
REM Requiere: Java 21+, Maven 3.9+, AppMap CLI (opcional)

setlocal enabledelayedexpansion

echo.
echo ================================================
echo SilkRoad - Ejecutar Tests con AppMap
echo ================================================
echo.

REM Paso 1: Limpiar y compilar
echo [*] Compilando proyecto...
call mvn clean compile -q
if errorlevel 1 (
    echo [ERROR] Compilacion fallida
    exit /b 1
)

echo [OK] Compilacion exitosa

REM Paso 2: Ejecutar tests
echo [*] Ejecutando tests JUnit...
call mvn test -Dtest=AppMapFullTestJUnit
if errorlevel 1 (
    echo [ADVERTENCIA] Algunos tests pueden haber fallado
)

echo.
echo [OK] Tests completados

REM Paso 3: Mostrar resultados
if exist "target\surefire-reports\*Test.txt" (
    echo.
    echo [*] Resultados de tests:
    type "target\surefire-reports\TEST-test.AppMapFullTestJUnit.txt" 2>nul
)

REM Paso 4: Verificar AppMap
if exist "tmp\appmap\junit" (
    echo.
    echo [OK] Archivos AppMap encontrados en tmp\appmap\junit
    dir tmp\appmap\junit
) else (
    echo.
    echo [NOTA] No se encontraron archivos AppMap
    echo        Para usar AppMap en Windows, instala AppMap CLI:
    echo        npm install -g @appmap/cli
)

echo.
echo ================================================
echo Ejecucion completada
echo ================================================
echo.
pause
