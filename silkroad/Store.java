package silkroad;

import shapes.*;

/**
 * Store representa una tienda compuesta por una base rectangular y un techo
 * triangular.
 * El color de la base se elige aleatoriamente entre varios.
 * Proporciona métodos para cambiar color, mover y visibilidad.
 *
 * Extiende StoreAbstracto para heredar toda la lógica de movimiento sin
 * duplicación.
 */
public class Store extends StoreAbstracto {

    /**
     * Construye la tienda en una posición por defecto.
     * 
     * @param visible true para hacerla visible inmediatamente
     */
    public Store(boolean visible) {
        super(visible);
        this.positionX = 54;
        this.positionY = 45;
        this.sizeRectangle = 20;
        this.sizeTriangle = 20;
        this.color = getRandomColor();

        base = new Rectangle();
        base.changeColor(color);
        base.changeSize(sizeRectangle / 2, sizeRectangle);
        base.setPosition(positionX, positionY);

        roof = new Triangle();
        roof.changeColor("brown");
        roof.changeSize(sizeTriangle / 2, sizeTriangle);
        roof.setPosition(positionX + 10, positionY - sizeTriangle / 2);
        if (visible) {
            makeVisible();
        }
        this.isVisible = visible;
    }

    /**
     * Cambia el color de la base de la tienda.
     * ✅ Se hereda de StoreAbstracto, pero Store personaliza para actualizar base
     * 
     * @param newColor nuevo color (string)
     */
    @Override
    public void changeColor(String newColor) {
        this.color = newColor;
        base.changeColor(newColor);
    }

    /**
     * Hace visible la tienda (si no lo estaba).
     * ✅ Esta es la implementación específica de Store
     */
    @Override
    public void makeVisible() {
        if (!isVisible) {
            base.makeVisible();
            roof.makeVisible();
            isVisible = true;
        }
    }

    /**
     * Hace invisible la tienda (si estaba visible).
     * ✅ Esta es la implementación específica de Store
     */
    @Override
    public void makeInvisible() {
        if (isVisible) {
            base.makeInvisible();
            roof.makeInvisible();
            isVisible = false;
        }
    }

    /**
     * POLIMORFISMO: Store normal siempre acepta todos los robots.
     * Este override documenta el comportamiento estándar.
     * 
     * ✅ MEDIATOR: Store solo recibe tenges, no conoce al Robot
     * 
     * @param robotTenges tenges actuales del robot
     * @param storeTenges tenges de la tienda
     * @return true (Store normal siempre acepta)
     */
    @Override
    protected boolean puedeRecibirRobot(int robotTenges, int storeTenges) {
        return true;  // Store normal acepta a todos los robots
    }

    /**
     * POLIMORFISMO: Store normal transfiere exactamente lo recolectado.
     * Este override documenta el comportamiento estándar.
     * 
     * @param tengesRecolectados cantidad de tenges recolectados
     * @return cantidad de tenges a transferir (100%)
     */
    @Override
    protected int procesarTransferencia(int tengesRecolectados) {
        return tengesRecolectados;  // Store normal transfiere todo
    }
}