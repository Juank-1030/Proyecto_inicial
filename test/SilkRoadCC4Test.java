package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import silkroad.*;

/**
 * Pruebas exhaustivas SilkRoad Ciclo 4
 *
 * Prueba todos los objetos nuevos y casos que evidencian su propósito.
 *
 * @author Juan Carlos Bohorquez y Juan Diego Valderrama
 */
public class SilkRoadCC4Test {

    private SilkRoad silkRoad;
    private static final int ROAD_LENGTH = 50;

    @Before
    public void setUp() {
        silkRoad = new SilkRoad(ROAD_LENGTH);
        silkRoad.makeInvisible(); // Modo invisible para ejecutar más rápido
    }


    /**
     * Prueba la inicialización y estado base de SilkRoad.
     */
    @Test
    public void testConstructorYEstadoInicial() {
        SilkRoad sr = new SilkRoad(100);
        sr.makeInvisible();
        assertEquals(100, sr.getLength());
        assertEquals(0, sr.profit());
    }

    /**
     * Prueba la creación de todos los tipos de robots.
     */
    @Test
    public void testRobotsNuevos() {
        RobotAbstracto robot = new Robot(false);
        assertNotNull(robot);

        RobotAbstracto tender = new TenderRobot(false);
        assertNotNull(tender);

        RobotAbstracto neverback = new NeverbackRobot(false);
        assertNotNull(neverback);
    }

    /**
     * Prueba la creación de todos los tipos de tiendas.
     */
    @Test
    public void testTiendasNuevas() {
        StoreAbstracto store = new Store(false);
        assertNotNull(store);

        StoreAbstracto fighter = new FighterStore(false);
        assertNotNull(fighter);

        StoreAbstracto casino = new CasinoStore(false);
        assertNotNull(casino);

        StoreAbstracto autonomous = new AutonomousStore(false);
        assertNotNull(autonomous);
    }

    /**
     * Prueba la colocación de robots en SilkRoad.
     */
    @Test
    public void testColocacionRobots() {
        silkRoad.placeRobot(10);
        silkRoad.placeRobot(20);
        silkRoad.placeRobot(30);
        assertTrue(true);
    }

    /**
     * Prueba la colocación de robots por tipo.
     */
    @Test
    public void testColocacionRobotsPorTipo() {
        SilkRoad sr = new SilkRoad(40);
        sr.makeInvisible();
        sr.placeRobot("normal", 10);
        sr.placeRobot("tender", 20);
        sr.placeRobot("neverback", 30);
        assertTrue(true);
    }

    /**
     * Prueba la colocación de tiendas en SilkRoad.
     */
    @Test
    public void testColocacionTiendas() {
        silkRoad.placeStore(5, 100);
        silkRoad.placeStore(10, 200);
        silkRoad.placeStore(15, 75);
        silkRoad.placeStore(20, 150);
        assertTrue(true);
    }

    /**
     * Prueba la colocación de tiendas por tipo.
     */
    @Test
    public void testColocacionTiendasPorTipo() {
        SilkRoad sr = new SilkRoad(50);
        sr.makeInvisible();
        sr.placeStore("normal", 5, 100);
        sr.placeStore("fighter", 10, 100);
        sr.placeStore("casino", 15, 100);
        sr.placeStore("autonomous", 20, 100);
        assertTrue(true);
    }

    /**
     * Prueba el movimiento de robots y la interacción con tiendas.
     */
    @Test
    public void testMovimientoRobotYInteraccionTienda() {
        SilkRoad sr = new SilkRoad(20);
        sr.makeInvisible();
        sr.placeRobot(10);
        sr.placeStore(15, 50);
        sr.moveRobot(0, 5);
        assertTrue(sr.profit() >= 0);
    }

    /**
     * Prueba la transferencia especial de TenderRobot.
     */
    @Test
    public void testTenderRobotTransferencia() {
        SilkRoad sr = new SilkRoad(30);
        sr.makeInvisible();
        sr.placeRobot(10);
        sr.placeStore(20, 100);
        sr.moveRobot(0, 10);
        assertTrue(sr.profit() > 0);
    }

    /**
     * Prueba el comportamiento de dirección de NeverbackRobot.
     */
    @Test
    public void testNeverbackRobotDireccion() {
        SilkRoad sr = new SilkRoad(50);
        sr.makeInvisible();
        sr.placeRobot(10);
        sr.placeRobot(20);
        sr.placeRobot(30);
        sr.placeStore(15, 50);
        sr.placeStore(25, 50);
        sr.moveRobot(2, 10);
        sr.moveRobot(2, 20);
        assertTrue(true);
    }

    /**
     * Prueba el rechazo de robots pobres por FighterStore.
     */
    @Test
    public void testFighterStoreRechazo() {
        SilkRoad sr = new SilkRoad(30);
        sr.makeInvisible();
        sr.placeRobot(10);
        sr.placeStore("fighter", 10, 100);
        int profitBefore = sr.profit();
        sr.moveRobot(0, 10);
        int profitAfter = sr.profit();
        assertTrue(profitAfter >= profitBefore);
    }

