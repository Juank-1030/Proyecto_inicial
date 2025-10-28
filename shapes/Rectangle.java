package shapes;

/**
 * Rectangle: forma básica manipulable (mover, cambiar tamaño, color,
 * visibilidad).
 * Se dibuja sobre {@link Canvas} y re-dibuja tras cada operación.
 */
public class Rectangle extends Shape {

    public static int EDGES = 4;

    private int height;
    private int width;

    /**
     * Crea un rectángulo con valores por defecto (posición 0,0; color magenta).
     */
    public Rectangle() {
        height = 30;
        width = 40;
        xPosition = 0;
        yPosition = 0;
        color = "magenta";
        isVisible = false;
    }

    /**
     * Hace visible el rectángulo si no lo está.
     */
    @Override
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Hace invisible el rectángulo si está visible.
     */
    @Override
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Mueve a la derecha 20 px.
     */
    public void moveRight() {
        moveHorizontal(20);
    }

    /**
     * Mueve a la izquierda 20 px.
     */
    public void moveLeft() {
        moveHorizontal(-20);
    }

    /**
     * Mueve arriba 20 px.
     */
    public void moveUp() {
        moveVertical(-20);
    }

    /**
     * Mueve abajo 20 px.
     */
    public void moveDown() {
        moveVertical(20);
    }

    /**
     * Traslada horizontalmente.
     * 
     * @param distance píxeles (positivo derecha / negativo izquierda)
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Traslada verticalmente.
     * 
     * @param distance píxeles (positivo abajo / negativo arriba)
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Desplaza lentamente en horizontal (paso a paso).
     * 
     * @param distance distancia total (px)
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
     * Desplaza lentamente en vertical (paso a paso).
     * 
     * @param distance distancia total (px)
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
     * Cambia dimensiones del rectángulo.
     * 
     * @param newHeight alto en px (>=0)
     * @param newWidth  ancho en px (>=0)
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    /**
     * Cambia el color y redibuja (si visible).
     * 
     * @param newColor nombre de color
     */
    @Override
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Dibuja la figura si está visible.
     */
    @Override
    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                    new java.awt.Rectangle(xPosition, yPosition, width, height));
            canvas.wait(10);
        }
    }

    /**
     * Borra la figura si está visible.
     */
    @Override
    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    /**
     * @return posición X actual
     */
    public int getX() {
        return xPosition;
    }

    /**
     * @return posición Y actual
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
     * @param height nuevo alto
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param width nuevo ancho
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Reposiciona la figura (se redibuja).
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
     * @param color nuevo color (no dibuja si invisible)
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Ajusta directamente la bandera de visibilidad (no dibuja/borra).
     * 
     * @param isVisible valor booleano
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}