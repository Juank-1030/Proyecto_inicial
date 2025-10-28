package shapes;

/**
 * Shape es la clase base abstracta para todas las figuras geométricas.
 * Define los atributos y comportamientos comunes: posición, color y
 * visibilidad.
 */
public abstract class Shape {

    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    /**
     * Constructor por defecto que inicializa la figura como invisible.
     */
    public Shape() {
        this.isVisible = false;
    }

    /**
     * Constructor que permite especificar posición y color iniciales.
     * 
     * @param xPosition Posición horizontal inicial
     * @param yPosition Posición vertical inicial
     * @param color     Color inicial de la figura
     */
    public Shape(int xPosition, int yPosition, String color) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
        this.isVisible = false;
    }

    /**
     * Hace visible la figura (la dibuja en el canvas).
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Hace invisible la figura (la borra del canvas).
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Cambia el color de la figura.
     * 
     * @param newColor El nuevo color (ej: "red", "blue", "green")
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Mueve la figura horizontalmente.
     * 
     * @param distance Distancia a mover (positiva = derecha, negativa = izquierda)
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Mueve la figura verticalmente.
     * 
     * @param distance Distancia a mover (positiva = abajo, negativa = arriba)
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Mueve lentamente la figura horizontalmente.
     * 
     * @param distance Distancia a mover
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
     * Mueve lentamente la figura verticalmente.
     * 
     * @param distance Distancia a mover
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
     * Obtiene la posición X actual de la figura.
     * 
     * @return Coordenada X
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Obtiene la posición Y actual de la figura.
     * 
     * @return Coordenada Y
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Obtiene el color actual de la figura.
     * 
     * @return El color como String
     */
    public String getColor() {
        return color;
    }

    /**
     * Verifica si la figura está visible.
     * 
     * @return true si está visible, false si no
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Método abstracto que debe implementar cada figura específica
     * para dibujarse en el canvas.
     */
    protected abstract void draw();

    /**
     * Método abstracto que debe implementar cada figura específica
     * para borrarse del canvas.
     */
    protected abstract void erase();
}
