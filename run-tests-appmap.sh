#!/bin/bash

# Script para ejecutar tests con AppMap en Codespace
# Este script genera diagramas de secuencia UML de los tests JUnit

set -e  # Exit on error

echo "================================================"
echo "SilkRoad - Ejecutar Tests con AppMap"
echo "================================================"
echo ""

# Paso 1: Limpiar y compilar
echo "✓ Compilando proyecto..."
mvn clean compile -q

echo "✓ Compilando tests..."
mvn test-compile -q

# Paso 2: Ejecutar tests con AppMap (capturar eventos)
echo "✓ Ejecutando tests con AppMap..."
mvn test -Dtest=AppMapFullTestJUnit -DargLine="-javaagent:$(mvn dependency:properties -q -Dmdep.outputFile=/dev/stdout 2>/dev/null | grep appmap.jar | cut -d= -f2)" 2>&1 | tee test-output.log

# Paso 3: Generar diagramas AppMap
if [ -d "tmp/appmap/junit" ]; then
    echo ""
    echo "✓ Generando diagramas de secuencia AppMap..."
    
    # Crear directorio para diagramas
    mkdir -p appmap-diagrams
    
    # Procesar cada archivo AppMap JSON
    for appmap_file in tmp/appmap/junit/*.json; do
        if [ -f "$appmap_file" ]; then
            basename=$(basename "$appmap_file" .json)
            echo "  → Procesando: $basename"
            
            # El AppMap CLI puede convertir a PlantUML
            appmap sequences "$appmap_file" --output-directory appmap-diagrams/ || true
        fi
    done
    
    echo ""
    echo "✓ Diagramas generados en: appmap-diagrams/"
    ls -lh appmap-diagrams/ 2>/dev/null || echo "  (No se generaron archivos)"
else
    echo ""
    echo "⚠ No se encontraron archivos AppMap en tmp/appmap/junit/"
    echo "  Esto es normal si los tests no fueron ejecutados con AppMap agent"
fi

echo ""
echo "================================================"
echo "Ejecución completada"
echo "================================================"
