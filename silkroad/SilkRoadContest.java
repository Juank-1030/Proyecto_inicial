package silkroad;

import java.util.*;

/**
 * Clase que simula un concurso en la Ruta de la Seda con robots y tiendas.
 * Permite calcular la ganancia máxima posible a través de eventos progresivos.
 *
 * <p>
 * La clase maneja la posición de los robots, las tiendas y sus tenges
 * disponibles.
 * Proporciona métodos para resolver el problema numéricamente y para simular
 * visualmente el proceso en una interfaz gráfica.
 * </p>
 *
 * <p>
 * El método principal {@link #solve(int[][])} calcula la ganancia máxima
 * para una serie de eventos sin usar la simulación gráfica, mientras que
 * {@link #simulate(int[][], boolean)} ejecuta una simulación visual completa
 * con opciones de velocidad.
 * </p>
 *
 * @version 1.0
 * @since 2024-06
 */
public class SilkRoadContest {

    private int[] robotPos; // posición de cada robot (-1 = no existe)
    private int[] storeTenges; // tenges disponibles en cada tienda
    private boolean[] storeExists; // si existe tienda en esa posición
    private int profit; // ganancia acumulada

    /**
     * Constructor privado para inicializar el concurso con un tamaño específico.
     *
     * @param length tamaño máximo del camino (posición máxima)
     */
    private SilkRoadContest(int length) {
        this.robotPos = new int[length + 1];
        this.storeTenges = new int[length + 1];
        this.storeExists = new boolean[length + 1];
        Arrays.fill(robotPos, -1);
        this.profit = 0;
    }

    /**
     * Resuelve el problema de la Ruta de la Seda para una serie de eventos.
     * Cada evento puede ser la aparición de un robot o una tienda con tenges.
     * Calcula la ganancia máxima posible progresivamente y aplica una optimización
     * para eventos posteriores al punto de ganancia máxima.
     *
     * @param days arreglo bidimensional donde:
     *             - days[0][0] es el número total de eventos.
     *             - days[i] (i>0) es un evento con formato [tipo, posición,
     *             tenges?]
     *             donde tipo=1 (robot), tipo=2 (tienda con tenges).
     * @return arreglo unidimensional con la ganancia máxima después de cada evento,
     *         aplicando la optimización descrita.
     */
    public static int[] solve(int[][] days) {
        if (days == null || days.length < 2)
            return new int[0];

        // Determinar tamaño máximo del camino
        int maxPos = 0;
        for (int i = 1; i < days.length; i++) {
            if (days[i] != null && days[i].length > 1) {
                maxPos = Math.max(maxPos, days[i][1]);
            }
        }

        // Lista de profits parciales
        List<Integer> profitsParciales = new ArrayList<>();
        int maxProfitGlobal = 0; // Para trackear la máxima ganancia encontrada
        int mejorLimite = 1; // Hasta qué evento tenemos la máxima ganancia

        int numEvents = days[0][0];

        // Ir agregando eventos progresivamente
        for (int limite = 1; limite <= numEvents; limite++) {
            SilkRoadContest contest = new SilkRoadContest(maxPos);

            // Cargar los primeros 'limite' eventos
            for (int i = 1; i <= limite && i < days.length; i++) {
                int[] event = days[i];
                if (event == null || event.length < 2)
                    continue;

                int type = event[0];
                int pos = event[1];

                if (type == 1) {
                    contest.robotPos[pos] = pos; // Robot
                } else if (type == 2 && event.length >= 3) {
                    contest.storeTenges[pos] = event[2]; // Tienda
                    contest.storeExists[pos] = true;
                }
            }

            // Calcular profit actual
            contest.calculateOptimalProfit();
            int profitActual = contest.profit;
            profitsParciales.add(profitActual);

            // Verificar si este es el nuevo máximo
            if (profitActual > maxProfitGlobal) {
                maxProfitGlobal = profitActual;
                mejorLimite = limite;
            }
            // Si el profit actual es menor que el máximo global, usar el máximo anterior
            else if (profitActual < maxProfitGlobal) {
                profitsParciales.set(profitsParciales.size() - 1, maxProfitGlobal);
            }
            // Si es igual, mantener el máximo
        }

        // Aplicar la optimización: para eventos posteriores al mejor límite, usar el
        // máximo global
        for (int i = mejorLimite; i < profitsParciales.size(); i++) {
            profitsParciales.set(i, maxProfitGlobal);
        }

        // Convertir List<Integer> a int[] para el retorno
        int[] resultado = new int[profitsParciales.size()];
        for (int i = 0; i < profitsParciales.size(); i++) {
            resultado[i] = profitsParciales.get(i);
        }

        return resultado;
    }

