package silkroad;

import shapes.*;

/**
 * Robot representa un robot estándar con tamaño 15×15.
 * Extiende RobotAbstracto para heredar toda la lógica de movimiento sin
 * duplicación.
 * 
 * ✅ Constructor SIMPLIFICADO: solo 12 líneas
 * ✅ Construcción visual centralizada en RobotAbstracto.initializeVisualParts()
 * ✅ Métodos visuales específicos: updateParts(), makeVisible(), makeInvisible()
 */
public class Robot extends RobotAbstracto {

    /**
     * Construye un robot estándar con tamaño 15×15 y color aleatorio.
     * 
     * ✅ Constructor SIMPLIFICADO: solo define parámetros
     * ✅ La construcción visual (ojos, boca, cabeza) está en initializeVisualParts()
     * 
     * @param visible true para hacerlo visible tras construir
     */
    public Robot(boolean visible) {
        super(visible);
        this.size = 15;
        this.positionX = 36;
        this.positionY = 58;
        this.color = getRandomColor();
        initializeVisualParts(); // ← Construye cabeza, ojos, boca
        if (visible) {
            makeVisible();
        }
        this.isVisible = visible;
    }

    /**
     * Actualiza las posiciones de las partes del robot basadas en positionX/Y
     * actuales.
     * ✅ Esta es la implementación específica del Robot estándar
     */
    @Override
    protected void updateParts() {
        robotHead.setPosition(positionX, positionY);
        leftEye.setPosition(positionX + size - 14, positionY + size / 4);
        rightEye.setPosition(positionX + size - 4, positionY + size / 4);
        mouth.setPosition(positionX + size / 6, positionY + size - (size / 10) - 1);

        robotHead.makeVisible();
        leftEye.makeVisible();
        rightEye.makeVisible();
        mouth.makeVisible();
    }

    /**
     * Hace visible el robot (todas sus partes) si no lo está.
     */
    @Override
    public void makeVisible() {
        if (!isVisible) {
            robotHead.makeVisible();
            leftEye.makeVisible();
            rightEye.makeVisible();
            mouth.makeVisible();
            isVisible = true;
        }
    }

    /**
     * Hace invisible el robot (todas sus partes) si está visible.
     */
    public void makeInvisible() {
        if (isVisible) {
            robotHead.makeInvisible();
            leftEye.makeInvisible();
            rightEye.makeInvisible();
            mouth.makeInvisible();
            isVisible = false;
        }
    }

    /**
     * POLIMORFISMO: Robot normal recolecta TODOS los tenges.
     * Este override documenta el comportamiento estándar.
     * 
     * ✅ MEDIATOR: Robot solo recibe cantidad, no conoce a Store
     * 
     * @param tengesDisponibles cantidad de tenges en la tienda
     * @return TODOS los tenges disponibles
     */
    @Override
    protected int recolectarTenges(int tengesDisponibles) {
        return tengesDisponibles;  // Toma todos
    }

    /**
     * FASE 4 OPCIÓN B: Robot normal calcula ganancia estándar.
     * Ganancia = tenges recolectados - distancia recorrida
     * 
     * @param tengesRecolectados cantidad de tenges obtenidos
     * @param distancia metros recorridos
     * @return ganancia neta (puede ser negativa)
     */
    @Override
    public int calcularGanancia(int tengesRecolectados, int distancia) {
        return tengesRecolectados - distancia;  // Ganancia estándar
    }

}