package silkroad;

import shapes.*;
import java.util.Random;

/**
 * Clase abstracta que define la estructura base para todas las tiendas en
 * SilkRoad.
 * Contiene la lógica común de movimiento, visualización y posicionamiento.
 * 
 * Las subclases (Store, BigStore, ExpressStore, etc.) solo necesitan:
 * - Inicializar las partes visuales (base, techo) en el constructor
 * - Implementar makeVisible() y makeInvisible() si es necesario personalizar
 * - Personalizar getRandomColor() o métodos si es necesario
 *
 * @author Sistema de Herencia - Ciclo 3
 * @version 1.0
 */
public abstract class StoreAbstracto {
    protected int positionX;
    protected int positionY;
    protected int sizeRectangle;
    protected int sizeTriangle;
    protected String color;
    protected boolean isVisible;

    protected Rectangle base;
    protected Triangle roof;

    /**
     * Constructor base para todas las tiendas.
     * 
     * @param visible estado inicial de visibilidad
     */
    public StoreAbstracto(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Traslada la tienda a coordenadas absolutas y la hace visible.
     * 
     * @param x nueva coordenada X
     * @param y nueva coordenada Y
     */
    public void moveTo(int x, int y) {
        positionX = x;
        positionY = y;
        base.setPosition(positionX, positionY);
        roof.setPosition(positionX + 10, positionY - sizeTriangle / 2);
        base.makeVisible();
        roof.makeVisible();
    }

    /**
     * Reposiciona la tienda usando desplazamiento relativo.
     * 
     * @param x nueva coordenada X
     * @param y nueva coordenada Y
     */
    public void setPosition(int x, int y) {
        int dx = x - this.positionX;
        int dy = y - this.positionY;

        this.positionX = x;
        this.positionY = y;

        base.moveHorizontal(dx);
        base.moveVertical(dy);
        roof.moveHorizontal(dx);
        roof.moveVertical(dy);
    }

    /**
     * Cambia el color de la base de la tienda.
     * 
     * @param newColor nuevo color (string)
     */
    public void changeColor(String newColor) {
        this.color = newColor;
        base.changeColor(newColor);
    }

    /**
     * Hace visible la tienda (si no lo estaba).
     * Implementado por subclases si es necesario personalizar.
     */
    public abstract void makeVisible();

    /**
     * Hace invisible la tienda (si estaba visible).
     * Implementado por subclases si es necesario personalizar.
     */
    public abstract void makeInvisible();

    /**
     * Selecciona un color aleatorio para la tienda.
     * Disponible para todas las subclases.
     * 
     * @return color elegido
     */
    protected String getRandomColor() {
        String[] colors = { "red", "blue", "green", "yellow", "magenta", "cyan", "purple", "orange" };
        Random rand = new Random();
        return colors[rand.nextInt(colors.length)];
    }

    /**
     * @return coordenada X actual de la tienda
     */
    public int getX() {
        return positionX;
    }

    /**
     * @return coordenada Y actual de la tienda
     */
    public int getY() {
        return positionY;
    }

    /**
     * @return color actual de la tienda
     */
    public String getColor() {
        return color;
    }

    /**
     * @return true si la tienda está visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    // ============== NUEVOS MÉTODOS PARA DELEGACIÓN - CON MEDIATOR ==============

    /**
     * ✅ PATRÓN MEDIATOR: Store valida la transferencia.
     * Store no conoce al robot específico, solo valida la cantidad solicitada.
     * Road actúa como intermediario.
     * 
     * @param tengesRecolectados cantidad de tenges que el robot recolectó
     * @return cantidad real de tenges que la tienda transfiere (puede ser diferente)
     */
    protected int validarTransferencia(int tengesRecolectados) {
        // Implementación por defecto: Store transfiere lo que el robot solicitó
        // Subclases pueden override para implementar lógica personalizada
        return tengesRecolectados;
    }

    /**
     * Transfiere tenges de la tienda al robot.
     * Puede ser sobrescrito por subclases para implementar lógica personalizada
     * (ej: BigStore que da más, ExpressStore que cobra comisión, etc.)
     * 
     * @param cantidadSolicitada cantidad de tenges solicitados
     * @return cantidad real de tenges que la tienda transfiere
     */
    public int transferirTenges(int cantidadSolicitada) {
        // Implementación por defecto: transfiere todo lo solicitado
        // Subclases pueden override para agregar lógica personalizada
        return cantidadSolicitada;
    }

    /**
     * @return cantidad actual de tenges disponibles en la tienda
     */
    public int getTengesActuales() {
        // Por ahora, StoreAbstracto no gestiona tenges internamente
        // Subclases pueden override si necesitan gestionar inventario
        return 0; // Placeholder
    }

    // ============== NUEVOS MÉTODOS PARA LÓGICA DE TIENDAS - FASE 5 ==============

    /**
     * ✅ SINGLE RESPONSIBILITY: Cada tienda decide si puede recibir al robot.
     * Delegación completa: SilkRoad no hace instanceof ni validaciones especiales.
     * 
     * Flujo:
     * 1. Store determina si acepta al robot (puede tener restricciones)
     * 2. Si NO acepta, retorna false (robot no recibe tenges)
     * 3. Si SÍ acepta, continúa con la transferencia
     * 
     * Ejemplos de override:
     * - Store normal: siempre retorna true (acepta todos)
     * - FighterStore: retorna false si robot es débil (tenges <= storeTenges)
     * - ProtectedStore: retorna false si robot no pasa verificación, etc.
     * 
     * @param robotTenges tenges actuales del robot
     * @param storeTenges tenges de la tienda
     * @return true si la tienda acepta el robot, false si lo rechaza
     */
    protected boolean puedeRecibirRobot(int robotTenges, int storeTenges) {
        // Implementación por defecto: Todas las tiendas aceptan robots
        // Subclases pueden override para agregar restricciones
        return true;
    }

    /**
     * ✅ OPEN/CLOSED: Cada tienda procesa la transferencia según su lógica.
     * SilkRoad solo orquesta, no implementa lógica de transferencia.
     * 
     * Flujo:
     * 1. Store recibe cantidad de tenges que el robot recolectó
     * 2. Store aplica su lógica (puede rechazar, cobrar comisión, etc.)
     * 3. Store retorna cantidad final que transfiere
     * 
     * Ejemplos de override:
     * - Store normal: transfiere exactamente lo solicitado
     * - BigStore: transfiere 150% de lo solicitado
     * - ExpressStore: cobra 10% de comisión
     * - VaultStore: rechaza si cantidad > umbral
     * 
     * @param tengesRecolectados cantidad de tenges que el robot recolectó
     * @return cantidad final de tenges a transferir (puede ser 0, 100%, 150%, etc.)
     */
    protected int procesarTransferencia(int tengesRecolectados) {
        // Implementación por defecto: transfiere exactamente lo recolectado
        // Subclases pueden override para personalizar
        return tengesRecolectados;
    }

    /**
     * ✅ ENCAPSULACIÓN: Cada tienda genera su propio mensaje de rechazo.
     * SilkRoad no sabe QUÉ tienda rechaza ni POR QUÉ.
     * 
     * Flujo:
     * 1. SilkRoad pregunta: ¿Aceptas este robot?
     * 2. Si respuesta es NO, SilkRoad pide el mensaje
     * 3. Tienda retorna su mensaje personalizado
     * 4. SilkRoad solo muestra el mensaje (agnóstico)
     * 
     * Ejemplos:
     * - Store: retorna "" (nunca rechaza, nunca muestra mensaje)
     * - FighterStore: retorna "¡Robot débil! No tienes poder suficiente..."
     * - VaultStore: retorna "¡Cantidad muy alta! Supera el límite de la bóveda..."
     * 
     * @param robotTenges tenges actuales del robot
     * @param storeTenges tenges de la tienda
     * @return mensaje personalizado del rechazo (vacío si no rechaza)
     */
    public String obtenerMensajeRechazo(int robotTenges, int storeTenges) {
        // Implementación por defecto: Store acepta todos los robots
        // No hay rechazo, por lo tanto no hay mensaje
        return "";
    }

    /**
     * Indica si esta tienda necesita una posición aleatoria.
     * Por defecto, todas las tiendas usan la ubicación especificada.
     * Solo tiendas especiales como AutonomousStore sobrescriben para retornar true.
     * 
     * @return true si necesita posición aleatoria, false si usa la ubicación especificada
     */
    protected boolean necesitaPosicionAleatoria() {
        return false;  // Por defecto, acepta ubicación especificada
    }
}
