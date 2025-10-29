package silkroad;

import shapes.*;
import java.util.Random;

/**
 * Clase abstracta que define la estructura base para todos los robots en
 * SilkRoad.
 * Contiene la lógica común de movimiento, visualización y actualización de
 * partes.
 * 
 * Las subclases (Robot, FastRobot, HeavyRobot, etc.) solo necesitan:
 * - Inicializar las partes visuales en el constructor
 * - Implementar updateParts() con la lógica específica de su diseño
 * - Personalizar getSpeed() o makeVisible/makeInvisible si es necesario
 *
 * @author Sistema de Herencia - Ciclo 3
 * @version 1.0
 */
public abstract class RobotAbstracto {
    protected int positionX;
    protected int positionY;
    protected int size;
    protected String color;
    protected boolean isVisible;

    protected Rectangle robotHead;
    protected Circle leftEye;
    protected Circle rightEye;
    protected Rectangle mouth;

    /**
     * Constructor base para todos los robots.
     * 
     * @param visible estado inicial de visibilidad
     */
    public RobotAbstracto(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Mueve el robot a una posición específica mostrando el desplazamiento animado.
     * Llama a slowMoveTo() que hereda toda la lógica de animación.
     * 
     * @param x nueva coordenada X
     * @param y nueva coordenada Y
     */
    public void moveTo(int x, int y) {
        makeVisible();
        slowMoveTo(x, y);
    }

    /**
     * Movimiento gradual del robot desde su posición actual hasta (targetX,
     * targetY).
     * Usa getSpeed() para permitir personalización de velocidad en subclases.
     * Llama a updateParts() para que cada robot actualice sus partes de forma
     * específica.
     * 
     * Este método NO se duplica en subclases
     * Las subclases solo cambian getSpeed() o updateParts() si es necesario
     */
    protected void slowMoveTo(int targetX, int targetY) {
        int deltaX = targetX - positionX;
        int deltaY = targetY - positionY;

        int steps = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        if (steps == 0)
            return;

        int speed = getSpeed(); // ← HOOK: Personalizable por subclases

        double stepX = (double) deltaX / steps * speed;
        double stepY = (double) deltaY / steps * speed;

        for (int i = 0; i < steps; i += speed) {
            positionX += stepX;
            positionY += stepY;
            updateParts();
        }

        positionX = targetX;
        positionY = targetY;
        updateParts();
    }

    /**
     * Coloca el robot directamente en una ubicación específica sin animación.
     * 
     * @param x coordenada X donde aparecerá
     * @param y coordenada Y donde aparecerá
     */
    public void placeTo(int x, int y) {
        positionX = x;
        positionY = y;

        updateParts();
        isVisible = true;
    }

    /**
     * Inicializa todas las partes visuales del robot (cabeza, ojos, boca).
     * Este método CENTRALIZA la construcción visual para evitar duplicación.
     * 
     * LLAMAR en el constructor de cada subclase así:
     * super(visible);
     * this.size = 15; // Personalizar tamaño si es necesario
     * this.positionX = 36;
     * this.positionY = 58;
     * this.color = getRandomColor();
     * initializeVisualParts(); // ← Crea todas las partes
     * if (visible) { makeVisible(); }
     * this.isVisible = visible;
     * 
     * @param visible true para mostrar las partes inmediatamente
     */
    protected final void initializeVisualParts() {
        String eyeAndMouthColor = (color.equals("black") || color.equals("brown")) ? "white" : "black";

        robotHead = new Rectangle();
        robotHead.changeColor(color);
        robotHead.changeSize(size, size);
        robotHead.setPosition(positionX, positionY);

        int eyeSize = size / 6;
        leftEye = new Circle();
        leftEye.changeColor(eyeAndMouthColor);
        leftEye.changeSize(eyeSize);
        leftEye.setPosition(positionX + size - 14, positionY + size / 4);

        rightEye = new Circle();
        rightEye.changeColor(eyeAndMouthColor);
        rightEye.changeSize(eyeSize);
        rightEye.setPosition(positionX + size - 4, positionY + size / 4);

        mouth = new Rectangle();
        mouth.changeColor(eyeAndMouthColor);
        mouth.changeSize(1, 10);
        mouth.setPosition(positionX + size / 6, positionY + size - (size / 10) - 1);
    }

    /**
     * Hook para personalizar la velocidad de movimiento.
     * Las subclases pueden sobrescribir este método para cambiar velocidad.
     * 
     * @return velocidad de movimiento (mayor = más rápido)
     */
    protected int getSpeed() {
        return 5; // Velocidad por defecto
    }

    /**
     * Actualiza las posiciones de las partes del robot basadas en positionX/Y
     * actuales.
     * DEBE ser implementado por cada subclase con su lógica específica.
     * 
     * Esta es la única parte que cada robot debe personalizar
     */
    protected abstract void updateParts();

    /**
     * Hace visible el robot (todas sus partes) si no lo está.
     * Implementado por subclases.
     */
    public abstract void makeVisible();

    /**
     * Hace invisible el robot (todas sus partes) si está visible.
     * Implementado por subclases.
     */
    public abstract void makeInvisible();

    /**
     * Recolecta tenges de una tienda.
     * Este método define cómo cada robot recolecta dinero.
     * Por defecto, el robot recolecta TODOS los tenges disponibles.
     * Las subclases pueden sobrescribir para comportamiento especial.
     * 
     * MEDIATOR: Robot solo recibe cantidad, NO conoce a la Store
     * Road actúa como intermediario entre Robot y Store.
     * 
     * Ejemplo de polimorfismo:
     * - Robot normal: retorna todos los tenges
     * - TenderRobot: retorna la mitad
     * - FuturoRobot: retorna según su propia lógica
     * 
     * @param tengesDisponibles cantidad de tenges en la tienda
     * @return cantidad de tenges que el robot recolecta
     */
    protected int recolectarTenges(int tengesDisponibles) {
        // Implementación por defecto: robot normal toma todos
        return tengesDisponibles;
    }

    /**
     * Selecciona un color aleatorio para el robot.
     * Disponible para todas las subclases.
     * 
     * @return color elegido
     */
    protected String getRandomColor() {
        String[] colors = { "red", "blue", "green", "yellow", "magenta", "cyan", "black", "brown", "purple", "orange" };
        Random rand = new Random();
        return colors[rand.nextInt(colors.length)];
    }

    /**
     * @return coordenada X actual del robot
     */
    public int getX() {
        return positionX;
    }

    /**
     * @return coordenada Y actual del robot
     */
    public int getY() {
        return positionY;
    }

    /**
     * @return color actual del robot
     */
    public String getColor() {
        return color;
    }

    /**
     * @return true si el robot está visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Calcula la ganancia neta del robot al recolectar dinero.
     * Ganancia = dinero recolectado - costo de movimiento
     * 
     * Cada tipo de robot puede personalizar este cálculo si es necesario.
     * Por defecto: ganancia simple = dinero - distancia
     * 
     * @param tengesRecolectados cantidad de dinero recolectado de la tienda
     * @param distancia distancia recorrida (costo del movimiento)
     * @return ganancia neta (puede ser negativa)
     */
    public int calcularGanancia(int tengesRecolectados, int distancia) {
        return tengesRecolectados - distancia;
    }

    /**
     * Devuelve el tipo de robot como String para identificación.
     * Las subclases pueden sobrescribir para personalizar.
     * Por defecto retorna el nombre de la clase.
     * 
     * @return tipo de robot ("Robot", "TenderRobot", "NeverbackRobot", etc.)
     */
    public String getTipo() {
        return this.getClass().getSimpleName();
    }

    /**
     * Retorna el mensaje de rechazo cuando un robot no puede realizar una acción.
     * Por defecto no hay rechazo (retorna cadena vacía).
     * Las subclases especializadas pueden sobrescribir para personalizar el mensaje.
     * 
     * @return mensaje de rechazo personalizado para este tipo de robot
     */
    protected String obtenerMensajeRechazo() {
        return "";
    }

    /**
     * Valida si este robot puede realizar un movimiento especificado.
     * Por defecto, todos los robots pueden moverse en cualquier dirección.
     * Robots especializados como NeverbackRobot pueden sobrescribir para agregar restricciones.
     * 
     * @param meters desplazamiento propuesto
     * @return true si el movimiento es permitido, false si está bloqueado
     */
    protected boolean isMovementAllowed(int meters) {
        return true; // Por defecto, permite cualquier movimiento
    }

    /**
     * Reinicia el estado especial del robot después de completar una acción.
     * Por defecto, no hace nada.
     * Robots como NeverbackRobot pueden sobrescribir para restablecer su estado interno.
     */
    protected void resetDirection() {
        // Por defecto, no hace nada. Las subclases especializadas pueden sobrescribir.
    }
}