    /**
     * Calcula la ganancia óptima actual moviendo los robots a las tiendas
     * de manera que se maximice la ganancia total.
     * Este método es una implementación voraz que asigna cada tienda
     * al robot que le proporciona la mayor ganancia inmediata.
     */
    private void calculateOptimalProfit() {
        List<Integer> tiendas = getTiendasActivas();
        List<Integer> robots = getRobotsActivos();
        if (tiendas.isEmpty() || robots.isEmpty())
            return;

        // Copiar posiciones actuales de los robots
        int[] robotActual = new int[robots.size()];
        for (int i = 0; i < robots.size(); i++) {
            robotActual[i] = robots.get(i);
        }

        // Recorrer todas las tiendas
        for (int tienda : tiendas) {
            int mejorGanancia = Integer.MIN_VALUE;
            int robotSeleccionado = -1;

            // Elegir el robot que obtiene mayor ganancia para esta tienda
            for (int i = 0; i < robots.size(); i++) {
                int distancia = Math.abs(tienda - robotActual[i]);
                int ganancia = storeTenges[tienda] - distancia;
                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia;
                    robotSeleccionado = i;
                }
            }

            // Mover al robot seleccionado y sumar ganancia
            if (robotSeleccionado != -1) {
                profit += mejorGanancia;
                robotActual[robotSeleccionado] = tienda;
            }
        }
    }

    private List<Integer> getRobotsActivos() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < robotPos.length; i++) {
            if (robotPos[i] == i)
                result.add(i);
        }
        return result;
    }

    private List<Integer> getTiendasActivas() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < storeTenges.length; i++) {
            if (storeExists[i] && storeTenges[i] > 0)
                result.add(i);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Simula visualmente el concurso en la Ruta de la Seda usando una interfaz
     * gráfica.
     * Muestra progresivamente la adición de robots y tiendas, y luego optimiza
     * los movimientos de los robots para maximizar la ganancia.
     *
     * @param days arreglo bidimensional donde:
     *             - days[0][0] es el número total de eventos.
     *             - days[i] (i>0) es un evento con formato [tipo, posición,
     *             tenges?]
     *             donde tipo=1 (robot), tipo=2 (tienda con tenges).
     * @param slow si es true, los movimientos del robot serán lentos (visibles);
     *             si es false, los movimientos serán rápidos (reposition).
     */
    public static void simulate(int[][] days, boolean slow) {
        if (days == null || days.length < 2)
            return; // Salida silenciosa si inválido

        int numEvents = days[0][0];
        if (numEvents <= 0)
            return; // No hay eventos, fin inmediato

        // Crear SilkRoad con schedule (activa dayMode, carga days para reboots
        // automáticos)
        SilkRoad road = new SilkRoad(days); // 🔹 CAMBIO: Usa constructor con schedule

        // Asegurar visibilidad inicial (siempre visible)
        road.makeVisible();

        // Paso 1: Reboots progresivos para agregar TODOS los objetos (uno por evento)
        // Cada reboot() llama checkReboots(), que agrega days[i] (i=1 a numEvents)
        for (int i = 1; i <= numEvents; i++) {
            road.reboot(); // Agrega el evento i (robot o tienda)

            // Reforzar visibilidad después de cada reboot (mantiene estable durante
            // progresión)
            road.makeVisible();
        }

        // Paso 2: Configurar velocidad del robot antes de optimizar
        road.setFastMovement(!slow); // true para rápido (reposition); false para lento

        // Reforzar visibilidad post-setter
        road.makeVisible();

        // Paso 3: Ejecutar optimización final con todos los objetos agregados
        // (movimientos visibles, según velocidad)
        road.moveRobots();

        // Reforzar visibilidad post-movimientos
        road.makeVisible();

        // Paso 4: Reboot EXTRA para activar finish() (excede schedule, termina
        // simulación)
        // Esto entra en el if de checkReboots() (dayIndexSchedule > numEvents), llama
        // finish()
        road.reboot(); // 🔹 NUEVO: El "reboot más" que activa finish en lugar de agregar

        // Reforzar visibilidad final (aunque finish oculta, por consistencia; opcional)
        // Nota: finish() hace makeInvisible() y limpia; si quieres mantener visible
        // post-finish, comenta esto
        // road.makeVisible(); // Descomenta si prefieres visible al final (revierte
        // hide de finish)

        // Fin: Simulación terminada con finish() activado automáticamente
    }
}
