package silkroad;

import java.util.List;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Clase principal que gestiona la SilkRoad, incluyendo tiendas, robots,
 * movimientos, profit y visualización.
 *
 * @author Juan Carlos Bohorquez y Juan Diego Valderrama
 * @version 3.5
 */
public class SilkRoad {
    private final int length; // Longitud de la SilkRoad
    private final int[][] stores; // Matriz de tiendas {location, tenges}
    private int[][] robots; // Matriz de robots {location, tenges}
    private int[][] positions;
    private Road road;
    private int profit;
    private boolean lastOperationOK;
    private ProgressBar progressBar;
    private boolean visible;
    private int maxProfit;
    private RobotAbstracto[] robotRefs;
    private StoreAbstracto[] storeRefs;
    private int[] robotOrigins;
    private int[][] profitPerLocation;
    private int[] originalStoreTenges;
    private boolean useFastMovement = false;

    private int[][] daysSchedule = null; // cada evento: [1,x] robot, [2,x,c] tienda
    private int dayIndexSchedule = -1; // -1 = no se ha aplicado ningún día
    private boolean dayMode = false; // true => reboot actúa como "pasar día"

    /**
     * Constructor que inicializa la SilkRoad con la longitud dada.
     *
     * @param length longitud de la SilkRoad (número de celdas)
     */
    public SilkRoad(int length) {
        this.length = length;
        this.positions = new int[length + 1][2];
        this.stores = new int[length + 1][2];
        this.robots = new int[length + 1][2];
        this.robotRefs = new RobotAbstracto[this.positions.length];
        this.storeRefs = new StoreAbstracto[this.positions.length];
        this.robotOrigins = new int[length + 1];
        Arrays.fill(this.robotOrigins, -1);
        this.road = new Road(length);
        this.profitPerLocation = new int[this.positions.length + 1][2];
        for (int i = 0; i < this.profitPerLocation.length; i++) {
            this.profitPerLocation[i][0] = i; // location
            this.profitPerLocation[i][1] = 0; // acumulado
        }
        int[][] roadPos = road.getPositions();
        for (int i = 0; i < roadPos.length; i++) {
            this.positions[i][0] = roadPos[i][0];
            this.positions[i][1] = roadPos[i][1];
        }
        // En SilkRoad(int length)
        this.originalStoreTenges = new int[length + 1]; // Inicializar a 0 por defecto
        this.progressBar = new ProgressBar(true);
        this.profit = 0;
        this.maxProfit = 0;
        this.visible = true;
        this.lastOperationOK = true;
    }

    /**
     * Constructor que inicializa la SilkRoad con un schedule de eventos diarios.
     * Cada evento en el schedule es un array:
     * - [1, x] para colocar un robot en la posición x
     * - [2, x, c] para colocar una tienda en la posición x con c tenges
     *
     * El primer elemento del schedule (schedule[0]) debe ser [n, 0] donde n es
     * el número total de reboots (días) que se simularán.
     *
     * @param schedule matriz de eventos diarios
     */
    public SilkRoad(int[][] schedule) {
        int maxPos = 0;
        if (schedule != null) {
            for (int i = 1; i < schedule.length; i++) {
                if (schedule[i] != null && schedule[i].length > 1) {
                    maxPos = Math.max(maxPos, schedule[i][1]);
                }
            }
        }

        int length = Math.max(maxPos, 1);
        this.length = length;
        this.positions = new int[length + 1][2];
        this.stores = new int[length + 1][2];
        this.robots = new int[length + 1][2];
        this.robotRefs = new RobotAbstracto[this.positions.length];
        this.storeRefs = new StoreAbstracto[this.positions.length];
        this.robotOrigins = new int[length + 1];
        Arrays.fill(this.robotOrigins, -1);
        this.road = new Road(length);
        this.profitPerLocation = new int[this.positions.length + 1][2];
        for (int i = 0; i < this.profitPerLocation.length; i++) {
            this.profitPerLocation[i][0] = i;
            this.profitPerLocation[i][1] = 0;
        }
        int[][] roadPos = road.getPositions();
        for (int i = 0; i < roadPos.length; i++) {
            this.positions[i][0] = roadPos[i][0];
            this.positions[i][1] = roadPos[i][1];
        }
        this.progressBar = new ProgressBar(true);
        this.profit = 0;
        this.maxProfit = 0; // 🔹 Importante: reiniciar acumulado
        this.visible = true;
        this.lastOperationOK = true;

        this.daysSchedule = schedule;
        this.dayIndexSchedule = 0;
        this.dayMode = true;

        // En SilkRoad(int[][] schedule) - igual, después de calcular length
        this.originalStoreTenges = new int[length + 1]; // Inicializar a 0 por defecto
    }

    public int getLength() {
        return length;
    }

