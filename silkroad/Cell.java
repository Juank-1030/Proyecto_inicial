package silkroad;

import shapes.*;

/**
 * Cell representa una casilla visual del tablero. Contiene un fondo y un
 * rectángulo interior que simulan la cuadrícula. Puede marcar flags para
 * saber si contiene un robot o una tienda (lógica ligera).
 */
public class Cell {
    private String color;
    private boolean hasRobot;
    private boolean hasStore;
    private int positionX;
    private int positionY;
    private Rectangle background;
    private Rectangle cell;

    /**
     * Construye una celda en (0,0) con un fondo negro y una "arena" interior.
     * Marca inicialmente sin robot ni tienda.
     */
    public Cell() {
        this.color = color;
        this.hasRobot = false;
        this.hasStore = false;

        this.positionX = 0;
        this.positionY = 0;

        background = new Rectangle();
        background.changeColor("black");
        background.changeSize(50, 50);
        background.setPosition(positionX, positionY);
        background.makeVisible();

        cell = new Rectangle();
        cell.changeColor("desertsand");
        cell.changeSize(40, 40);
        cell.setPosition(5, 5);
        cell.makeVisible();
    }

    /**
     * Marca que esta celda tiene un robot.
     */
    public void placeRobot() {
        this.hasRobot = true;
    }

    /**
     * Quita la marca de robot.
     */
    public void removeRobot() {
        this.hasRobot = false;
    }

    /**
     * Marca que esta celda tiene una tienda.
     */
    public void placeStore() {
        this.hasStore = true;
    }

    /**
     * Quita la marca de tienda.
     */
    public void removeStore() {
        this.hasStore = false;
    }

    /**
     * @return true si la celda tiene un robot
     */
    public boolean hasRobot() {
        return hasRobot;
    }

    /**
     * @return true si la celda tiene una tienda
     */
    public boolean hasStore() {
        return hasStore;
    }

    /**
     * Mueve la celda (ambos rectángulos) en la dirección indicada.
     * 
     * @param direction "up", "down", "left", "right"
     */
    public void moveTo(String direction) {
        int step = 45;
        int dx = 0;
        int dy = 0;

        switch (direction.toLowerCase()) {
            case "up":
                dy = -step;
                break;
            case "down":
                dy = step;
                break;
            case "left":
                dx = -step;
                break;
            case "right":
                dx = step;
                break;
            default:
                System.out.println("Dirección no válida. Usa: up, down, left, right");
                return;
        }

        positionX += dx;
        positionY += dy;
        background.moveHorizontal(dx);
        background.moveVertical(dy);
        cell.moveHorizontal(dx);
        cell.moveVertical(dy);
    }

    /**
     * Hace visibles los elementos gráficos de la celda.
     */
    public void makeVisible() {
        background.makeVisible();
        cell.makeVisible();
    }

    /**
     * Hace invisibles los elementos gráficos de la celda.
     */
    public void makeInvisible() {
        background.makeInvisible();
        cell.makeInvisible();
    }

    /**
     * @return coordenada X actual
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * @return coordenada Y actual
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * @return color (campo lógico, no se usa para pintar directamente)
     */
    public String getColor() {
        return color;
    }

    /**
     * Reposiciona la celda aplicando desplazamiento a cada rectángulo.
     * 
     * @param x nueva coordenada X
     * @param y nueva coordenada Y
     */
    public void setPosition(int x, int y) {
        int dx = x - this.positionX;
        int dy = y - this.positionY;

        this.positionX = x;
        this.positionY = y;

        background.moveHorizontal(dx);
        background.moveVertical(dy);
        cell.moveHorizontal(dx);
        cell.moveVertical(dy);
    }
}