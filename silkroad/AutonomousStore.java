package silkroad;

import shapes.*;

/**
 * AutonomousStore: Una tienda autónoma que es diferenciable de una tienda
 * normal.
 * 
 * Hereda toda la lógica de movimiento, visibilidad y comportamiento de
 * StoreAbstracto.
 * SilkRoad es responsable de asignar una posición aleatoria a esta tienda
 * usando instanceof para identificarla.
 *
 * @author Sistema de Herencia - Ciclo 3
 * @version 1.0
 */
public class AutonomousStore extends StoreAbstracto {

    /**
     * Construye una tienda autónoma con dimensiones estándar y color aleatorio.
     * 
     * @param visible true para hacerla visible tras construir
     */
    public AutonomousStore(boolean visible) {
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
     * ✅ Se hereda de StoreAbstracto, pero personaliza para actualizar base
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
     * ✅ Esta es la implementación específica de AutonomousStore
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
     * ✅ Esta es la implementación específica de AutonomousStore
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
     * Indica que esta tienda autónoma necesita una posición aleatoria.
     * SilkRoad consultará este método para saber si debe buscar una ubicación libre.
     * 
     * @return true (siempre necesita posición aleatoria)
     */
    @Override
    protected boolean necesitaPosicionAleatoria() {
        return true;  // ✅ Siempre necesita posición aleatoria
    }
}
