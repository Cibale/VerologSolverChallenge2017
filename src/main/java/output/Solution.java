package main.java.output;

import main.java.DayRoute;
import main.java.ProblemModel;
import main.java.Request;
import main.java.Vehicle;
import main.java.genetic_algorithm.Chromosome;

import java.util.*;

/**
 * Created by mmatak on 6/15/17.
 */
public class Solution {
    public String dataset;
    public String name;

    public int maxNumOfVehicles;
    public int numOfVehicleDays;
    // index is id of tool, value is how many of those tools are used
    public int[] toolUse;
    public int distance;
    public int cost;
    public List<Day> days = new ArrayList<>();
    private ProblemModel model;

    public Solution(ProblemModel model) {
        this.model = model;
        this.dataset = model.dataset;
        this.name = model.name;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("DATASET = ").append(dataset).append("\n");
        buff.append("NAME = ").append(name).append("\n");
        buff.append("\n");
        buff.append("MAX_NUMBER_OF_VEHICLES = ").append(maxNumOfVehicles).append("\n");
        buff.append("NUMBER_OF_VEHICLE_DAYS = ").append(numOfVehicleDays).append("\n");
        buff.append("TOOL_USE =");
        for (int i = 0; i < toolUse.length; i++) {
            buff.append(" ").append(toolUse[i]);
        }
        buff.append("\n");
        buff.append("DISTANCE = ").append(distance);
        buff.append("COST = ").append(cost);
        for (Day day : days) {
            buff.append(day.toString());
            buff.append("\n");
        }
        buff.deleteCharAt(buff.length() - 1);
        return buff.toString();
    }

    /**
     * Constructs solution from chromosme
     *
     * @param chromosome chromosme from which solution is constructed
     */
    public void constructFrom(Chromosome chromosome) {
        /*
        1. sort requests per days (lower day - lower index)
        2. for each day spent:
            2.1. check number of vehicles used that day (update this number as maxNumOfVehicles)
            2.2. create routes per vehicle (*you have this in vehicle class*)
            2.2. set depot values (based on previous day + routes that day)
            2.3. for each vehicle that day:
                2.3.1. write his route
                2.3.2. for each visit to depot:
                    2.3.3. write which tools he has taken / returned to depot
                    2.3.4. update how many which kind of tools is used
                2.3.3. write his DISTANCE COST for that day
        3. set cost based on chromosome cost without punishment
         */
        //1.
        Arrays.sort(chromosome.requests, Comparator.comparingInt(o -> o.pickedDayForDelivery));
        //2.
        int currentDay = -1;
        int currentDayIndexStart = -1;
        int currentDayIndexEnd = -1;
        for (int i = 0; i < chromosome.requests.length; i++) {
            if (currentDay == -1) {
                currentDay = chromosome.requests[i].pickedDayForDelivery;
                currentDayIndexStart = i;
                currentDayIndexEnd = i;
                continue;
            }
            if (chromosome.requests[i].pickedDayForDelivery == currentDay) {
                currentDayIndexEnd = i;
            } else {
                processDay(chromosome.requests, currentDayIndexStart, currentDayIndexEnd, currentDay);
                currentDay = chromosome.requests[i].pickedDayForDelivery;
                currentDayIndexStart = i;
                currentDayIndexEnd = i;
            }
        }
        //3.
        cost = chromosome.realCost;
    }

    /*
    2. for each day spent:
            2.1. check number of vehicles used that day (update this number as maxNumOfVehicles)
            2.2. create routes per vehicle (*you have this in vehicle class*)
            2.2. TODO: set depot values (based on previous day + routes that day)
            2.3. for each vehicle that day:
                2.3.1. write his route
                2.3.2. for each visit to depot:
                    2.3.3. TODO: write which tools he has taken / returned to depot
                    2.3.4. update how many which kind of tools is used
                2.3.3. write his DISTANCE COST for that day
     */
    private void processDay(Request[] requests, int indexStart, int indexEnd, int currentDay) {
        // check number of vehicles used that day
        Set<Vehicle> vehiclesUsedThisDay = new HashSet<>();
        Day previousDay;
        if (days.size() > 0) {
            previousDay = days.get(days.size() - 1);
        } else {
            previousDay = null;
        }
        for (int i = indexStart; i <= indexEnd; i++) {
            vehiclesUsedThisDay.add(requests[i].correspondingVehicle);
        }
        if (vehiclesUsedThisDay.size() > maxNumOfVehicles) {
            maxNumOfVehicles = vehiclesUsedThisDay.size();
        }

        Day day = new Day(currentDay);
        day.finishDepot = new int[model.tools.length];
        if (previousDay == null) {
            day.startDepot = new int[model.tools.length];
        } else {
            day.startDepot = Arrays.copyOf(previousDay.finishDepot, previousDay.finishDepot.length);
        }
        day.numOfVehicles = vehiclesUsedThisDay.size();
        processVehiclesOnTheDay(day, vehiclesUsedThisDay);
        days.add(day);
    }

    private void processVehiclesOnTheDay(Day day, Set<Vehicle> vehiclesUsedThisDay) {
        List<Vehicle> vehicles = new ArrayList<>(vehiclesUsedThisDay);
        vehicles.sort(Comparator.comparingInt(o -> o.id));
        for (Vehicle vehicle : vehicles) {
            Route route = new Route();
            DayRoute dayRoute = vehicle.dayRouteMap.get(day.id);
            route.vehicleId = vehicle.id;
            //DEPOT ID
            route.visitedPlaces.add(0);
            int visitNumber = 1;
            for (List<Request> routes : dayRoute.routes) {
                Visit visit = new Visit();
                visit.dayId = day.id;
                visit.vehicleId = vehicle.id;
                visit.visitNumber = visitNumber;
                // TODO: write which tools he has taken / returned to depot
                for (Request req : routes) {
                    route.visitedPlaces.add(req.customerId);
                    model.tools[req.toolId].numberNeeded += req.numOfTools;
                }
                visitNumber++;
                route.visitedPlaces.add(0);
            }
            day.vehicleIdRoute.put(vehicle.id, route);
            day.vehicleIdCost.put(vehicle.id, dayRoute.totalRouteDistance);
        }
    }
}
