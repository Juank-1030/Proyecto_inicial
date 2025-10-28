package silkroad;

import shapes.*;
import java.util.Random;

/**
 * CasinoStore: Una tienda con riesgo/recompensa basada en azar.
 * 
 * Características:
 * - Acepta a todos los robots (como Store normal)
 * - Transferencia ALEATORIA: Entre 50% y 150% de lo recolectado
 * - Si el robot tiene suerte: ¡Obtiene más dinero!
 * - Si el robot es desafortunado: Obtiene menos dinero
 * 
 * Ejemplo:
 * - Robot recolecta 100 tenges
 * - CasinoStore aplica random (50%-150%)
 * - Resultado: Entre 50 y 150 tenges (¡impredecible!)
 *
 * @author Sistema de Tiendas Especializadas - Ciclo 3
 * @version 1.0
 */
public class CasinoStore extends StoreAbstracto {

    private Random random; // Para generación de números aleatorios

    /**
     * Construye una tienda casino con dimensiones estándar y color especial.
     * 
     * @param visible true para hacerla visible tras construir
     */
    public CasinoStore(boolean visible) {
        super(visible);
        this.positionX = 54;
        this.positionY = 45;
        this.sizeRectangle = 20;
        this.sizeTriangle = 20;
        this.color = "magenta"; // Color especial para distinguirla (magenta = casino/suerte)
        this.random = new Random();

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
     * ✅ Esta es la implementación específica de CasinoStore
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
     * ✅ Esta es la implementación específica de CasinoStore
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
     * ✅ SINGLE RESPONSIBILITY: CasinoStore acepta a todos los robots.
     * No hay restricciones especiales (no es como FighterStore).
     * 
     * Override de StoreAbstracto.puedeRecibirRobot()
     * 
     * @param robotTenges tenges actuales del robot (ignorados)
     * @param storeTenges tenges de la tienda (ignorados)
     * @return siempre true (CasinoStore acepta todos)
     */
    @Override
    protected boolean puedeRecibirRobot(int robotTenges, int storeTenges) {
        // CasinoStore siempre acepta robots - el riesgo está en la transferencia
        return true;
    }

    /**
     * ✅ OPEN/CLOSED: CasinoStore implementa lógica aleatoria de transferencia.
     * 
     * El resultado depende de la suerte:
     * - 50% de posibilidad: recibe entre 50%-75% (MALA SUERTE)
     * - 50% de posibilidad: recibe entre 100%-150% (BUENA SUERTE)
     * 
     * Esto crea:
     * - Riesgo: Podrías perder dinero (50%-75%)
     * - Recompensa: Podrías ganar mucho (100%-150%)
     * - Estrategia: ¿Vale la pena entrar al casino?
     * 
     * Override de StoreAbstracto.procesarTransferencia()
     * 
     * @param tengesRecolectados cantidad de tenges que el robot recolectó
     * @return cantidad aleatoria basada en suerte (50%-150% del solicitado)
     */
    @Override
    protected int procesarTransferencia(int tengesRecolectados) {
        // Generar número aleatorio: 0-99
        int suerte = random.nextInt(100);
        
        int resultado;
        if (suerte < 50) {
            // MALA SUERTE (50% de probabilidad): Recibe entre 50%-75%
            // random.nextInt(26) genera 0-25, así que: 50% + (0-25%) = 50%-75%
            int porcentaje = 50 + random.nextInt(26);
            resultado = (tengesRecolectados * porcentaje) / 100;
        } else {
            // BUENA SUERTE (50% de probabilidad): Recibe entre 100%-150%
            // random.nextInt(51) genera 0-50, así que: 100% + (0-50%) = 100%-150%
            int porcentaje = 100 + random.nextInt(51);
            resultado = (tengesRecolectados * porcentaje) / 100;
        }
        
        return resultado;
    }
}
