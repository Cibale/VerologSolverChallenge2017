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
    private Chromosome chromosomeInDay;


    public Vehicle(int id, Chromosome chromosomeInDay) {
        this.id = id;
        this.requestListIds = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.dayRouteMap = new HashMap<>();
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.chromosomeInDay = chromosomeInDay;
    }

    public Vehicle(Vehicle vehicle, Chromosome chromosomeInDay) {
        this.dayRouteMap = new HashMap<>();
        this.requestListIds = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.totalVehicleDistance = vehicle.totalVehicleDistance;
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.id = vehicle.id;
        this.chromosomeInDay = chromosomeInDay;
        for (Integer requestId : vehicle.requestListIds){
            addRequest(requestId);
            chromosomeInDay.requests[requestId].correspondingVehicleId = this.id;
        }
        this.update();

    }


    public void addRequest(Integer newRequestId) {
        Request newRequest = chromosomeInDay.requests[newRequestId];

        DayRoute dayRoute = dayRouteMap.get(newRequest.pickedDayForDelivery);
        if (dayRoute == null) {
            dayRoute = new DayRoute(chromosomeInDay.model);
            dayRouteMap.put(newRequest.pickedDayForDelivery, dayRoute);
        }
        changedDays.add(newRequest.pickedDayForDelivery);
        dayRoute.add(newRequest);
        requestListIds.add(newRequestId);
    }

    public void removeRequest(Integer requestId) {
        Request request = chromosomeInDay.requests[requestId];
        DayRoute dayRoute = dayRouteMap.get(request.pickedDayForDelivery);
        changedDays.add(request.pickedDayForDelivery);
        dayRoute.remove(request);
        requestListIds.remove(requestId);
    }

    public boolean usedVehicle() {
        return requestListIds.size() != 0;
    }

    /**
     * Updates only dayRoute that have changes in their requests.
     * Updates totalVehicleDistance and totalExceededLoad.
     */
    public void update() {
        // optimize only changed days
        for (Integer day : changedDays) {
            DayRoute dayRoute = dayRouteMap.get(day);
            //first delete old dayRoute values

            this.totalVehicleDistance -= dayRoute.totalDayRouteDistance;
            for (Integer load : dayRoute.routeMaxLoad) {
                if (load > chromosomeInDay.model.capacity) {
                    this.totalExceededLoad -= load - chromosomeInDay.model.capacity;
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
                if (load > chromosomeInDay.model.capacity) {
                    this.totalExceededLoad += load - chromosomeInDay.model.capacity;

                }
            }

        }
        changedDays.clear();

    }


    /**
     * So far only creates one route
     *
     * @param dayRoute
     */
    private void optimizeDayRoute(DayRoute dayRoute) {
        dayRoute.routes.clear();
        for (Request request : dayRoute.requests) {
            List<Request> route = new ArrayList<>();
            dayRoute.routes.add(route);
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
