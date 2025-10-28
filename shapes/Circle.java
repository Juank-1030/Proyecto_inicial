package shapes;

import java.awt.geom.*;

/**
 * Circle representa un círculo dibujable sobre el {@link Canvas}.
 * Permite movimiento, cambio de tamaño, color y cálculo de área aproximada.
 */
public class Circle extends Shape {

    public static final double PI = 3.1416;

    private int diameter;

    /**
     * Crea un círculo por defecto (diámetro 30, color azul, invisible).
     */
    public Circle() {
        diameter = 30;
        xPosition = 55;
        yPosition = 55;
        color = "blue";
        isVisible = false;
    }

    /**
     * Hace visible el círculo (se dibuja).
     */
    @Override
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Hace invisible el círculo (lo borra).
     */
    @Override
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Dibuja si isVisible es true.
     */
    @Override
    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                    new Ellipse2D.Double(xPosition, yPosition, diameter, diameter));
            canvas.wait(10);
        }
    }

    /**
     * Borra si isVisible es true.
     */
    @Override
    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
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
     * Mueve horizontalmente.
     * 
     * @param distance px (pos=der / neg=izq)
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Mueve verticalmente.
     * 
     * @param distance px (pos=abajo / neg=arriba)
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
     * Cambia el diámetro.
     * 
     * @param newDiameter nuevo diámetro (>=0)
     */
    public void changeSize(int newDiameter) {
        erase();
        diameter = newDiameter;
        draw();
    }

    /**
     * Cambia el color (si visible, redibuja).
     * 
     * @param newColor nombre de color
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * @return área aproximada del círculo (double)
     */
    public double area() {
        double diameter2 = diameter;
        double radio = diameter2 / 2.0;
        return PI * radio * radio;
    }

    /**
     * Aumenta el área en un porcentaje y recalcula diámetro.
     * 
     * @param percentage porcentaje (0..100 o más)
     */
    public void bigger(int percentage) {
        double areaActual = area();
        double areaNueva = areaActual * (1 + percentage / 100.0);
        double nuevoRadio = Math.sqrt(areaNueva / PI);
        double nuevoDiametro = 2 * nuevoRadio;
        changeSize((int) Math.round(nuevoDiametro));
    }

    /**
     * Construye un círculo cuyo área aproximada busca ser la dada.
     * 
     * @param areaDeseada área objetivo (px^2)
     */
    public Circle(double areaDeseada) {
        double radio = Math.sqrt(areaDeseada / PI);
        double diamDouble = 2.0 * radio;
        this.diameter = (int) Math.round(diamDouble);
        this.xPosition = 20;
        this.yPosition = 15;
        this.color = "blue";
        this.isVisible = false;
    }

    /**
     * Construye un círculo con diámetro específico.
     * 
     * @param diametroDeseado diámetro deseado
     */
    public Circle(int diametroDeseado) {
        this.diameter = diametroDeseado;
        this.xPosition = 20;
        this.color = "blue";
        this.isVisible = false;
    }

    /**
     * @return diámetro actual
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * @return coordenada X
     */
    public int getX() {
        return xPosition;
    }

    /**
     * @return coordenada Y
     */
    public int getY() {
        return yPosition;
    }

    /**
     * @return color actual
     */
    public String getColor() {
        return color;
    }

    /**
     * @return true si se ha marcado como visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Establece un nuevo diámetro sin dibujo inmediato.
     * 
     * @param diameter nuevo diámetro
     */
    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    /**
     * Reposiciona y redibuja si visible.
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

    /**
     * Establece el color sin redibujar si invisible.
     * 
     * @param color nuevo color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Ajusta la bandera de visibilidad sin forzar dibujo.
     * 
     * @param isVisible nuevo estado
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}