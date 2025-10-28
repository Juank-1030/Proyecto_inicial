package silkroad;

import shapes.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Representa un camino espiral de celdas en un canvas.
 * Permite posicionar y mover visualmente un robot y una tienda a lo largo del
 * camino.
 * Mantiene una lista de celdas y una matriz de posiciones (x,y) por índice
 * lógico.
 * Proporciona métodos para dibujar la espiral, asignar objetos a celdas,
 * mover el robot con animación o reposicionarlo instantáneamente.
 * También permite mostrar u ocultar todos los elementos visuales.
 *
 * @version 1.0
 * @author OpenAI's ChatGPT
 */
public class Road {
    private ArrayList<Cell> cells;
    private int length;
    private int[][] positions;
    private RobotAbstracto robot;
    private StoreAbstracto store;
    private boolean visible;

    /**
     * Constructor que inicializa la espiral con la longitud dada.
     * 
     * @param length número de celdas en la espiral
     */
    public Road(int length) {
        this.cells = new ArrayList<>();
        this.length = length + 1;
        this.positions = new int[this.length][2];
        this.visible = true;
        drawSpiral();
    }

    /**
     * Dibuja una espiral de celdas en el canvas.
     * Calcula posiciones (x,y) para cada celda y las almacena en positions.
     * La espiral comienza en la esquina superior izquierda y avanza en sentido
     * horario.
     */
    public void drawSpiral() {
        Canvas canvas = Canvas.getCanvas();
        int canvasWidth = canvas.getSizeCanvasW();
        int canvasHeight = canvas.getSizeCanvasH();

        int cellSize = 15;
        int gap = 31;

        int step = cellSize + gap;
        int x0 = gap, y0 = gap;

        int maxCols = (canvasWidth - 2 * gap) / step;
        int maxRows = (canvasHeight - gap) / step;

        int left = 1, top = 0;
        int right = maxCols - 1, bottom = maxRows - 1;
        int i = 0;

        while (i < length && left <= right && top <= bottom) {
            // Franja superior (izquierda -> derecha)
            for (int col = left - 1; col <= right && i < length; col++) {
                int x = x0 + col * step;
                int y = y0 + top * step;
                addCell(x, y);
                cellPosition(i, x, y);
                i++;
            }
            top += 2;

            // Columna derecha (arriba -> abajo)
            for (int row = top - 1; row <= bottom && i < length; row++) {
                int x = x0 + right * step;
                int y = y0 + row * step;
                addCell(x, y);
                cellPosition(i, x, y);
                i++;
            }
            right -= 2;

            // Franja inferior (derecha -> izquierda)
            for (int col = right + 1; col >= left && i < length; col--) {
                int x = x0 + col * step;
                int y = y0 + bottom * step;
                addCell(x, y);
                cellPosition(i, x, y);
                i++;
            }
            bottom -= 2;

            // Columna izquierda (abajo -> arriba)
            for (int row = bottom + 1; row >= top && i < length; row--) {
                int x = x0 + left * step;
                int y = y0 + row * step;
                addCell(x, y);
                cellPosition(i, x, y);
                i++;
            }
            left += 2;
        }
    }

    /**
     * Asigna un objeto (store o robot) a una celda específica.
     * Reposiciona el objeto en la celda indicada, manteniendo su offset relativo.
     * 
     * @param location   índice de celda (0 a length-1)
     * @param objectType "store" o "robot"
     */
    public void assignObjectPosition(int location, String objectType) {
        int cellX = positions[location][0];
        int cellY = positions[location][1];

        JOptionPane.showMessageDialog(
                null,
                "Asignando objeto: " + objectType +
                        " a la ubicación " + location +
                        " → (x: " + cellX + ", y: " + cellY + ")",
                "Asignación de objeto",
                JOptionPane.INFORMATION_MESSAGE);

        if (objectType.equals("store")) {

            if (store == null)
                store = new Store(visible);

            int offsetX = store.getX() - 30;
            int offsetY = store.getY() - 30;
            int storeX = cellX + offsetX;
            int storeY = cellY + offsetY;

            store.moveTo(storeX, storeY);

            if (visible)
                store.makeVisible();
            return;
        } else if (objectType.equals("robot")) {
            // Crear robot si no existe (inicialmente invisible)
            if (robot == null)
                robot = new Robot(false);

            robot.makeInvisible();

            int offsetX = robot.getX() - 30;
            int offsetY = robot.getY() - 30;
            int robotX = cellX + offsetX;
            int robotY = cellY + offsetY;

            robot.placeTo(robotX, robotY);

            robot.makeVisible();
        }
    }

