package main.java;

import java.util.*;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle{

    public Map<Integer, Route> dayRouteMap;
    //in this list are all requests from all days
    private List<Request> requestList;
    private Set<Integer> changedDays;
    public int distance;

    public Vehicle(){
        this.requestList = new ArrayList<>();
        this.changedDays = new HashSet<>();
        this.dayRouteMap = new HashMap<>();
    }

    public Vehicle(Vehicle vehicle){
        this.dayRouteMap = new HashMap<>(vehicle.dayRouteMap);
        this.requestList = new ArrayList<>(vehicle.requestList);
        this.changedDays = new HashSet<>();
        this.distance = vehicle.distance;
    }

    public List<Request> getRequestList(){
        return requestList;
    }

    public void addRequest(Request newRequest){
        Route route = dayRouteMap.get(newRequest.pickedDayForDelivery);
        if (route == null){
            route = new Route();
            dayRouteMap.put(newRequest.pickedDayForDelivery, route);
        }
        changedDays.add(newRequest.pickedDayForDelivery);
        route.add(newRequest);
        requestList.add(newRequest);
    }

    public void removeRequest(Request request){
        Route route = dayRouteMap.get(request.pickedDayForDelivery);
        changedDays.add(request.pickedDayForDelivery);
        route.remove(request);
        if (route.requests.size() == 0){
            dayRouteMap.remove(request.pickedDayForDelivery);
        }
        requestList.remove(request);
    }

    public boolean usedVehicle(){
        return requestList.size() != 0;
    }

    /**
     * This method updates vehicle traveled distance also
     */
    public void optimizeRoute(){
        //route can have more visits to depot to unload some stuff
        // optimize only changed days

        //no optimization only cost counting
        for(Integer day : changedDays){
            Route route = dayRouteMap.get(day);
            //first delete old route distance from total distance
            this.distance -= route.oldRouteDistance;

            //TODO: Optimize route

            route.oldRouteDistance = route.calculateDistance();
            this.distance += route.oldRouteDistance;

        }

    }



}
