package test.java;

import static org.junit.Assert.assertEquals;

import main.java.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mmatak on 6/15/17.
 */
public class SolutionTest {
    @Test
    public void toStringTest() {
        Solution solution = new Solution(new ProblemModel());
        solution.days = new ArrayList<>();
        Day day = new Day();
        solution.days.add(day);
        day.id = 10;
        day.numOfVehicles = 10;
        day.startDepot = new int[]{2, 1, 1, 3};
        day.finishDepot = new int[]{2, 1, 1, 3};
        day.usedVehiclesSorted = new ArrayList<>();
        day.usedVehiclesSorted.add(5);
        day.vehicleIdRoute = new HashMap<>();
        Route route = new Route();
        route.visitedPlaces = new ArrayList<>();
        route.visitedPlaces.add(0);
        route.visitedPlaces.add(1);
        route.visitedPlaces.add(0);
        day.vehicleIdRoute.put(5, route);
        day.vehicleIdVisits = new HashMap<>();
        List<Visit> visitList = new ArrayList<>();
        day.vehicleIdVisits.put(5, visitList);
        Visit visit = new Visit();
        visit.vehicleId = 5;
        visit.visitNumber = 1;
        visit.tools = new int[]{-1, 0, 0, 0};
        visitList.add(visit);
        Visit visit2 = new Visit();
        visit2.vehicleId = 5;
        visit2.visitNumber = 2;
        visit2.tools = new int[4];
        visitList.add(visit2);
        day.vehicleIdCost = new HashMap<>();
        day.vehicleIdCost.put(5, 82);
        assertEquals(
                "DAY = 10\n" +
                        "NUMBER_OF_VEHICLES = 10\n" +
                        "START_DEPOT = 2 1 1 3\n" +
                        "FINISH_DEPOT = 2 1 1 3\n" +
                        "5\tR\t0\t1\t0\n" +
                        "5\tV\t1\t-1\t0\t0\t0\n" +
                        "5\tV\t2\t0\t0\t0\t0\n" +
                        "5\tD\t82\n", day.toString());
    }
}