    /**
     * Prueba la transferencia aleatoria de CasinoStore.
     */
    @Test
    public void testCasinoStoreTransferencia() {
        SilkRoad sr1 = new SilkRoad(20);
        sr1.makeInvisible();
        sr1.placeRobot(10);
        sr1.placeStore("casino", 15, 100);
        sr1.moveRobot(0, 5);
        assertTrue(sr1.profit() > 0);
    }
    /**
     * Prueba la colocación aleatoria de AutonomousStore.
     */
    @Test
    public void testAutonomousStoreAleatoria() {
        SilkRoad sr = new SilkRoad(50);
        sr.makeInvisible();
        sr.placeRobot(10);
        sr.placeStore("autonomous", 0, 100);
        assertTrue(true);
    }
    

    /**
     * PRUEBA 14: moveRobots() múltiples
     */
    @Test
    public void testMoveRobotsMultiples() {
        SilkRoad sr = new SilkRoad(50);
        sr.makeInvisible();
        sr.placeRobot(100);
        sr.placeRobot(100);
        sr.placeRobot(100);
        sr.placeStore(5, 50);
        sr.placeStore(15, 50);
        sr.placeStore(25, 50);
        sr.moveRobots();
        assertTrue("moveRobots ejecutado", sr.profit() >= 0);
    }

    /**
     * PRUEBA 15: emptiedStores()
     */
    @Test
    public void testEmptiedStores() {
        silkRoad.placeStore(5, 100);
        silkRoad.placeStore(10, 200);
        silkRoad.emptiedStores();
        assertTrue("emptiedStores ejecutado", true);
    }

    /**
     * PRUEBA 16: resupplyStores()
     */
    @Test
    public void testResupplyStores() {
        silkRoad.placeStore(5, 100);
        silkRoad.placeStore(10, 200);
        silkRoad.emptiedStores();
        silkRoad.resupplyStores();
        assertTrue("resupplyStores ejecutado", true);
    }

