package main.java.output;

import java.util.HashMap;
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
    public Map<Integer, Route> vehicleIdRoute = new HashMap<>();
    public Map<Integer, List<Visit>> vehicleIdVisits = new HashMap<>();
    public Map<Integer, Integer> vehicleIdCost = new HashMap<>();

    public Day(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("DAY = ").append(id).append("\n");
        buff.append("NUMBER_OF_VEHICLES = ").append(numOfVehicles).append("\n");

        buff.append("START_DEPOT = ");
        for (int i = 0; i < startDepot.length; i++) {
            buff.append(startDepot[i]).append(" ");
        }
        buff.deleteCharAt(buff.length() - 1).append("\n");

        buff.append("FINISH_DEPOT = ");
        for (int i = 0; i < finishDepot.length; i++) {
            buff.append(finishDepot[i]).append(" ");
        }
        buff.deleteCharAt(buff.length() - 1).append("\n");

        for (Integer vehicleId : usedVehiclesSorted) {
            // routes
            Route route = vehicleIdRoute.get(vehicleId);
            buff.append(vehicleId).append("\t").append("R").append("\t");
            for (Integer placeId : route.visitedPlaces) {
                buff.append(placeId).append("\t");
            }
            buff.deleteCharAt(buff.length() - 1).append("\n");

            // visits to depot
            for (Visit visit : vehicleIdVisits.get(vehicleId)) {
                buff.append(vehicleId).append("\t").append("V").append("\t").append(visit.visitNumber).append("\t");
                for (int i = 0; i < visit.tools.length; i++) {
                    buff.append(visit.tools[i]).append("\t");
                }
                buff.deleteCharAt(buff.length() - 1).append("\n");
            }

            //total cost of that vehicle
            buff.append(vehicleId).append("\t").append("D").append("\t");
            buff.append(vehicleIdCost.get(vehicleId)).append("\n");
        }
        return buff.toString();
    }
}