    public void profitPerMove() {
        boolean found = false;

        String message = "Profit por ubicación:\n";
        for (int i = 0; i < profitPerLocation.length; i++) {
            if (profitPerLocation[i][1] > 0) {
                message += "- Casilla " + profitPerLocation[i][0] +
                        " → " + profitPerLocation[i][1] + " tenges\n";
                found = true;
            }
        }

        if (found) {
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Profit por movimiento",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay ganancias registradas aún.",
                    "Sin datos de profit",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Coloca una tienda normal en la ubicación dada si está libre.
     *
     * @param location índice de la celda
     * @param tenges   cantidad inicial de tenges en la tienda
     */
    public void placeStore(int location, int tenges) {
        placeStore("normal", location, tenges);
    }

    /**
     * ✅ SOBRECARGADO: Coloca una tienda según tipo
     * 
     * Crea y posiciona tienda según el tipo especificado:
     * - "normal" → Store en posición indicada
     * - "autonomous" → AutonomousStore elige posición aleatoria
     * - "fighter" → FighterStore en posición indicada
     * 
     * @param type     tipo de tienda ("normal", "autonomous" o "fighter")
     * @param location ubicación para la tienda (ignorada si es autónoma)
     * @param tenges   cantidad inicial de tenges en la tienda
     */
    public void placeStore(String type, int location, int tenges) {
        // Crear tienda según tipo
        StoreAbstracto store = crearTienda(type);

        if (store == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tipo de tienda desconocido: " + type +
                            "\nTipos válidos: normal, autonomous, fighter",
                    "Error: Tipo inválido",
                    JOptionPane.ERROR_MESSAGE);
            lastOperationOK = false;
            return;
        }

        // Delegar al método privado que maneja la posición y validaciones
        addStoreToLocation(store, type, location, tenges);
    }

    /**
     * Método privado que posiciona la tienda con validaciones.
     * Determina automáticamente la ubicación para tiendas autónomas.
     * 
     * @param store    tienda ya creada
     * @param type     tipo de tienda para identificación
     * @param location ubicación indicada (puede ser ignorada)
     * @param tenges   cantidad de tenges
     */
    private void addStoreToLocation(StoreAbstracto store, String type, int location, int tenges) {
        // Para tiendas que necesitan posición aleatoria (polimórfica: cada tienda decide)
        if (store.necesitaPosicionAleatoria()) {
            location = encontrarPosicionRandom();
            if (location < 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "No hay posiciones disponibles para colocar una tienda autónoma.",
                        "Error: Sin espacio",
                        JOptionPane.ERROR_MESSAGE);
                lastOperationOK = false;
                return;
            }
        }

        // Validar ubicación
        if (location < 0 || location >= positions.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Índice de casilla inválido: " + location,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            lastOperationOK = false;
            return;
        }

        if (storeRefs[location] != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ya existe una tienda en la casilla " + location,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            lastOperationOK = false;
            return;
        }

        // Posicionar tienda
        storeRefs[location] = store;
        stores[location][0] = location;
        stores[location][1] = tenges;
        originalStoreTenges[location] = tenges;
        maxProfit += tenges;
        updateProgressBar();

        if (this.visible && road != null) {
            road.assignStore(store);
            road.assignObjectPosition(location, "store");
            store.makeVisible();
        }

        // Mensaje de confirmación
        JOptionPane.showMessageDialog(
                null,
                "Tienda " + type + " colocada en la casilla " + location + " con " + tenges + " tenges.",
                "Tienda creada",
                JOptionPane.INFORMATION_MESSAGE);

        transferTengesIfCoincide(0); // Robot recién colocado tiene 0 tenges
        lastOperationOK = true;
    }

    /**
     * Encuentra una posición aleatoria disponible en la carretera.
     * Utilizada exclusivamente por tiendas autónomas (AutonomousStore).
     * Verifica que la posición no esté ocupada por otra tienda.
     * 
     * @return índice de posición aleatoria disponible, o -1 si no hay disponibles
     */
    private int encontrarPosicionRandom() {
        java.util.List<Integer> available = new java.util.ArrayList<>();

        // Encontrar todas las posiciones libres (sin tienda)
        for (int i = 0; i < storeRefs.length; i++) {
            if (storeRefs[i] == null) {
                available.add(i);
            }
        }

        // Si no hay posiciones disponibles
        if (available.isEmpty()) {
            return -1;
        }

        // Escoger una aleatoriamente
        java.util.Random rand = new java.util.Random();
        return available.get(rand.nextInt(available.size()));
    }

    /**
     * Método privado que crea una tienda según su tipo.
     * ESTE ES EL ÚNICO LUGAR donde se especifica qué clase instanciar.
     * Para agregar un nuevo tipo: agregar un case aquí.
     * 
     * @param type tipo de tienda ("normal", "autonomous", "fighter", etc.)
     * @return instancia de StoreAbstracto del tipo pedido, o null si inválido
     */
    private StoreAbstracto crearTienda(String type) {
        type = type.toLowerCase();

        switch (type) {
            case "normal":
                return new Store(this.visible);
            case "autonomous":
                return new AutonomousStore(this.visible);
            case "fighter":
                return new FighterStore(this.visible);
            case "casino":
                return new CasinoStore(this.visible);
            // AGREGAR NUEVOS TIPOS DE TIENDAS AQUÍ (solo agregar case)
            default:
                return null;
        }
    }

    /**
     * Método privado que crea un robot según su tipo.
     * ESTE ES EL ÚNICO LUGAR donde se especifica qué clase instanciar.
     * Para agregar un nuevo tipo: agregar un case aquí.
     * 
     * @param type tipo de robot ("normal", "neverback", "tender", etc.)
     * @return instancia de RobotAbstracto del tipo pedido, o null si inválido
     */
    private RobotAbstracto crearRobot(String type) {
        type = type.toLowerCase();

        switch (type) {
            case "normal":
                return new Robot(this.visible);
            case "neverback":
                return new NeverbackRobot(this.visible);
            case "tender":
                return new TenderRobot(this.visible);
            // AGREGAR NUEVOS TIPOS DE ROBOTS AQUÍ (solo agregar case)
            default:
                return null;
        }
    }

