package main.java;

import main.java.genetic_algorithm.Chromosome;

import java.util.*;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle {
    public int id;
    public Map<Integer, DayRoute> dayRouteMap;
    //in this list are all requests from all days
    private List<Integer> requestListIds;
    private Set<Integer> changedDays;
    public int totalVehicleDistance;
    public int totalExceededLoad;
    private Chromosome chromosome;


    public Vehicle(int id, Chromosome chromosome) {
        this.id = id;
        this.requestListIds = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.dayRouteMap = new HashMap<>();
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.chromosome = chromosome;
    }

    public Vehicle(Vehicle vehicle, Chromosome chromosome) {
        this.dayRouteMap = new HashMap<>();
        this.requestListIds = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.totalVehicleDistance = vehicle.totalVehicleDistance;
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.id = vehicle.id;
        this.chromosome = chromosome;
        for (Integer requestId : vehicle.requestListIds){
            addRequest(requestId);
        }
    }


    public void addRequest(Integer newRequestId) {
        Request newRequest = chromosome.requests[newRequestId];
        DayRoute dayRoute = dayRouteMap.get(newRequest.pickedDayForDelivery);
        if (dayRoute == null) {
            dayRoute = new DayRoute(chromosome.model);
            dayRouteMap.put(newRequest.pickedDayForDelivery, dayRoute);
        }
        changedDays.add(newRequest.pickedDayForDelivery);
        dayRoute.add(newRequest);
        requestListIds.add(newRequestId);
    }

    public void removeRequest(Integer requestId) {
        Request request = chromosome.requests[requestId];
        DayRoute dayRoute = dayRouteMap.get(request.pickedDayForDelivery);
        if (dayRoute == null){
            System.err.print("Null route has request?!");
        }
        changedDays.add(request.pickedDayForDelivery);
        dayRoute.remove(request);
        requestListIds.remove(request);
    }

    public boolean usedVehicle() {
        return requestListIds.size() != 0;
    }

    /**
     * Updates only dayRoute that have changes in their requests.
     * Updates totalVehicleDistance and totalExceededLoad.
     */
    public void updateDayRoutes() {
        // optimize only changed days
        for (Integer day : changedDays) {
            DayRoute dayRoute = dayRouteMap.get(day);
            //first delete old dayRoute values

            this.totalVehicleDistance -= dayRoute.totalDayRouteDistance;
            for (Integer load : dayRoute.routeMaxLoad) {
                if (load > chromosome.model.capacity) {
                    this.totalExceededLoad -= load - chromosome.model.capacity;
                }
            }

            //if day route is empty then we first have to substract its distance and load
            if(dayRoute.requests.size() == 0){
                dayRouteMap.remove(day);
                continue;
            }


            optimizeDayRoute(dayRoute);

            this.totalVehicleDistance += dayRoute.totalDayRouteDistance;
            for (Integer load : dayRoute.routeMaxLoad) {
                if (load > chromosome.model.capacity) {
                    this.totalExceededLoad += load - chromosome.model.capacity;

                }
            }

        }

    }


    /**
     * So far only creates one route
     *
     * @param dayRoute
     */
    private void optimizeDayRoute(DayRoute dayRoute) {
        dayRoute.routes.clear();
        List<Request> route = new ArrayList<>();
        dayRoute.routes.add(route);
        for (Request request : dayRoute.requests) {
            route.add(request);
        }
        dayRoute.update();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vehicle vehicle = (Vehicle) o;

        return id == vehicle.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
