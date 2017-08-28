package main.java.genetic_algorithm;

import main.java.DayRoute;
import main.java.ProblemModel;
import main.java.Request;
import main.java.Vehicle;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by felentovic on 28/08/17.
 */
public class VehicleChromosome extends Chromosome {
    public Vehicle[] vehicles;

    public VehicleChromosome(ProblemModel model) {
        super(model);
    }

    public VehicleChromosome(ProblemModel model, Vehicle[] vehicles, Request[] requests) {
        super(model);
        this.requests = new Request[requests.length];
        for (int i = 0; i < requests.length; i++) {
            this.requests[i] = new Request(requests[i]);
        }

        this.vehicles = new Vehicle[vehicles.length];
        for (int i = 0; i < vehicles.length; i++) {
            this.vehicles[i] = new Vehicle(vehicles[i], this);
        }


    }

    public static Chromosome createNew(ProblemModel model) {
        return new VehicleChromosome(model);
    }

    @Override
    public int getGenomeValue(int index) {

        return vehicles[index].id;
    }

    @Override
    public void setGenomeValue(int index, int value) {
        vehicles[requests[index].correspondingVehicleId].removeRequest(index);
        requests[index].correspondingVehicleId = value;
        vehicles[requests[index].correspondingVehicleId].addRequest(index);

    }

    public void initialize() {
        // in worst case - one vehicle per request
        // because of empty first request id (no request with id 0)
        vehicles = new Vehicle[model.requests.length + model.negativeRequests.length];
        requests = new Request[model.requests.length + model.negativeRequests.length];
        int index = 0;
        for (int i = 0; i < model.requests.length; i++) {
            requests[index++] = new Request(model.requests[i]);
        }
        for (int i = 0; i < model.negativeRequests.length; i++) {
            if (index != model.negativeRequests[i].id) {
                System.out.println("Error line 64 in Chromosme.java - id doesn't match");
            }
            requests[index++] = new Request(model.negativeRequests[i]);
        }
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i] = new Vehicle(i, this);
        }
        for (int i = 0; i < requests.length; i++) {
            int vehicleIndex = ThreadLocalRandom.current().nextInt(vehicles.length);
            vehicles[vehicleIndex].addRequest(i);
            requests[i].correspondingVehicleId = vehicleIndex;
        }
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i].update();
        }

    }

    @Override
    public int getChromosomeLength() {
        return vehicles.length;
    }


    @Override
    public Chromosome mutateOnIndex(int index) {
        return null;
    }

    /**
     * Total cost is calculated in a way described below.
     * Idea is to have as less cost as possible.
     * <p>
     * Let RVC be cost per "renting" a vehicle,
     * no matter how many time is used as soon as it is more than once).
     * Let TRVC be total "renting" vehicle cost, i.e. total costs related to vehicles.
     * Then TRVC = max{numberOfVehiclesPerDay} * RVC
     * <p>
     * Let UVC be cost of "using" vehicle per day.
     * Let TUVC be total cost of using all the vehicles.
     * Then TUVC = sum by each day in time horizont: numberOfUsedCarsThatDay * UVC
     * <p>
     * Let DTC be cost per unit totalVehicleDistance traveled.
     * Let TDTC be total cost for all totalVehicleDistance traveled costs.
     * Then TDTC =  sum by each vehicle: sum by each route travelled: totalVehicleDistance per route
     * <p>
     * Let TC be total cost for all used tools.
     * Then TC = sum by kinds of tools used: cost of using that kind of tool
     * <p>
     * Let COST denote cost of our solution.
     * Then COST = TRVC + TUVC + TDTC + TC.
     * <p>
     * i.e. total cost of our solution TCOST = COST + PUNISHMENT.
     *
     * @return total cost of solution
     */
    public final Long evaluate() {
        long trvc = calculateTRVC();
        long tuvc = calculateTUVC();
        long tdtc = calculateTDTC();
        //int tc = calculateTC(chromosome);
        long cost = trvc + tuvc + tdtc;// + tc;
        realCost = cost;
        long punishment = calculatePunishment();
        totalCost = cost + punishment;
        return cost + punishment;
    }

    /**
     * This is always the same. This cost is optimized on the higher level when picking day for delivery request
     *
     * @return
     */
//    private int calculateTC(chromosome chromosome) {
//        //cost for using tool
//    }
    private long calculateTDTC() {
        long totalDist = 0;
        for (int i = 0; i < vehicles.length; i++) {
            totalDist += vehicles[i].totalVehicleDistance;
        }

        return totalDist * model.distanceCost;
    }

    private long calculateTUVC() {
        //for each vehicle add number of used daysMap
        long totalUsedDays = 0;
        for (int i = 0; i < vehicles.length; i++) {
            totalUsedDays += vehicles[i].dayRouteMap.size();
        }
        return totalUsedDays * model.vehicleDayCost;
    }

    private long calculateTRVC() {
        long numOfUsedVehicle = 0;
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i].usedVehicle()) {
                numOfUsedVehicle++;
            }
        }
        return numOfUsedVehicle * model.vehicleCost;
    }


    private long calculatePunishment() {
        //punishment for: 1.) exceeded distance traveled
        //                2.) exceeded load capacity
        long totalExceededDistance = 0;
        long totalExceededLoad = 0;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.totalVehicleDistance > model.maxTripDistance) {
                totalExceededDistance += vehicle.totalVehicleDistance - model.maxTripDistance;
            }
            for (DayRoute dayRoute : vehicle.dayRouteMap.values()) {
                for (Integer maxRouteLoad : dayRoute.routeMaxLoad) {
                    if (maxRouteLoad > model.capacity) {
                        totalExceededLoad += maxRouteLoad - model.capacity;
                    }
                }
            }
        }
        long costPunishment = 10 * model.vehicleCost;
        // return totalExceededLoad *
        return costPunishment * (totalExceededLoad + totalExceededDistance);
    }

    @Override
    public void update() {
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i].update();
        }
    }

    @Override
    public Chromosome clone() {
        VehicleChromosome tmp = new VehicleChromosome(model, vehicles, requests);
        tmp.totalCost = totalCost;
        tmp.realCost = realCost;
        return tmp;
    }
}
