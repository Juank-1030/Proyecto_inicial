package silkroad;

import shapes.*;

/**
 * FighterStore: Una tienda defensiva que protege sus tenges.
 * 
 * Los robots solo pueden tomar tenges de una FighterStore si tienen
 * MÁS dinero que la tienda. Si el robot tiene MENOS dinero, pierde
 * 10 tenges al intentar tomar de esta tienda.
 *
 * Características:
 * - Hereda toda la lógica de StoreAbstracto
 * - Comportamiento especial: Protección contra robots más pobres
 * - SilkRoad es responsable de validar la lógica de protección
 *
 * @author Sistema de Herencia - Ciclo 3
 * @version 1.0
 */
public class FighterStore extends StoreAbstracto {

    /**
     * Construye una tienda defensiva con dimensiones estándar y color aleatorio.
     * 
     * @param visible true para hacerla visible tras construir
     */
    public FighterStore(boolean visible) {
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
     * Se hereda de StoreAbstracto, pero personaliza para actualizar base
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
     * Esta es la implementación específica de FighterStore
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
     * Esta es la implementación específica de FighterStore
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
     * SINGLE RESPONSIBILITY: FighterStore decide si acepta robots.
     * FighterStore rechaza robots débiles (tenges <= storeTenges).
     * 
     * Override de StoreAbstracto.puedeRecibirRobot()
     * 
     * @param robotTenges tenges actuales del robot
     * @param storeTenges tenges de la tienda
     * @return true si robot es fuerte (tiene más dinero), false si es débil
     */
    @Override
    protected boolean puedeRecibirRobot(int robotTenges, int storeTenges) {
        // FighterStore solo acepta robots que tengan MÁS tenges que ella
        // Si robot es débil (tenges <= storeTenges), es rechazado
        return robotTenges > storeTenges;
    }

    /**
     * OPEN/CLOSED: FighterStore maneja su propia lógica de rechazo.
     * Si fue rechazado, retorna 0 (no transfiere dinero).
     * Si fue aceptado, transfiere normalmente.
     * 
     * IMPORTANTE: Este método se llama SOLO si puedeRecibirRobot() retorna true.
     * No es necesario verificar aquí; se asume que el robot fue validado.
     * 
     * @param tengesRecolectados cantidad de tenges que el robot recolectó
     * @return cantidad de tenges a transferir (100% si fue aceptado)
     */
    @Override
    protected int procesarTransferencia(int tengesRecolectados) {
        // Si llegamos aquí, puedeRecibirRobot() ya retornó true
        // FighterStore transfiere normalmente
        return tengesRecolectados;
    }

    /**
     * ENCAPSULACIÓN: FighterStore genera su propio mensaje de rechazo.
     * Solo se muestra si puedeRecibirRobot() retorna false (robot rechazado).
     * 
     * Mensaje personalizado para FighterStore:
     * - Comunica por qué fue rechazado (robot débil)
     * - Explica la condición (tenges del robot vs tenges de la tienda)
     * - Sugiere acción (acumular más dinero)
     * 
     * @param robotTenges tenges actuales del robot (usado en el mensaje)
     * @param storeTenges tenges de la tienda (usado en el mensaje)
     * @return mensaje descriptivo del rechazo por debilidad
     */
    @Override
    public String obtenerMensajeRechazo(int robotTenges, int storeTenges) {
        return "¡Robot rechazado! La tienda tiene " + storeTenges + " tenges.\n" +
               "Tu robot solo tiene " + robotTenges + " tenges.\n" +
               "No tienes suficiente poder para acceder a esta tienda.";
    }
}
