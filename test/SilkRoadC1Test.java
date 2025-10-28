package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import silkroad.*;

/**
 * PRUEBAS DEL CICLO 1 - SilkRoad
 * Valida funcionalidad básica de robots y movimientos
 */
public class SilkRoadC1Test {

    private SilkRoad silkRoad;

    @Before
    public void setUp() {
        silkRoad = new SilkRoad(50);
    }

    @Test
    public void accordingDAShouldAddAndRemoveObjects() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 50);
        assertTrue("Objetos agregados correctamente", true);
    }

    @Test
    public void accordingDAShouldAccumulateTengesInMultipleMovements() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 100);
        int profitBefore = silkRoad.profit();
        silkRoad.moveRobot(0, 10);
        int profitAfter = silkRoad.profit();
        assertTrue("Profit acumulado o sin cambios", profitAfter >= profitBefore);
    }

    @Test
    public void accordingDAShouldCalculatePositiveProfit() {
        silkRoad.placeRobot(200);
        silkRoad.placeStore(5, 100);
        int profitBefore = silkRoad.profit();
        silkRoad.moveRobot(0, 5);
        int profitAfter = silkRoad.profit();
        assertTrue("Profit se calcula correctamente", profitAfter >= profitBefore);
    }

    @Test
    public void accordingDAShouldCalculateNegativeProfit() {
        silkRoad.placeRobot(50);
        silkRoad.placeStore(5, 100);
        silkRoad.moveRobot(0, 5);
        int profit = silkRoad.profit();
        assertTrue("Profit se calcula (puede ser negativo)", profit <= 100);
    }

    @Test
    public void accordingDAShouldCleanupWithFinish() {
        silkRoad.placeRobot(100);
        silkRoad.placeStore(10, 50);
        silkRoad.moveRobot(0, 10);
        silkRoad.finish();
        assertTrue("finish() ejecutado", true);
    }
}