    /**
     * Mueve visualmente el robot a lo largo de la espiral, celda a celda.
     * Mantiene el mismo offset relativo dentro de cada celda durante todo el
     * recorrido.
     * 
     * @param oldLocation índice actual del robot
     * @param meters      desplazamiento (positivo o negativo)
     * @param robot       instancia del robot a mover
     */
    public void moveRobotVisual(int oldLocation, int meters, RobotAbstracto robot) {
        if (robot == null)
            return;

        // Índice destino calculado como desplazamiento
        int newLocation = oldLocation + meters;
        if (newLocation < 0 || newLocation >= positions.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Movimiento inválido: fuera de límites.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (meters == 0)
            return;

        // Offset REAL del robot dentro de su casilla actual
        // (mantiene su posición relativa dentro de cada celda durante todo el
        // recorrido)
        int currentCellX = positions[oldLocation][0];
        int currentCellY = positions[oldLocation][1];
        int offsetX = robot.getX() - currentCellX;
        int offsetY = robot.getY() - currentCellY;

        // Dirección de avance por índice de celda (sigue exactamente la espiral de
        // drawSpiral)
        int step = (meters > 0) ? 1 : -1;

        // Recorre casilla a casilla para que el movimiento sea visible y alineado
        for (int i = oldLocation + step; (step > 0 ? i <= newLocation : i >= newLocation); i += step) {
            int cellX = positions[i][0];
            int cellY = positions[i][1];

            // Destino absoluto = posición de la celda + offset (constante durante TODO el
            // movimiento)
            int targetX = cellX + offsetX;
            int targetY = cellY + offsetY;

            // Animación desde donde esté actualmente hasta la casilla i
            robot.moveTo(targetX, targetY);
        }
    }

    /**
     * Reposiciona instantáneamente el robot a una celda específica.
     * Mantiene el mismo offset relativo dentro de la celda.
     * No hay animación, el robot "salta" a la nueva posición.
     * 
     * @param oldLocation índice actual del robot
     * @param meters      desplazamiento (positivo o negativo)
     * @param robot       instancia del robot a reposicionar
     */
    public void reposition(int oldLocation, int meters, RobotAbstracto robot) {
        if (robot == null)
            return;

        int newLocation = oldLocation + meters;
        if (newLocation < 0 || newLocation >= positions.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Reposición inválida: fuera de límites.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Offset RELATIVO a la celda de la que parte (oldLocation)
        int offsetX = robot.getX() - positions[oldLocation][0];
        int offsetY = robot.getY() - positions[oldLocation][1];

        // Destino = base de la celda nueva + mismo offset que traía
        int targetX = positions[newLocation][0] + offsetX;
        int targetY = positions[newLocation][1] + offsetY;

        robot.placeTo(targetX, targetY); // sin animación

        JOptionPane.showMessageDialog(
                null,
                "Robot reposicionado de " + oldLocation +
                        " a " + newLocation +
                        " → (" + (positions[newLocation][0]) + ", " + (positions[newLocation][1]) + ")",
                "Reposición completada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea una celda en la posición (px,py) y la añade a la lista cells.
     * 
     * @param px coordenada X
     * @param py coordenada Y
     */
    private void addCell(int px, int py) {
        Cell cell = new Cell();
        cell.setPosition(px, py);
        cell.makeVisible();
        cells.add(cell);
    }

    /**
     * Almacena las coordenadas (x,y) de una celda en la matriz positions.
     * 
     * @param index índice de celda
     * @param x     coordenada X
     * @param y     coordenada Y
     */
    private void cellPosition(int index, int x, int y) {
        if (index >= 0 && index < positions.length) {
            positions[index][0] = x;
            positions[index][1] = y;
        }
    }

    /**
     * @return lista de celdas que forman la espiral.
     */
    public ArrayList<Cell> getCells() {
        return cells;
    }

    /**
     * @return matriz de posiciones (x,y) por índice lógico de celda.
     */
    public int[][] getPositions() {
        return positions;
    }

    /**
     * Devuelve información descriptiva de la celda en la posición indicada.
     * Incluye el índice de la celda y sus coordenadas (X, Y).
     * Si el índice está fuera de rango, retorna un mensaje de error.
     *
     * @param index índice de la celda a consultar
     * @return String con la información de la celda o mensaje de error si el índice
     *         es inválido
     */
    public String getCellInfo(int index) {
        if (index >= 0 && index < length) {
            int x = positions[index][0];
            int y = positions[index][1];
            return "Casilla " + index + ": Coordenadas (X, Y) = (" + x + ", " + y + ")";
        }
        return "Índice fuera de rango";
    }

    /**
     * Devuelve la coordenada X de la celda en la posición indicada.
     * 
     * @param index índice de la celda (0 a length-1)
     * @return coordenada X de la celda, o -1 si el índice es inválido
     */
    public int getCellX(int index) {
        if (index >= 0 && index < length) {
            return positions[index][0];
        }
        return -1;
    }

    /**
     * Devuelve la coordenada Y de la celda en la posición indicada.
     * 
     * @param index índice de la celda (0 a length-1)
     * @return coordenada Y de la celda, o -1 si el índice es inválido
     */
    public int getCellY(int index) {
        if (index >= 0 && index < length) {
            return positions[index][1];
        }
        return -1;
    }

    /**
     * Hace visibles todas las celdas, la tienda y el robot (si existen).
     * Cambia el estado interno a visible y actualiza la visibilidad de los
     * elementos gráficos asociados.
     */
    public void makeVisible() {
        visible = true;
        if (cells != null) {
            for (Cell c : cells) {
                if (c != null) {
                    c.makeVisible();
                }
            }
        }
        if (store != null) {
            store.makeVisible();
        }
        if (robot != null) {
            robot.makeVisible();
        }
    }

    /**
     * Hace invisibles todas las celdas, la tienda y el robot (si existen).
     * Cambia el estado interno a invisible y actualiza la visibilidad de los
     * elementos gráficos asociados.
     */
    public void makeInvisible() {
        visible = false;
        if (robot != null) {
            robot.makeInvisible();
        }
        if (store != null) {
            store.makeInvisible();
        }
        if (cells != null) {
            for (Cell c : cells) {
                if (c != null) {
                    c.makeInvisible();
                }
            }
        }
    }

    /**
     * Asocia una instancia de Store global para reubicarla en celdas.
     * 
     * @param store instancia a asignar
     */
    public void assignStore(StoreAbstracto store) {
        this.store = store;
    }

    /**
     * Asocia una instancia de Robot global para reubicarla en celdas.
     * 
     * @param robot instancia a asignar
     */
    public void assignRobot(RobotAbstracto robot) {
        this.robot = robot;
    }


    /**
     * PATRÓN MEDIATOR: Road actúa como intermediario entre Robot y Store.
     * Orquesta la transferencia de tenges sin que Robot y Store se conozcan.
     * 
     * Flujo:
     * 1. Robot decide cuántos tenges puede recolectar
     * 2. Store valida la transferencia
     * 3. Road retorna la cantidad final transferida
     * 
     * @param robot el robot que visita la tienda
     * @param store la tienda siendo visitada
     * @param tengesDisponibles cantidad total de tenges en la tienda
     * @return cantidad real de tenges transferidos
     */
    public int transferirTenges(RobotAbstracto robot, StoreAbstracto store, int tengesDisponibles) {
        // PASO 1: Robot decide cuánto toma (sin conocer la tienda)
        int recolectados = robot.recolectarTenges(tengesDisponibles);
        
        // PASO 2: Store valida y confirma la transferencia (sin conocer al robot)
        int transferidos = store.validarTransferencia(recolectados);
        
        // PASO 3: Road retorna el resultado
        return transferidos;
    }
}