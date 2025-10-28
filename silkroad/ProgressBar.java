package silkroad;

import shapes.*;

/**
 * ProgressBar representa una barra vertical de progreso que muestra
 * la proporción currentProfit / maxProfit. Crece de abajo hacia arriba.
 */
public class ProgressBar {
    private static final int DEFAULT_X = 20;
    private static final int DEFAULT_Y = 160;
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 500;

    private Rectangle bgBar; // Fondo
    private Rectangle bar; // Barra activa
    private int x, y;
    private int width, height;
    private int currentProfit;
    private int maxProfit;
    private boolean isVisible;

    /**
     * Crea la barra con valores por defecto. Si visible es true, se muestra.
     * 
     * @param visible true para mostrar inmediatamente
     */
    public ProgressBar(boolean visible) {
        this.x = DEFAULT_X;
        this.y = DEFAULT_Y;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.currentProfit = 0;
        this.maxProfit = 1; // Evita división por cero
        this.isVisible = visible;

        bgBar = new Rectangle();
        bgBar.changeColor("lightgray");
        bgBar.changeSize(height, width);
        bgBar.setPosition(x, y);

        bar = new Rectangle();
        bar.changeColor("green");
        bar.changeSize(0, width);
        bar.setPosition(x, y + height);

        if (isVisible) {
            bgBar.makeVisible();
            bar.makeVisible();
        }
    }

    /**
     * Ajusta el progreso (redimensionando la barra verde).
     * 
     * @param currentProfit ganancia actual (>=0)
     * @param maxProfit     ganancia máxima posible (>0, se fuerza a 1 si llega 0)
     */
    public void setProgress(int currentProfit, int maxProfit) {
        this.currentProfit = Math.max(0, currentProfit);
        this.maxProfit = Math.max(1, maxProfit);
        double frac = (double) this.currentProfit / this.maxProfit;
        frac = Math.max(0.0, Math.min(1.0, frac));
        int progHeight = (int) (height * frac);
        bar.changeSize(progHeight, width);
        bar.setPosition(x, y + height - progHeight);
        if (isVisible)
            bar.makeVisible();
        else
            bar.makeInvisible();
    }

    /**
     * Hace visible la barra (fondo + barra interna).
     */
    public void makeVisible() {
        if (!isVisible) {
            bgBar.makeVisible();
            bar.makeVisible();
            isVisible = true;
        }
    }

    /**
     * Hace invisible la barra (fondo + barra interna).
     */
    public void makeInvisible() {
        if (isVisible) {
            bgBar.makeInvisible();
            bar.makeInvisible();
            isVisible = false;
        }
    }
}