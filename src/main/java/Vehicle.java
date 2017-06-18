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

    public Vehicle() {
        this.requestList = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.dayRouteMap = new HashMap<>();
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
    }

    public Vehicle(Vehicle vehicle) {
        this.dayRouteMap = new HashMap<>(vehicle.dayRouteMap);
        this.requestList = new ArrayList<>(vehicle.requestList);
        this.changedDays = new HashSet<>();
        this.totalVehicleDistance = vehicle.totalVehicleDistance;
        this.totalVehicleDistance = 0;
        this.totalExceededLoad = 0;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void addRequest(Request newRequest) {
        DayRoute dayRoute = dayRouteMap.get(newRequest.pickedDayForDelivery);
        if (dayRoute == null) {
            dayRoute = new DayRoute();
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
     * Optimizes day route in one day(several visits to depot is possible).This method updates vehicle traveled totalVehicleDistance and maxLoad.
     * Optimizes day route in a way that can create more visits to depot in one day. Each route starts and ends with depot so it can create more routes in one day.
     */
    public void optimizeDayRoute() {
        // optimize only changed days
        for (Integer day : changedDays) {
            DayRoute dayRoute = dayRouteMap.get(day);
            //first delete old dayRoute values
            this.totalVehicleDistance -= dayRoute.oldRouteDistance;
            for (Integer load : dayRoute.oldRouteMaxLoads){
                if (load > ProblemModel.capacity){
                    this.totalExceededLoad -= load - ProblemModel.capacity;
                }
            }

            dayRoute.routes.clear();
            //TODO: Optimize dayRoute, currently only one route
            List<Request> route = new ArrayList<>();
            dayRoute.routes.add(route);
            for (Request request : dayRoute.requests){
                route.add(request);
            }
            dayRoute.update();
            this.totalVehicleDistance += dayRoute.oldRouteDistance;
            for (Integer load : dayRoute.oldRouteMaxLoads){
                if (load > ProblemModel.capacity){
                    this.totalExceededLoad += load - ProblemModel.capacity;
                }
            }

        }

    }


}
