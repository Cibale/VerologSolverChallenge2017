package main.java;

import java.util.*;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle {

    public Map<Integer, DayRoute> dayRouteMap;
    //in this list are all requests from all days
    private List<Request> requestList;
    private Set<Integer> changedDays;
    public int totalVehicleDistance;
    public int totalExceededLoad;
    private ProblemModel model;

    public Vehicle(ProblemModel model) {
        this.requestList = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.dayRouteMap = new HashMap<>();
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.model = model;
    }

    public Vehicle(Vehicle vehicle) {
        this.dayRouteMap = new HashMap<>(vehicle.dayRouteMap);
        this.requestList = new ArrayList<>(vehicle.requestList);
        this.changedDays = new HashSet<>();
        this.totalVehicleDistance = vehicle.totalVehicleDistance;
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
        this.model = vehicle.model;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void addRequest(Request newRequest) {
        DayRoute dayRoute = dayRouteMap.get(newRequest.pickedDayForDelivery);
        if (dayRoute == null) {
            dayRoute = new DayRoute(this.model);
            dayRouteMap.put(newRequest.pickedDayForDelivery, dayRoute);
        }
        changedDays.add(newRequest.pickedDayForDelivery);
        dayRoute.add(newRequest);
        requestList.add(newRequest);
    }

    public void removeRequest(Request request) {
        DayRoute dayRoute = dayRouteMap.get(request.pickedDayForDelivery);
        changedDays.add(request.pickedDayForDelivery);
        dayRoute.remove(request);
        if (dayRoute.requests.size() == 0) {
            dayRouteMap.remove(request.pickedDayForDelivery);
        }
        requestList.remove(request);
    }

    public boolean usedVehicle() {
        return requestList.size() != 0;
    }

    /**
     * Updates only dayRoute that have changes in their requests. Updates totalVehicleDistance and totalExceededLoad.
     *
     */
    public void updateDayRoutes() {
        // optimize only changed days
        for (Integer day : changedDays) {
            DayRoute dayRoute = dayRouteMap.get(day);
            //first delete old dayRoute values
            this.totalVehicleDistance -= dayRoute.totalDayRouteDistance;
            for (Integer load : dayRoute.routeMaxLoad){
                if (load > model.capacity){
                    this.totalExceededLoad -= load - model.capacity;
                }
            }

            optimizeDayRoute(dayRoute);

            this.totalVehicleDistance += dayRoute.totalDayRouteDistance;
            for (Integer load : dayRoute.routeMaxLoad){
                if (load > model.capacity){
                    this.totalExceededLoad += load - model.capacity;
                }
            }

        }

    }

    /**
     * So far only creates one route
     * @param dayRoute
     */
    private void optimizeDayRoute(DayRoute dayRoute){
        dayRoute.routes.clear();
        List<Request> route = new ArrayList<>();
        dayRoute.routes.add(route);
        for (Request request : dayRoute.requests){
            route.add(request);
        }
        dayRoute.update();

    }


}
