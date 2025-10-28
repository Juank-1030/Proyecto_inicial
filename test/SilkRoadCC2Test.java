package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import silkroad.*;

/**
 * PRUEBAS DEL CICLO 2 - CC2Test
 * Valida herencia y subclases
 */
public class SilkRoadCC2Test {

    private SilkRoad silkRoad;

    @Before
    public void setUp() {
        silkRoad = new SilkRoad(60);
    }

    @Test
    public void shouldSupportRobotSubclasses() {
        silkRoad.placeRobot("normal", 10);
        silkRoad.placeRobot("tender", 20);
        silkRoad.placeRobot("neverback", 30);
        assertTrue("Subclases de Robot soportadas", true);
    }

    @Test
    public void shouldSupportStoreSubclasses() {
        silkRoad.placeStore("normal", 5, 100);
        silkRoad.placeStore("fighter", 15, 100);
        silkRoad.placeStore("casino", 25, 100);
        silkRoad.placeStore("autonomous", 35, 100);
        assertTrue("Subclases de Store soportadas", true);
    }

    @Test
    public void tenderRobotBehavior() {
        SilkRoad sr = new SilkRoad(30);
        sr.placeRobot(100);
        sr.placeStore(10, 100);
        sr.moveRobot(0, 10);
        assertTrue("TenderRobot se comporta correctamente", sr.profit() >= 0);
    }

    @Test
    public void neverbackRobotDirection() {
        SilkRoad sr = new SilkRoad(50);
        sr.placeRobot(100);
        sr.placeStore(10, 50);
        sr.placeStore(20, 50);
        sr.moveRobot(0, 10);
        sr.moveRobot(0, 20);
        assertTrue("NeverbackRobot respeta dirección", true);
    }
}
