package main.java;

import java.util.List;
import java.util.Map;

/**
 * Created by mmatak on 6/15/17.
 */
public class Day {
    public int id;
    public int numOfVehicles;
    public int[] startDepot;
    public int[] finishDepot;
    public List<Integer> usedVehiclesSorted;
    public Map<Integer, Route> vehicleIdRoute;
    public Map<Integer, Visit> vehicleIdVisits;
    public Map<Integer, Integer> vehicleIdCost;
}