    /**
     * Muestra un mensaje con la lista de tiendas que están vacías (tienen 0
     * tenges).
     * Cambia el color de las tiendas vacías a negro para resaltarlas visualmente.
     * Si no hay tiendas vacías, muestra un mensaje informativo.
     */
    public void emptiedStores() {
        boolean found = false;
        String message = "Tiendas vacías:\n";

        for (int i = 0; i < stores.length; i++) {
            if (storeRefs[i] != null && stores[i][1] == 0) {
                storeRefs[i].changeColor("black");
                message += "- Casilla " + i + "\n";
                found = true;
            }
        }

        if (found) {
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Tiendas vacías",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay tiendas vacías actualmente.",
                    "Sin tiendas vacías",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Restaura todas las tiendas a su valor original de tenges.
     * Para cada tienda activa, recupera el valor original registrado y lo repone si
     * es necesario.
     * Ajusta el profit máximo acumulado y actualiza la barra de progreso.
     * Muestra un mensaje informativo al finalizar.
     */
    public void resupplyStores() {
        for (int i = 0; i < stores.length; i++) {
            if (storeRefs[i] != null) {
                // Obtener el valor original registrado en profitPerLocation
                int originalTenges = profitPerLocation[i][1];

                // Si no hay registro válido, usar el valor actual como referencia base
                if (originalTenges <= 0) {
                    originalTenges = stores[i][1];
                }

                int diff = originalTenges - stores[i][1]; // diferencia a reponer

                if (diff > 0) {
                    stores[i][1] = originalTenges; // restaurar valor original
                    maxProfit += diff; // ajustar ganancia máxima acumulada
                }
            }
        }

        updateProgressBar();

        JOptionPane.showMessageDialog(
                null,
                "Todas las tiendas han sido reabastecidas a su valor original de tenges.",
                "Tiendas reabastecidas",
                JOptionPane.INFORMATION_MESSAGE);

        lastOperationOK = true;
    }

    /**
     * Coloca un robot en la ubicación dada si está libre.
     * Si hay una tienda con tenges se transfiere inmediatamente.
     * 
     * @param location índice de la celda
     */
    public void placeRobot(int location) {
        placeRobot("normal", location);
    }

    /**
     * ✅ SOBRECARGADO: Coloca un robot según tipo
     * 
     * Crea y posiciona robot según el tipo especificado:
     * - "normal" → Robot en posición indicada
     * - "neverback" → NeverbackRobot en posición indicada
     * 
     * @param type     tipo de robot ("normal" o "neverback" o "tender")
     * @param location ubicación para el robot
     */
    public void placeRobot(String type, int location) {
        // Crear robot según tipo
        RobotAbstracto robot = crearRobot(type);

        if (robot == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tipo de robot desconocido: " + type +
                            "\nTipos válidos: normal, neverback, tender",
                    "Error: Tipo inválido",
                    JOptionPane.ERROR_MESSAGE);
            lastOperationOK = false;
            return;
        }

        // Delegar al método privado que maneja la posición y validaciones
        addRobotToLocation(robot, type, location);
    }

    /**
     * Método privado que posiciona el robot con validaciones.
     * 
     * @param robot    robot ya creado
     * @param type     tipo de robot para identificación
     * @param location ubicación indicada
     */
    private void addRobotToLocation(RobotAbstracto robot, String type, int location) {
        if (location < 0 || location >= positions.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Índice de casilla inválido: " + location,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (robotRefs[location] != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ya existe un robot en la casilla " + location,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        robotRefs[location] = robot;
        robots[location][0] = location;
        robots[location][1] = 0;
        robotOrigins[location] = location;

        if (this.visible && road != null) {
            road.assignRobot(robot);
            road.assignObjectPosition(location, "robot");
            robot.makeVisible();
        }

        transferTengesIfCoincide(0); // Robot recién colocado tiene 0 tenges
        lastOperationOK = true;
    }

    /**
     * Mueve un robot existente a una nueva posición (location+meters).
     * 
     * @param location posición actual del robot
     * @param meters   desplazamiento (positivo o negativo)
     */
    public void moveRobot(int location, int meters) {
        // Validar ubicación origen
        if (location < 0 || location >= robotRefs.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Índice origen inválido: " + location,
                    "Error de movimiento",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que existe robot en la ubicación
        RobotAbstracto r = robotRefs[location];
        if (r == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay robot en la casilla " + location,
                    "Error de movimiento",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Delegar la ejecución del movimiento al método privado
        executeRobotMovement(location, meters, r);
    }

    /**
     * Método privado que ejecuta el movimiento del robot.
     * Centraliza toda la lógica de movimiento, incluyendo validaciones especiales
     * para NeverbackRobot.
     * 
     * @param location posición actual del robot
     * @param meters   desplazamiento (positivo o negativo)
     * @param r        referencia al robot a mover
     */
    private void executeRobotMovement(int location, int meters, RobotAbstracto r) {
        // Validación genérica para TODOS los robots (polimórfica)
        if (!validateRobotMovement(r, meters)) {
            return; // Movimiento rechazado, lastOperationOK ya fue establecido
        }

        // Calcular nueva ubicación
        int newLocation = location + meters;
        if (newLocation < 0 || newLocation >= robotRefs.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Movimiento inválido: fuera de límites.",
                    "Error de movimiento",
                    JOptionPane.ERROR_MESSAGE);
            lastOperationOK = false;
            return;
        }

        // Validar que la ubicación destino está libre
        if (robotRefs[newLocation] != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ya existe un robot en la casilla destino " + newLocation,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            lastOperationOK = false;
            return;
        }

        // Guardar información actual del robot
        int tengesActuales = robots[location][1];
        int originVal = robotOrigins[location];
        if (originVal == -1) {
            originVal = location;
        }
        int distance = Math.abs(meters);

        // Mover el robot lógicamente
        robotRefs[location] = null;
        robots[location][0] = 0;
        robots[location][1] = 0;

        robotRefs[newLocation] = r;
        robots[newLocation][0] = newLocation;
        robots[newLocation][1] = 0;

        // Actualizar visualización si está visible
        if (this.visible && road != null) {
            road.assignRobot(r);
            if (useFastMovement) {
                // Modo rápido: instantáneo, sin animación
                road.reposition(location, meters, r);
            } else {
                // Modo lento: animado casilla a casilla
                road.moveRobotVisual(location, meters, r);
            }
        }

        // Recolectar tenges de la tienda (si hay una)
        int collected = transferTengesIfCoincide(tengesActuales);

        // ✅ DELEGACIÓN: Pedir al robot que calcule su propia ganancia
        int netGain = r.calcularGanancia(collected, distance);

        // Actualizar estado del robot y global
        robots[newLocation][1] = tengesActuales + netGain;
        profit += netGain;
        profitPerLocation[newLocation][1] += netGain;
        robotOrigins[newLocation] = originVal;

        // Actualizar interfaz
        updateProgressBar();
        updateMaxProfit();

        lastOperationOK = true;
    }

    /**
     * Valida si un robot puede realizar el movimiento especificado.
     * Este método es completamente genérico y funciona para TODOS los tipos de robots.
     * Cada robot puede sobrescribir isMovementAllowed() para implementar su lógica específica.
     * 
     * Ejemplo polimórfico:
     * - Robot: always returns true (permite cualquier movimiento)
     * - NeverbackRobot: valida según dirección bloqueada
     * - Futuros robots: pueden agregar su propia lógica sin modificar SilkRoad
     * 
     * @param r      referencia al robot (cualquier tipo)
     * @param meters desplazamiento propuesto
     * @return true si el movimiento es permitido, false si está bloqueado
     */
    private boolean validateRobotMovement(RobotAbstracto r, int meters) {
        // Validar que el movimiento sea permitido (polimórfico: cada robot decide)
        if (!r.isMovementAllowed(meters)) {
            // Si el movimiento es rechazado, mostrar mensaje del robot
            String mensaje = r.obtenerMensajeRechazo();
            if (!mensaje.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        mensaje,
                        "Movimiento rechazado",
                        JOptionPane.WARNING_MESSAGE);
            }
            lastOperationOK = false;
            return false;
        }

        // Permitir que cada robot realice acciones al completar movimiento exitoso
        // (ej: NeverbackRobot bloquea dirección, otros hacen nada)
        r.resetDirection();
        return true;
    }

    /**
     * Realiza el movimiento óptimo de los robots hacia las tiendas activas para
     * maximizar la ganancia.
     * Utiliza una estrategia greedy: para cada tienda, selecciona el robot que
     * puede obtener la mayor ganancia neta
     * (tenges de la tienda menos la distancia recorrida). Solo se realizan
     * movimientos si la ganancia es mayor o igual a cero.
     * Al finalizar, muestra un resumen con la ganancia obtenida, el profit total
     * acumulado y la ganancia máxima teórica.
     */
    public void moveRobots() {
        List<Integer> robots = getActiveRobots();
        List<Integer> tiendas = getActiveStores();

        if (tiendas.isEmpty() || robots.isEmpty()) {
            // No hay nada que optimizar; setear maxProfit a 0
            this.maxProfit = 0;
            updateProgressBar();

            JOptionPane.showMessageDialog(
                    null,
                    "No hay tiendas o robots disponibles para mover.",
                    "Sin movimientos posibles",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 🔹 NUEVO: Calcular ganancia máxima teórica (óptima) SIN mover nada
        int gananciaMaxima = calculateMaxProfitGreedy(robots, tiendas);
        this.maxProfit = gananciaMaxima; // Usar esto como tope para la progress bar
        updateProgressBar(); // Actualizar barra con el nuevo tope óptimo

        // Copiar posiciones actuales de los robots (índices en la lista)
        int[] robotActual = new int[robots.size()];
        for (int i = 0; i < robots.size(); i++) {
            robotActual[i] = robots.get(i); // Posición actual
        }

        // Recorrer todas las tiendas en orden sorted
        for (int tienda : tiendas) {
            int mejorGanancia = Integer.MIN_VALUE;
            int robotSeleccionado = -1;

            // Elegir el robot que obtiene mayor ganancia para esta tienda
            for (int i = 0; i < robots.size(); i++) {
                int distancia = Math.abs(tienda - robotActual[i]);
                int ganancia = stores[tienda][1] - distancia; // Tenges - distancia
                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia;
                    robotSeleccionado = i;
                }
            }

            // Solo mover si la ganancia es >= 0 (evita movimientos perdedores;
            if (robotSeleccionado != -1 && mejorGanancia >= 0) {
                int posActualRobot = robotActual[robotSeleccionado];
                int meters = tienda - posActualRobot; // Desplazamiento necesario

                // Ejecutar el movimiento real
                moveRobot(posActualRobot, meters);

                // Actualizar posición del robot para la siguiente asignación
                robotActual[robotSeleccionado] = tienda;
            }
            // Si mejorGanancia < 0, ignoramos (no movemos a esa tienda) - esto coincide con
            // el cálculo óptimo
        }

        // Actualizar barra final (profit actual vs. maxProfit óptimo)
        updateProgressBar();

        // Movimiento óptimo completado
    }

    /**
     * Calcula la ganancia máxima teórica utilizando una estrategia greedy.
     * Para cada tienda, selecciona el robot que puede obtener la mayor ganancia
     * neta
     * (tenges de la tienda menos la distancia recorrida). Solo se consideran
     * ganancias >= 0.
     *
     * @param robots  lista de posiciones de robots activos
     * @param tiendas lista de posiciones de tiendas activas
     * @return ganancia máxima teórica posible
     */
    private int calculateMaxProfitGreedy(List<Integer> robots, List<Integer> tiendas) {
        if (robots.isEmpty() || tiendas.isEmpty())
            return 0;

        // Copiar posiciones actuales de los robots (virtual)
        int[] robotActual = new int[robots.size()];
        for (int i = 0; i < robots.size(); i++) {
            robotActual[i] = robots.get(i);
        }

        int gananciaMaxima = 0; // Solo suma ganancias >=0

        // Recorrer todas las tiendas en orden sorted
        for (int tienda : tiendas) {
            int mejorGanancia = Integer.MIN_VALUE;
            int robotSeleccionado = -1;

            // Elegir el robot que obtiene mayor ganancia para esta tienda
            for (int i = 0; i < robots.size(); i++) {
                int distancia = Math.abs(tienda - robotActual[i]);
                int ganancia = stores[tienda][1] - distancia; // Tenges - distancia
                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia;
                    robotSeleccionado = i;
                }
            }

            // Solo "asignar" (sumar) si la ganancia es >= 0 (omite no rentables)
            if (robotSeleccionado != -1 && mejorGanancia >= 0) {
                gananciaMaxima += mejorGanancia;
                // Actualizar posición virtual del robot para la siguiente asignación
                robotActual[robotSeleccionado] = tienda;
            }
            // Si <0, omitir (no suma nada, como en tu ejemplo de 3 tiendas)
        }

        return gananciaMaxima;
    }

    /**
     * Obtiene la lista de posiciones de robots activos (no nulos).
     * 
     * @return Lista sorted de posiciones de robots activos.
     */
    private List<Integer> getActiveRobots() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < robotRefs.length; i++) {
            if (robotRefs[i] != null) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Obtiene la lista de posiciones de tiendas activas (con tenges > 0).
     * 
     * @return Lista sorted de posiciones de tiendas activas.
     */
    private List<Integer> getActiveStores() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < storeRefs.length; i++) {
            if (storeRefs[i] != null && stores[i][1] > 0) {
                result.add(i);
            }
        }
        Collections.sort(result); // Ordenar por posición, como en getTiendasActivas()
        return result;
    }

