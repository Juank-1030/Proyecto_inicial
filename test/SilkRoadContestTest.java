package test;

import static org.junit.Assert.*;
import org.junit.Test;
import silkroad.*;

/**
 * PRUEBAS DE CONTEST - SilkRoadContestTest
 * Valida la funcionalidad de contest
 */
public class SilkRoadContestTest {

    @Test
    public void shouldSolveBasicContest() {
        int[][] days = {
            { 2 },
            { 1, 0 },
            { 2, 5, 100 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() retorna resultados", profits);
        assertTrue("solve() retorna array no vacío", profits.length > 0);
    }

    @Test
    public void shouldSolveMultipleDays() {
        int[][] days = {
            { 3 },
            { 1, 5 },
            { 2, 10, 100 },
            { 1, 15 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() maneja múltiples días", profits);
        assertTrue("solve() retorna resultados válidos", profits.length > 0);
    }

    @Test
    public void shouldSolveComplexContest() {
        int[][] days = {
            { 5 },
            { 1, 0 },
            { 2, 5, 100 },
            { 1, 10 },
            { 2, 15, 150 },
            { 1, 20 }
        };
        int[] profits = SilkRoadContest.solve(days);
        assertNotNull("solve() maneja casos complejos", profits);
        assertTrue("Todos los días tienen resultado", profits.length == 5);
    }
}
