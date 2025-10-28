package test;

import static org.junit.Assert.*;
import org.junit.Test;
import silkroad.*;

/**
 * PRUEBAS DE CONTEST C - SilkRoadContestCTest
 * Valida casos especiales del contest
 */
public class SilkRoadContestCTest {

    @Test
    public void shouldHandleSingleRobot() {
        int[][] days = {
            { 1 },
            { 1, 5 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() maneja 1 robot", profits);
    }

    @Test
    public void shouldHandleMultipleStoresAtSameLocation() {
        int[][] days = {
            { 2 },
            { 1, 5 },
            { 2, 5, 100 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() maneja tiendas en mismo lugar", profits);
    }

    @Test
    public void shouldReturnValidProfitsArray() {
        int[][] days = {
            { 3 },
            { 1, 0 },
            { 2, 10, 100 },
            { 1, 20 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertTrue("Array tiene longitud correcta", profits.length == 3);
    }
}
