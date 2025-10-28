package shapes;

import java.awt.*;

/**
 * Triangle es una figura isósceles simple con base horizontal y vértice
 * superior.
 * Permite mover, cambiar tamaño y color, y alternar visibilidad.
 */
public class Triangle extends Shape {

    public static int VERTICES = 3;

    private int height;
    private int width;

    /**
     * Crea un triángulo con dimensiones y posición por defecto.
     */
    public Triangle() {
        height = 30;
        width = 40;
        xPosition = 140;
        yPosition = 15;
        color = "green";
        isVisible = false;
    }

    /**
     * Hace visible el triángulo (si no lo estaba).
     */
    @Override
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Hace invisible el triángulo (si estaba visible).
     */
    @Override
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    public void moveRight() {
        moveHorizontal(20);
    }

    public void moveLeft() {
        moveHorizontal(-20);
    }

    public void moveUp() {
        moveVertical(-20);
    }

    public void moveDown() {
        moveVertical(20);
    }

    /**
     * Mueve en horizontal.
     * 
     * @param distance px (positivo derecha / negativo izquierda)
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Mueve en vertical.
     * 
     * @param distance px (positivo abajo / negativo arriba)
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Movimiento lento horizontal.
     * 
     * @param distance distancia total px
     */
    public void slowMoveHorizontal(int distance) {
        int delta;
        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }
        for (int i = 0; i < distance; i++) {
            xPosition += delta;
            draw();
        }
    }

    /**
     * Movimiento lento vertical.
     * 
     * @param distance distancia total px
     */
    public void slowMoveVertical(int distance) {
        int delta;
        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }
        for (int i = 0; i < distance; i++) {
            yPosition += delta;
            draw();
        }
    }

    /**
     * Cambia dimensiones (alto y ancho).
     * 
     * @param newHeight nuevo alto (>=0)
     * @param newWidth  nuevo ancho (>=0)
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    /**
     * Cambia el color actual.
     * 
     * @param newColor nombre de color
     */
    @Override
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Dibuja el triángulo si está visible.
     */
    @Override
    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            int[] xpoints = { xPosition, xPosition + (width / 2), xPosition - (width / 2) };
            int[] ypoints = { yPosition, yPosition + height, yPosition + height };
            canvas.draw(this, color, new Polygon(xpoints, ypoints, 3));
            canvas.wait(10);
        }
    }

    /**
     * Borra del lienzo si está visible.
     */
    @Override
    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    /**
     * Reposiciona el triángulo.
     * 
     * @param x nueva X
     * @param y nueva Y
     */
    public void setPosition(int x, int y) {
        erase();
        this.xPosition = x;
        this.yPosition = y;
        draw();
    }
}