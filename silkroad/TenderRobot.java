package silkroad;

import shapes.*;

/**
 * TenderRobot es un robot especializado que toma solo la MITAD del dinero
 * que encuentra en las tiendas que visita.
 * 
 * Características:
 * - Extrae 50% del dinero (en lugar del 100% del robot normal)
 * - Deja la otra mitad en la tienda
 * - Hereda toda la lógica de movimiento de RobotAbstracto
 * - Tamaño y apariencia igual al Robot normal (15×15)
 * 
 * @author Sistema SilkRoad - Ciclo 3
 * @version 1.0
 */
public class TenderRobot extends RobotAbstracto {

    /**
     * Construye un robot TenderRobot con tamaño 15×15 y color aleatorio.
     * 
     * @param visible true para hacerlo visible tras construir
     */
    public TenderRobot(boolean visible) {
        super(visible);
        this.size = 15;
        this.positionX = 36;
        this.positionY = 58;
        this.color = getRandomColor();
        initializeVisualParts(); // Construye cabeza, ojos, boca
        if (visible) {
            makeVisible();
        }
        this.isVisible = visible;
    }

    /**
     * Actualiza las posiciones de las partes del robot basadas en positionX/Y
     * actuales (igual que Robot normal).
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
    @Override
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
     * POLIMORFISMO: TenderRobot recolecta SOLO LA MITAD de los tenges.
     * Este override implementa el comportamiento especial del TenderRobot.
     * 
     * ✅ MEDIATOR: Robot solo recibe cantidad, no conoce a Store
     * 
     * @param tengesDisponibles cantidad total de tenges en la tienda
     * @return la MITAD de los tenges disponibles
     */
    @Override
    protected int recolectarTenges(int tengesDisponibles) {
        return tengesDisponibles / 2;  // Toma solo la mitad
    }

    /**
     * FASE 4 OPCIÓN B: TenderRobot calcula ganancia con su lógica especial.
     * Como toma menos dinero, su ganancia es proporcional a la cantidad recolectada.
     * Ganancia = tenges recolectados (50% del disponible) - distancia recorrida
     * 
     * @param tengesRecolectados cantidad de tenges obtenidos (ya es 50%)
     * @param distancia metros recorridos
     * @return ganancia neta del TenderRobot (puede ser negativa)
     */
    @Override
    public int calcularGanancia(int tengesRecolectados, int distancia) {
        // TenderRobot usa la misma lógica, pero los tengesRecolectados ya son 50%
        return tengesRecolectados - distancia;
    }

}
