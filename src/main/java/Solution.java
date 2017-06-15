package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmatak on 6/15/17.
 */
public class Solution {
    public String dataset;
    public String name;

    public int numOfVehicles;
    public int numOfVehicleDays;
    // index is id of tool, value is how many of those tools are used
    public int[] toolUse;
    public int distance;
    public int cost;
    public List<Day> days = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (Day day : days) {
            buff.append("DAY = ").append(day.id).append("\n");
            buff.append("NUMBER_OF_VEHICLES = ").append(day.numOfVehicles).append("\n");

            buff.append("START_DEPOT = ");
            for (int i = 0; i < day.startDepot.length; i++) {
                buff.append(day.startDepot[i]).append(" ");
            }
            buff.deleteCharAt(buff.length() - 1).append("\n");

            buff.append("FINISH_DEPOT = ");
            for (int i = 0; i < day.finishDepot.length; i++) {
                buff.append(day.finishDepot[i]).append(" ");
            }
            buff.deleteCharAt(buff.length() - 1).append("\n");

            for (Integer vehicleId : day.usedVehiclesSorted) {
                // routes
                Route route = day.vehicleIdRoute.get(vehicleId);
                buff.append(vehicleId).append("\t").append("R").append("\t");
                for (Integer placeId : route.visitedPlaces) {
                    buff.append(placeId).append("\t");
                }
                buff.deleteCharAt(buff.length() - 1).append("\n");

                // visits to depot
                buff.append(vehicleId).append("\t").append("V").append("\t");
                Visit visit = day.vehicleIdVisits.get(vehicleId);
                for (int i = 0; i < visit.tools.length; i++) {
                    buff.append(visit.tools[i]).append("\t");
                }
                buff.deleteCharAt(buff.length() - 1).append("\n");

                //total cost of that vehicle
                buff.append(vehicleId).append("\t").append("D");
                buff.append(day.vehicleIdCost.get(vehicleId)).append("\n");

                //line of separation between days
                buff.append("\n");
            }

        }
        return buff.toString();
    }
}
