package silkroad;

/**
 * NeverbackRobot es un robot especial que solo se mueve en una dirección.
 * 
 * Comportamiento:
 * - Si realiza un movimiento POSITIVO primero, solo aceptará movimientos
 * POSITIVOS
 * - Si realiza un movimiento NEGATIVO primero, solo aceptará movimientos
 * NEGATIVOS
 * - Una vez establecida la dirección, no puede cambiar (de ahí el nombre
 * "Neverback")
 * - Intenta rechazar movimientos en la dirección opuesta
 * 
 * Visualmente: Robot tamaño 18×18 con color verde, para diferenciarlo del Robot
 * estándar
 * 
 * @author Sistema de Robots Especiales - Ciclo 3
 * @version 1.0
 */
public class NeverbackRobot extends RobotAbstracto {

    private int directionLocked = 0; // 0=sin bloquear, 1=positivo, -1=negativo
    private int lastAttemptedMeters = 0; // Guarda el último intento de movimiento rechazado

    /**
     * Construye un NeverbackRobot con tamaño 18×18 y color verde.
     * 
     * @param visible true para hacerlo visible tras construir
     */
    public NeverbackRobot(boolean visible) {
        super(visible);
        this.size = 15;
        this.positionX = 36;
        this.positionY = 58;
        this.color = "green"; // Color distintivo para diferenciarlo
        this.directionLocked = 0; // Sin dirección bloqueada inicialmente
        initializeVisualParts();
        if (visible) {
            makeVisible();
        }
        this.isVisible = visible;
    }

    /**
     * Devuelve la dirección bloqueada del robot.
     * 
     * @return 0 si sin bloquear, 1 si positivo, -1 si negativo
     */
    public int getDirectionLocked() {
        return directionLocked;
    }

    /**
     * Establece la dirección inicial del robot.
     * Una vez establecida, no puede cambiar.
     * 
     * @param direction 1 para positivo, -1 para negativo
     */
    public void setDirectionLocked(int direction) {
        if (direction == 1 || direction == -1) {
            this.directionLocked = direction;
        }
    }

    /**
     * Valida si un movimiento es permitido según la dirección bloqueada.
     * Si directionLocked es 0 (sin bloquear), permite cualquier movimiento.
     * Si directionLocked es 1 (positivo), solo permite meters > 0.
     * Si directionLocked es -1 (negativo), solo permite meters < 0.
     * 
     * @param meters desplazamiento propuesto
     * @return true si el movimiento es permitido, false si está bloqueado
     */
    public boolean isMovementAllowed(int meters) {
        if (directionLocked == 0) {
            // Sin dirección bloqueada, permite cualquier movimiento
            return true;
        }

        boolean allowed;
        if (directionLocked == 1) {
            // Bloqueado en positivo: solo permite meters > 0
            allowed = meters > 0;
        } else if (directionLocked == -1) {
            // Bloqueado en negativo: solo permite meters < 0
            allowed = meters < 0;
        } else {
            allowed = true;
        }

        // Guardar el intento si es rechazado
        if (!allowed) {
            this.lastAttemptedMeters = meters;
        }

        return allowed;
    }

    /**
     * Bloquea la dirección del robot según el signo del movimiento.
     * Si el robot nunca se ha movido (directionLocked == 0), establece la dirección
     * según el signo de meters.
     * 
     * @param meters desplazamiento que determina la dirección
     */
    public void lockDirection(int meters) {
        if (directionLocked == 0) {
            if (meters > 0) {
                directionLocked = 1; // Bloqueado en positivo
            } else if (meters < 0) {
                directionLocked = -1; // Bloqueado en negativo
            }
        }
    }

    /**
     * Reinicia el bloqueo de dirección del robot.
     * Utilizado cuando el robot es devuelto a su posición original (en reboot).
     */
    public void resetDirection() {
        this.directionLocked = 0;
    }

    /**
     * Actualiza las posiciones de las partes del robot basadas en positionX/Y
     * actuales.
     * NeverbackRobot es un poco más grande (18×18) para ser visualmente distintivo.
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
     * Hace visible el robot en la pantalla.
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
     * Oculta el robot de la pantalla.
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
     * POLIMORFISMO: NeverbackRobot recolecta TODOS los tenges (como normal).
     * Este override documenta que NeverbackRobot no tiene restricciones en recolección.
     * 
     * ✅ MEDIATOR: Robot solo recibe cantidad, no conoce a Store
     * 
     * @param tengesDisponibles cantidad de tenges en la tienda
     * @return TODOS los tenges disponibles
     */
    @Override
    protected int recolectarTenges(int tengesDisponibles) {
        return tengesDisponibles;  // Toma todos (sin restricciones)
    }

    /**
    * Calcula la ganancia neta del robot tras visitar una tienda.
    *
    * Regla de negocio actual: la ganancia es la cantidad de tenges que
    * recolectó el robot menos la distancia absoluta recorrida para llegar
    * a la tienda. No se aplica ningún "descuento" por eficiencia.
    *
    * Fórmula: ganancia = tengesRecolectados - |distancia|
    *
    * @param tengesRecolectados cantidad de tenges obtenidos de la tienda
    * @param distancia desplazamiento (puede ser negativo o positivo)
    * @return ganancia neta (tenges recolectados menos distancia absoluta)
     */
    @Override
    public int calcularGanancia(int tengesRecolectados, int distancia) {
        // Ahora la ganancia es: tenges recolectados menos la distancia absoluta recorrida.
        // Es decir, no hay 'descuento' por eficiencia aquí: se resta la distancia real.
        int distanciaAbs = Math.abs(distancia);
        return tengesRecolectados - distanciaAbs;
    }

    /**
     * Retorna el mensaje de rechazo cuando NeverbackRobot está bloqueado.
     * Proporciona información sobre la dirección bloqueada y el tipo de movimiento intentado.
     * 
     * @return mensaje personalizado explicando el bloqueo de dirección
     */
    @Override
    protected String obtenerMensajeRechazo() {
        String directionName = this.directionLocked == 1 ? "positiva" : "negativa";
        return "¡Movimiento bloqueado!\n\n" +
                "Este NeverbackRobot está bloqueado en dirección " + directionName + ".\n" +
                "Solo puede moverse con números " + directionName + "s.\n\n" +
                "Intento actual: " + (lastAttemptedMeters > 0 ? "positivo (" : "negativo (") + lastAttemptedMeters + ")";
    }
}

