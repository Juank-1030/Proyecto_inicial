package shapes;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas es un singleton que gestiona el renderizado de formas simples.
 * Mantiene una lista de objetos (referencias) con su descripción (forma +
 * color),
 * redibujando el lienzo completo en cada actualización.
 *
 * Uso típico desde figuras como Rectangle, Circle, etc.
 */
public class Canvas {
    private static Canvas canvasSingleton;

    /**
     * Obtiene (o crea) la instancia única de Canvas y garantiza su visibilidad.
     * 
     * @return instancia singleton de Canvas
     */
    public static Canvas getCanvas() {
        if (canvasSingleton == null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;

            int screenW = screenWidth;
            int screenH = screenHeight;

            canvasSingleton = new Canvas("BlueJ Shapes Demo", screenW, screenH, Color.white);
        }

        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    // ----- Atributos de instancia -----
    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List<Object> objects;
    private HashMap<Object, ShapeDescription> shapes;

    /**
     * Constructor privado (patrón singleton).
     * 
     * @param title    título de la ventana
     * @param width    ancho deseado
     * @param height   alto deseado
     * @param bgColour color de fondo
     */
    private Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList<Object>();
        shapes = new HashMap<Object, ShapeDescription>();
    }

    /**
     * Establece visibilidad de la ventana y/o inicializa el buffer gráfico la
     * primera vez.
     * 
     * @param visible true para mostrar
     */
    public void setVisible(boolean visible) {
        if (graphic == null) {
            // Inicialización perezosa del buffer
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }

    /**
     * Dibuja una forma asociada a un objeto de referencia (para mantener
     * identidad).
     * 
     * @param referenceObject objeto que actúa como clave
     * @param color           color textual
     * @param shape           la forma a dibujar
     */
    public void draw(Object referenceObject, String color, java.awt.Shape shape) {
        objects.remove(referenceObject);
        objects.add(referenceObject);
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }

    /**
     * Borra (olvida) una forma previamente registrada.
     * 
     * @param referenceObject referencia usada al dibujar
     */
    public void erase(Object referenceObject) {
        objects.remove(referenceObject);
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Configura el color de dibujo según una cadena.
     * 
     * @param colorString nombre de color
     */
    public void setForegroundColor(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":
                graphic.setColor(Color.red);
                break;
            case "black":
                graphic.setColor(Color.black);
                break;
            case "blue":
                graphic.setColor(Color.blue);
                break;
            case "yellow":
                graphic.setColor(Color.yellow);
                break;
            case "green":
                graphic.setColor(Color.green);
                break;
            case "magenta":
                graphic.setColor(Color.magenta);
                break;
            case "white":
                graphic.setColor(Color.white);
                break;
            case "purple":
                graphic.setColor(new Color(128, 0, 128));
                break;
            case "orange":
                graphic.setColor(Color.orange);
                break;
            case "pink":
                graphic.setColor(Color.pink);
                break;
            case "cyan":
                graphic.setColor(Color.cyan);
                break;
            case "brown":
                graphic.setColor(new Color(139, 69, 19));
                break;
            case "lightgray":
                graphic.setColor(Color.lightGray);
                break;
            case "indigo":
                graphic.setColor(new Color(75, 0, 130));
                break;
            case "gold":
                graphic.setColor(new Color(255, 215, 0));
                break;
            case "salmon":
                graphic.setColor(new Color(250, 128, 114));
                break;
            case "beige":
                graphic.setColor(new Color(245, 245, 220));
                break;
            case "ivory":
                graphic.setColor(new Color(255, 255, 240));
                break;
            case "sunsetorange":
                graphic.setColor(new Color(255, 97, 56));
                break;
            case "desertsand":
                graphic.setColor(new Color(237, 201, 175));
                break;
            case "reddirt":
                graphic.setColor(new Color(233, 91, 33));
                break;
            case "sage":
                graphic.setColor(new Color(188, 184, 137));
                break;
            case "terracottared":
                graphic.setColor(new Color(226, 114, 91));
                break;
            case "amber":
                graphic.setColor(new Color(255, 191, 0));
                break;
            default:
                graphic.setColor(Color.black);
        }
    }

    /**
     * Pausa de utilidad (animaciones lentas).
     * 
     * @param milliseconds milisegundos a esperar
     */
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            // ignorado
        }
    }

    /**
     * Redibuja todos los objetos registrados en el canvas.
     */
    private void redraw() {
        erase();
        for (Iterator i = objects.iterator(); i.hasNext();) {
            shapes.get(i.next()).draw(graphic);
        }
        canvas.repaint();
    }

    /**
     * Borra (repinta fondo) sin redibujar.
     */
    private void erase() {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /**
     * @return ancho actual del panel
     */
    public int getSizeCanvasW() {
        return canvas.getWidth();
    }

    /**
     * @return alto actual del panel
     */
    public int getSizeCanvasH() {
        return canvas.getHeight();
    }

    /**
     * Panel interno sobre el que se pinta el buffer.
     */
    private class CanvasPane extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    /**
     * Descripción interna (forma y color) asociada a un objeto de referencia.
     */
    private class ShapeDescription {
        private java.awt.Shape shape;
        private String colorString;

        /**
         * @param shape forma a dibujar
         * @param color color textual
         */
        public ShapeDescription(java.awt.Shape shape, String color) {
            this.shape = shape;
            colorString = color;
        }

        /**
         * Dibuja la forma usando el color registrado.
         * 
         * @param graphic contexto gráfico (Graphics2D)
         */
        public void draw(Graphics2D graphic) {
            setForegroundColor(colorString);
            graphic.draw(shape);
            graphic.fill(shape);
        }
    }
}