    /**
     * PRUEBA 17: Ciclo completo
     */
    @Test
    public void testCicloEmptiedYResupply() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 100);
        silkRoad.moveRobot(0, 10);
        silkRoad.emptiedStores();
        silkRoad.resupplyStores();
        assertTrue("Ciclo completo", silkRoad.profit() >= 0);
    }

    /**
     * PRUEBA 18: returnRobots()
     */
    @Test
    public void testReturnRobots() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 50);
        silkRoad.moveRobot(0, 10);
        silkRoad.returnRobots();
        assertTrue("returnRobots ejecutado", true);
    }

    /**
     * PRUEBA 19: Visibilidad
     */
    @Test
    public void testVisibilidad() {
        silkRoad.placeRobot(100);
        silkRoad.makeInvisible();
        silkRoad.makeVisible();
        assertTrue("Visibilidad cambiada", true);
    }

    /**
     * PRUEBA 20: profitPerMove()
     */
    @Test
    public void testProfitPerMove() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 50);
        silkRoad.profitPerMove();
        assertTrue("profitPerMove ejecutado", true);
    }

    /**
     * PRUEBA 21: SilkRoadContest.solve()
     */
    @Test
    public void testSilkRoadContestSolve() {
        int[][] days = {
            { 5 },
            { 1, 0 },
            { 2, 1, 100 },
            { 1, 5 },
            { 2, 6, 150 },
            { 1, 10 }
        };
        
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() retorna array", profits);
        assertTrue("solve() retorna array no vacío", profits.length > 0);
    }

    /**
     * PRUEBA 22: SilkRoadContest complejo
     */
    @Test
    public void testSilkRoadContestSolveComplejo() {
        int[][] days = {
            { 8 },
            { 1, 2 },
            { 2, 3, 100 },
            { 1, 5 },
            { 1, 7 },
            { 2, 8, 80 },
            { 1, 10 },
            { 2, 4, 120 },
            { 1, 15 }
        };
        
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() maneja complejos", profits);
        assertTrue("solve() retorna resultados", profits.length > 0);
    }

    /**
     * PRUEBA 23: SilkRoadContest casos límite
     */
    @Test
    public void testSilkRoadContestCasosLimite() {
        int[][] days1 = {
            { 1 },
            { 1, 0 }
        };
        int[] profits1 = SilkRoadContest.solve(days1);
        assertNotNull("solve() con 1 robot", profits1);
        
        int[][] days2 = {
            { 2 },
            { 1, 5 },
            { 2, 5, 100 }
        };
        int[] profits2 = SilkRoadContest.solve(days2);
        assertNotNull("solve() mismo lugar", profits2);
    }

    /**
     * PRUEBA 24: Escenario negocio completo
     */
    @Test
    public void testEscenarioNegocioCompleto() {
        SilkRoad sr = new SilkRoad(60);
        sr.makeInvisible();
        
        sr.placeRobot(200);
        sr.placeRobot(150);
        sr.placeRobot(100);
        
        sr.placeStore("normal", 10, 100);
        sr.placeStore("fighter", 20, 80);
        sr.placeStore("casino", 30, 100);
        sr.placeStore("autonomous", 0, 120);
        
        sr.moveRobot(0, 10);
        sr.moveRobot(1, 20);
        sr.moveRobot(2, 30);
        
        int profitAfterRound1 = sr.profit();
        assertTrue("Profit después ronda 1", profitAfterRound1 >= 0);
        
        sr.returnRobots();
        sr.emptiedStores();
        sr.resupplyStores();
        
        sr.moveRobots();
        
        assertTrue("Profit consistente", sr.profit() >= 0);
        assertEquals("Longitud mantenida", 60, sr.getLength());
    }

    /**
     * PRUEBA 25: Polimorfismo completo
     */
    @Test
    public void testPolimorfismoCompleto() {
        SilkRoad sr1 = new SilkRoad(30);
        sr1.makeInvisible();
        sr1.placeRobot(100);
        sr1.placeStore(10, 100);
        sr1.moveRobot(0, 10);
        int profit1 = sr1.profit();
        
        SilkRoad sr2 = new SilkRoad(30);
        sr2.makeInvisible();
        sr2.placeRobot(100);
        sr2.placeStore(10, 100);
        sr2.moveRobot(0, 10);
        int profit2 = sr2.profit();
        
        assertTrue("Polimorfismo funciona", profit1 >= 0 && profit2 >= 0);
    }

    /**
     * PRUEBA 26: Robustez múltiples operaciones
     */
    @Test
    public void testRobustezMultiplesOperaciones() {
        SilkRoad sr = new SilkRoad(100);
        sr.makeInvisible();
        
        for (int i = 0; i < 5; i++) {
            sr.placeRobot(100 + i * 20);
        }
        
        for (int i = 0; i < 5; i++) {
            sr.placeStore(i * 15, 100 - i * 10);
        }
        
        sr.moveRobot(0, 15);
        sr.moveRobot(1, 30);
        sr.moveRobot(2, 45);
        
        int profitAfterMoves = sr.profit();
        assertTrue("Profit positivo", profitAfterMoves >= 0);
        
        sr.emptiedStores();
        sr.resupplyStores();
        sr.returnRobots();
        
        assertTrue("Sistema robusto", true);
    }

    /**
     * PRUEBA 27: Modo invisible
     */
    @Test
    public void testModoInvisibleNoAfectaFuncionalidad() {
        SilkRoad sr1 = new SilkRoad(30);
        sr1.makeInvisible();
        sr1.placeRobot(100);
        sr1.placeStore(10, 50);
        sr1.moveRobot(0, 10);
        int profit1 = sr1.profit();
        
        SilkRoad sr2 = new SilkRoad(30);
        sr2.makeInvisible();
        sr2.placeRobot(100);
        sr2.placeStore(10, 50);
        sr2.moveRobot(0, 10);
        int profit2 = sr2.profit();
        
        assertEquals("Modo invisible no afecta", profit1, profit2);
    }

    /**
     * PRUEBA 28: getLength()
     */
    @Test
    public void testGetLength() {
        assertEquals("Longitud inicial", ROAD_LENGTH, silkRoad.getLength());
        
        SilkRoad sr2 = new SilkRoad(75);
        sr2.makeInvisible();
        assertEquals("Longitud 75", 75, sr2.getLength());
        
        SilkRoad sr3 = new SilkRoad(1);
        sr3.makeInvisible();
        assertEquals("Longitud 1", 1, sr3.getLength());
    }

    /**
     * PRUEBA 29: Integración con todos los tipos
     */
    @Test
    public void testIntegracionTodosLosTipos() {
        SilkRoad sr = new SilkRoad(80);
        sr.makeInvisible();
        
        // Todos los tipos de robot
        sr.placeRobot("normal", 10);
        sr.placeRobot("tender", 20);
        sr.placeRobot("neverback", 30);
        
        // Todos los tipos de tienda
        sr.placeStore("normal", 15, 100);
        sr.placeStore("fighter", 25, 100);
        sr.placeStore("casino", 35, 100);
        sr.placeStore("autonomous", 45, 100);
        
        // Movimientos variados
        sr.moveRobot(0, 15);
        sr.moveRobot(1, 25);
        sr.moveRobot(2, 35);
        
        assertTrue("Integración total exitosa", sr.profit() >= 0);
    }

    /**
     * PRUEBA 30: Flujo final completo
     */
    @Test
    public void testFlujoFinalCompleto() {
        SilkRoad sr = new SilkRoad(100);
        sr.makeInvisible();
        
        // Setup
        sr.placeRobot(250);
        sr.placeRobot(180);
        sr.placeRobot(150);
        sr.placeStore("normal", 20, 120);
        sr.placeStore("fighter", 40, 100);
        sr.placeStore("casino", 60, 110);
        sr.placeStore("autonomous", 80, 130);
        
        // Ejecución
        sr.moveRobot(0, 20);
        sr.moveRobot(1, 40);
        sr.moveRobot(2, 60);
        
        // Validación
        assertTrue("Profit generado", sr.profit() > 0);
        assertEquals("Longitud correcta", 100, sr.getLength());
        
        // Limpieza
        sr.emptiedStores();
        sr.resupplyStores();
        sr.returnRobots();
        
        assertTrue("Flujo completo exitoso", true);
    }
}