    /**
     * Devuelve todos los robots a su posición original registrada.
     * Conserva los tenges actuales de cada robot.
     * Muestra mensajes informativos según el estado (visible/invisible).
     */
    public void returnRobots() {
        boolean anyReturned = false; // para saber si se devolvió al menos un robot
        boolean silentMode = !visible; // 🔹 true si no debe mostrar nada visual

        for (int i = 0; i < robotRefs.length; i++) {
            RobotAbstracto r = robotRefs[i];
            if (r == null)
                continue;

            int originIndex = robotOrigins[i];
            if (originIndex == -1 || originIndex == i)
                continue; // ya está en su lugar o sin origen registrado

            anyReturned = true;

            // 🔹 Mantener los tenges actuales
            int currentTenges = robots[i][1];

            // 🔹 NUEVO: Resetear estado del robot (genérico para todos)
            r.resetDirection();

            // Limpiar la celda actual (solo posición)
            robotRefs[i] = null;
            robots[i][0] = 0;

            // Mover lógicamente al origen
            robotRefs[originIndex] = r;
            robots[originIndex][0] = originIndex;
            robots[originIndex][1] = currentTenges; // conservar tenges

            robotOrigins[originIndex] = originIndex;

            // 🔸 Si visible, actualizar la posición visual
            if (!silentMode && road != null) {
                road.reposition(i, originIndex - i, r);
            }
        }

        // Mensajes solo si está visible
        if (!silentMode) {
            if (anyReturned) {
                JOptionPane.showMessageDialog(
                        null,
                        "Todos los robots han sido devueltos a su posición original.\nLos tenges se han conservado.",
                        "Robots devueltos",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No se encontró ningún robot que necesite regresar a su posición original.",
                        "Sin robots a devolver",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        updateMaxProfit();
        lastOperationOK = true;
    }

    /**
     * Oculta todos los elementos visuales de SilkRoad.
     * No elimina ni modifica datos lógicos, solo la representación gráfica.
     * Si ya está invisible, muestra un mensaje y no realiza cambios.
     */
    public void makeInvisible() {
        if (!visible) {
            JOptionPane.showMessageDialog(
                    null,
                    "SilkRoad ya estaba invisible.",
                    "Estado sin cambios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ocultar tiendas
        if (storeRefs != null) {
            for (StoreAbstracto s : storeRefs) {
                if (s != null)
                    s.makeInvisible();
            }
        }

        // Ocultar robots
        if (robotRefs != null) {
            for (RobotAbstracto r : robotRefs) {
                if (r != null)
                    r.makeInvisible();
            }
        }

        // Ocultar camino y barra
        if (road != null) {
            road.makeInvisible();
        }
        if (progressBar != null) {
            progressBar.makeInvisible();
        }

        visible = false;

    }

    /**
     * Muestra todos los elementos visuales de SilkRoad en sus posiciones actuales.
     * Si ya está visible, muestra un mensaje y no realiza cambios.
     * Restaura las posiciones lógicas actuales de tiendas y robots.
     */
    public void makeVisible() {
        if (visible) {
            JOptionPane.showMessageDialog(
                    null,
                    "SilkRoad ya estaba visible.\nNo se realizaron cambios.",
                    "Sin cambios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar camino
        if (road != null) {
            road.makeVisible();
        }

        // ---- Tiendas: reubicar y mostrar en su celda actual ----
        // ---- Tiendas: restaurar visibilidad y asignar solo las que no tienen posición
        // ----

        if (storeRefs != null) {
            final int OFFSET_X_S = 24; // elige un offset visual coherente con tu diseño
            final int OFFSET_Y_S = 15;
            for (int i = 0; i < storeRefs.length; i++) {
                StoreAbstracto s = storeRefs[i];
                if (s != null) {
                    int cellX = positions[i][0];
                    int cellY = positions[i][1];

                    // Colocar exactamente en su posición lógica
                    s.moveTo(cellX + OFFSET_X_S, cellY + OFFSET_Y_S);
                    s.makeVisible();
                }
            }
        }

        // ---- Robots: reubicar y mostrar en su celda actual ----
        // Usamos un offset canónico dentro de la celda para evitar arrastres previos

        if (robotRefs != null) {
            final int OFFSET_X_R = 6; // 36 - 30
            final int OFFSET_Y_R = 28;
            for (int i = 0; i < robotRefs.length; i++) {
                RobotAbstracto r = robotRefs[i];
                if (r != null) {
                    int cellX = positions[i][0];
                    int cellY = positions[i][1];

                    // Colocar EXACTAMENTE donde corresponde según el índice lógico
                    r.placeTo(cellX + OFFSET_X_R, cellY + OFFSET_Y_R);
                    r.makeVisible();
                }
            }
        }

        if (progressBar != null) {
            progressBar.makeVisible();
        }

        visible = true;
        JOptionPane.showMessageDialog(
                null,
                "SilkRoad ahora es visible nuevamente.\nSe restauraron todas las posiciones actuales.",
                "Visibilidad restaurada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Activa o desactiva el modo de movimiento rápido para los robots.
     * Si está activado, los robots se moverán instantáneamente sin animación
     * visual.
     *
     * @param fast true para activar el movimiento rápido, false para desactivarlo.
     */
    public void setFastMovement(boolean fast) {
        this.useFastMovement = fast;
    }

    /**
     * Reinicia el estado de la SilkRoad a su configuración original.
     * Si está en modo día (dayMode), avanza el schedule y agrega los objetos
     * correspondientes.
     * Restaura los tenges originales de todas las tiendas, devuelve los robots a su
     * posición inicial,
     * reinicia los tenges de los robots a 0, resetea el profit acumulado y
     * actualiza la barra de progreso.
     * Muestra un mensaje informativo si la visualización está activa.
     */
    public void reboot() {
        boolean silentMode = !visible; // true si no debe mostrar nada visual
        checkReboots(silentMode); // 🔹 PASAR silentMode para respetar en diálogos de checkReboots

        // --- Restaurar tiendas a originales ---
        for (int i = 0; i < storeRefs.length; i++) {
            if (storeRefs[i] != null) {
                int originalTenges = originalStoreTenges[i]; // 🔹 USAR EL NUEVO ARRAY
                stores[i][1] = originalTenges; // Restaurar directamente (si 0, queda vacía)
                if (!silentMode)
                    storeRefs[i].makeVisible(); // Solo si visible
            }
        }

        // --- Devolver robots a posiciones originales (conserva tenges temporalmente)
        // ---
        returnRobots(); // Reutiliza el método existente

        // --- Reiniciar los tenges de todos los robots a 0 (después de devolver) ---
        for (int i = 0; i < robots.length; i++) {
            if (robotRefs[i] != null) {
                robots[i][1] = 0; // Reiniciar tenges

                // 🔹 NUEVO: Resetear estado de TODOS los robots (genérico)
                robotRefs[i].resetDirection();
            }
        }

        // Reiniciar profit y actualizar barra
        profit = 0;
        updateProgressBar();

        // Mostrar mensaje solo si está visible
        if (!silentMode) {
            JOptionPane.showMessageDialog(
                    null,
                    "SilkRoad ha sido reiniciado a su estado original.\n" +
                            "Las tiendas fueron restauradas, robots devueltos y tenges reiniciados.",
                    "Reinicio completo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        updateMaxProfit(); // Recalcular max con originales restaurados
        lastOperationOK = true;
    }

    /**
     * Verifica el número de reboots y agrega los objetos correspondientes
     * según la entrada schedule.
     * Llama automáticamente desde reboot() si dayMode está activo.
     * 
     * @param silentMode true si no debe mostrar diálogos (e.g., modo invisible)
     */
    private void checkReboots(boolean silentMode) {
        if (!dayMode || daysSchedule == null)
            return;

        // Avanzar un día
        dayIndexSchedule++;

        // Validar límites
        if (dayIndexSchedule >= daysSchedule.length) {
            if (!silentMode) { // Solo mostrar si no silent
                JOptionPane.showMessageDialog(
                        null,
                        "Todos los objetos del schedule ya fueron agregados.\n" +
                                "Simulación terminada automáticamente.",
                        "Fin del schedule",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            // 🔹 NUEVO: Llamar finish() para terminar la simulación (limpiar y ocultar)
            this.finish();
            return;
        }

        int[] evento = daysSchedule[dayIndexSchedule];
        if (evento == null || evento.length < 2)
            return;

        int tipo = evento[0]; // 1 = robot, 2 = tienda
        int pos = evento[1];

        if (tipo == 1) {
            placeRobot(pos);
            if (!silentMode) { // Solo mostrar si no silent
                JOptionPane.showMessageDialog(
                        null,
                        "Día " + dayIndexSchedule + ": se colocó un robot en la posición " + pos,
                        "Evento diario",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (tipo == 2 && evento.length == 3) {
            int tenges = evento[2];
            placeStore(pos, tenges);

            // Actualizar el valor máximo de profit total
            this.maxProfit += tenges;

            // Actualizar barra visual si corresponde
            if (progressBar != null)
                updateProgressBar();

            if (!silentMode) { // Solo mostrar si no silent
                JOptionPane.showMessageDialog(
                        null,
                        "Día " + dayIndexSchedule + ": se creó una tienda en la posición " + pos +
                                " con " + tenges + " tenges.",
                        "Evento diario",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Devuelve el profit (ganancia total acumulada) actual de la SilkRoad.
     *
     * @return el valor actual de profit (tenges acumulados por los robots menos los
     *         costos de movimiento)
     */
    public int profit() {
        return profit;
    }

    /**
     * @return matriz de tiendas (puede contener filas vacías con ceros).
     */
    public int[][] stores() {
        return stores;
    }

    /**
     * Devuelve la matriz interna que representa los robots en la SilkRoad.
     * Cada fila contiene información sobre un robot:
     * - robots[i][0]: posición del robot (índice de celda)
     * - robots[i][1]: cantidad de tenges que posee el robot en esa posición
     *
     * @return matriz de robots (puede contener filas vacías con ceros si no hay
     *         robot en esa posición)
     */
    public int[][] robots() {
        return robots;
    }

    /**
     * Finaliza la simulación de SilkRoad, limpiando y liberando todos los recursos.
     * Oculta la interfaz gráfica, elimina referencias a objetos gráficos,
     * resetea arrays lógicos y campos globales, y libera memoria.
     * Después de llamar a este método, la instancia de SilkRoad no debe usarse más.
     * Muestra un mensaje informativo si la visualización está activa.
     */
    public void finish() {
        // Paso 1: Ocultar toda la UI gráfica (incluye celdas en road, refs,
        // progressBar)
        makeInvisible();

        // Paso 2: Limpiar referencias a objetos gráficos (tiendas y robots)
        if (storeRefs != null) {
            Arrays.fill(storeRefs, null);
        }
        if (robotRefs != null) {
            Arrays.fill(robotRefs, null);
        }

        // Paso 3: Resetear arrays lógicos (stores, robots, profitPerLocation,
        // originalStoreTenges, robotOrigins)
        if (stores != null) {
            for (int i = 0; i < stores.length; i++) {
                stores[i][0] = 0;
                stores[i][1] = 0;
            }
        }
        if (robots != null) {
            for (int i = 0; i < robots.length; i++) {
                robots[i][0] = 0;
                robots[i][1] = 0;
            }
        }
        if (profitPerLocation != null) {
            for (int i = 0; i < profitPerLocation.length; i++) {
                profitPerLocation[i][1] = 0;
            }
        }
        if (originalStoreTenges != null) {
            Arrays.fill(originalStoreTenges, 0); // 🔹 NUEVO: Resetear originales de tiendas
        }
        if (robotOrigins != null) {
            Arrays.fill(robotOrigins, -1); // 🔹 NUEVO: Resetear orígenes de robots
        }

        // Paso 4: Limpiar schedule si dayMode (opcional, pero libera memoria)
        if (dayMode && daysSchedule != null) {
            daysSchedule = null;
            dayIndexSchedule = -1; // Resetear índice
        }

        // Paso 5: Resetear campos lógicos globales
        profit = 0;
        maxProfit = 0;
        useFastMovement = false; // 🔹 NUEVO: Resetear flag de velocidad

        // Paso 6: Actualizar barra antes de nullificar (por si visible)
        updateProgressBar();

        // Paso 7: Eliminar/Nullificar elementos estructurales (road/camino y
        // progressBar)
        if (road != null) {
            // Road ya oculto via makeInvisible(); nullificar libera celdas y camino
            road = null; // 🔹 NUEVO: Elimina referencia al camino (celdas se GC)
        }
        if (progressBar != null) {
            progressBar = null; // 🔹 NUEVO: Elimina barra de progreso
        }

        // Paso 8: Estado final
        lastOperationOK = true;
    }

    /**
     * Indica si la última operación realizada fue exitosa.
     *
     * @return true si la última operación fue exitosa, false en caso contrario.
     */
    public boolean ok() {
        return lastOperationOK;
    }

    /**
     * Muestra una ventana con las estadísticas actuales de la SilkRoad.
     * Incluye profit total, número de celdas, detalles de robots y tiendas activas.
     * Si no hay robots o tiendas, indica que no hay elementos activos.
     * Utiliza JOptionPane para mostrar la información en un cuadro de diálogo.
     */
    public void consultStatistics() {
        StringBuilder info = new StringBuilder("ESTADÍSTICAS DE SILKROAD\n\n");

        // Resumen general
        info.append("Profit total: ").append(profit).append(" tenges\n");
        info.append("Número de celdas: ").append(length).append("\n\n");

        // Sección de robots
        info.append("ROBOTS:\n");
        boolean anyRobot = false;
        for (int i = 0; i < robotRefs.length; i++) {
            if (robotRefs[i] != null) {
                anyRobot = true;
                info.append(" - Robot en celda ").append(i)
                        .append(" | Tengés: ").append(robots[i][1])
                        .append(" | Origen: ").append(robotOrigins[i] != -1 ? robotOrigins[i] : "Desconocido")
                        .append("\n");
            }
        }
        if (!anyRobot) {
            info.append("   Ningún robot activo.\n");
        }

        info.append("\nTIENDAS:\n");
        boolean anyStore = false;
        for (int i = 0; i < storeRefs.length; i++) {
            if (storeRefs[i] != null) {
                anyStore = true;
                info.append(" - Tienda en celda ").append(i)
                        .append(" | Tengés actuales: ").append(stores[i][1])
                        .append("\n");
            }
        }
        if (!anyStore) {
            info.append("Ninguna tienda activa.\n");
        }

        // Mostrar en ventana
        JOptionPane.showMessageDialog(
                null,
                info.toString(),
                "Estado actual de SilkRoad",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Verifica si algún robot coincide con una tienda en la misma celda.
     * Si hay coincidencia y la tienda tiene tenges, el robot recoge todos los
     * tenges,
     * la tienda se vacía, y se muestra un mensaje informativo.
     * Para FighterStore: solo permite recolección si robot tiene más tenges que la
     * tienda.
     *
     * @param robotCurrentTenges tenges actuales acumulados del robot
     * @return la cantidad de tenges recogidos por el robot, o 0 si no hubo
     *         recolección.
     */
    private int transferTengesIfCoincide(int robotCurrentTenges) {
        for (int i = 0; i < robots.length; i++) {
            RobotAbstracto r = robotRefs[i];
            if (r == null)
                continue;

            StoreAbstracto s = storeRefs[i];
            if (s != null && stores[i][1] > 0) {
 
                
                int storeTenges = stores[i][1];
                
                // 1. Preguntar a la tienda si acepta al robot
                if (!s.puedeRecibirRobot(robotCurrentTenges, storeTenges)) {
                    // ✅ ENCAPSULACIÓN: Tienda genera su propio mensaje de rechazo
                    String mensaje = s.obtenerMensajeRechazo(robotCurrentTenges, storeTenges);
                    return mostrarRechazo(i, mensaje);
                }
                
                // 2. Si es aceptado, obtener los tenges recolectados
                int collected = road.transferirTenges(r, s, storeTenges);
                
                // 3. Tienda procesa la transferencia según su lógica especial
                int finalTransferencia = s.procesarTransferencia(collected);
                
                // 4. SilkRoad actualiza estado y muestra resultado
                return transferTengesPolimorficos(i, finalTransferencia, storeTenges);
            }
        }

        return 0; // si no se recogió nada
    }

    /**
     * Realiza la transferencia de tenges usando polimorfismo.
     * El robot decide cuántos tenges recolecta, SilkRoad actualiza el estado.
     * 
     * @param location posición de la tienda
     * @param collected cantidad que el robot recolecta (definido por el robot)
     * @param totalDisponible cantidad total que había en la tienda
     * @return cantidad de tenges recolectados
     */
    private int transferTengesPolimorficos(int location, int collected, int totalDisponible) {
        int remaining = totalDisponible - collected;
        stores[location][1] = remaining; // Dejar lo que sobra en la tienda

        JOptionPane.showMessageDialog(
                null,
                "Robot en la celda " + location + " recogió " + collected + " tenges.\n" +
                        (remaining > 0 ? "Dejó " + remaining + " tenges en la tienda." : "Tienda completamente vaciada."),
                "Tienda cobrada",
                JOptionPane.INFORMATION_MESSAGE);

        return collected;
    }

    /**
     * Realiza una transferencia normal de tenges de la tienda al robot.
    /**
     * ✅ FASE 5: Mostrar diálogo de rechazo cuando tienda no acepta robot.
     * Método genérico para cualquier tienda que rechace.
     * 
     * @param location    posición de la tienda
     * @param robotTenges tenges actuales del robot
     * @param storeTenges tenges de la tienda
     * @return 0 (sin transferencia)
     */
    private int mostrarRechazo(int location, String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Acceso denegado", 
                                      JOptionPane.WARNING_MESSAGE);
        return 0; // Sin transferencia, ambos mantienen sus tenges
    }

    /**
     * Elimina una tienda en la ubicación dada (si existe).
     * Actualiza el profit máximo y la barra de progreso.
     *
     * @param location índice de la celda
     */
    public void removeStore(int location) {
        if (location < 0 || location >= stores.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Índice de tienda inválido: " + location,
                    "Error al eliminar tienda",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (storeRefs != null && storeRefs[location] != null) {
            storeRefs[location].makeInvisible();
            storeRefs[location] = null;
        }

        maxProfit -= stores[location][1];
        stores[location][0] = 0;
        stores[location][1] = 0;
        originalStoreTenges[location] = 0;
        updateProgressBar();

        JOptionPane.showMessageDialog(
                null,
                "Tienda eliminada correctamente de la casilla " + location + ".",
                "Tienda eliminada",
                JOptionPane.INFORMATION_MESSAGE);

        lastOperationOK = true;
    }

    /**
     * Elimina un robot en la ubicación dada (si existe).
     * 
     * @param location índice de la celda
     */
    public void removeRobot(int location) {
        if (location < 0 || location >= robotRefs.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Índice de robot inválido: " + location,
                    "Error al eliminar robot",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (robotRefs[location] != null) {
            robotRefs[location].makeInvisible();
            robotRefs[location] = null;
        }

        robots[location][0] = 0;
        robots[location][1] = 0;

        JOptionPane.showMessageDialog(
                null,
                "Robot eliminado correctamente de la casilla " + location + ".",
                "Robot eliminado",
                JOptionPane.INFORMATION_MESSAGE);

        lastOperationOK = true;
    }

    /**
     * Actualiza el valor de maxProfit basado en las tiendas y robots actuales.
     * Considera los tenges totales en tiendas y el costo de mover los robots
     * desde su posición original (si se conoce).
     * Asegura que maxProfit sea al menos 1 para evitar división por cero en la
     * barra.
     * Finalmente, actualiza la barra de progreso con el nuevo valor de maxProfit.
     */
    private void updateMaxProfit() {
        int totalTenges = 0;
        int totalCost = 0;

        for (int i = 0; i < stores.length; i++) {
            if (storeRefs[i] != null) {
                totalTenges += stores[i][1];
            }
        }

        for (int i = 0; i < robotRefs.length; i++) {
            if (robotRefs[i] != null) {
                int originIndex = robotOrigins[i];
                if (originIndex == -1)
                    originIndex = i;
                totalCost += Math.abs(i - originIndex);
            }
        }

        maxProfit = Math.max(1, totalTenges - totalCost + profit);

        updateProgressBar();
    }

    /**
     * Actualiza la barra de progreso para reflejar el profit actual frente al
     * maxProfit.
     * Si progressBar es null, no hace nada.
     * Asegura que el tope sea al menos 1 para evitar división por cero.
     */
    private void updateProgressBar() {
        if (progressBar != null) {
            // Usar maxProfit como tope (ahora es la ganancia máxima teórica)
            int tope = Math.max(this.maxProfit, 1); // Evitar división por 0
            progressBar.setProgress(this.profit, tope);
        }
    }

}