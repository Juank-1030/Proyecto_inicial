@echo off
REM Ejecutar test con AppMap
REM Comando: mvn test -Dappmap.output.directory=tmp/appmap

cd /d "%~dp0"

echo =====================================
echo Compilando proyecto...
echo =====================================

mvn clean compile

echo.
echo =====================================
echo Ejecutando test con AppMap...
echo =====================================
echo.

REM Ejecutar test con AppMap output
mvn test -Dappmap.output.directory=tmp/appmap

echo.
echo =====================================
echo Test completado!
echo Los archivos AppMap se encuentran en: tmp/appmap
echo =====================================
pause
