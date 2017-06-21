package main.java;

import main.java.genetic_algorithm.Chromosome;

import java.util.*;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle {
    public int id;
    public Map<Integer, DayRoute> dayRouteMap;
    //in this list are all requests from all daysMap
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
        for (Integer requestId : vehicle.requestListIds) {
            addRequest(requestId);
            chromosome.requests[requestId].correspondingVehicleId = this.id;
        }
        this.update();

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
        // optimize only changed daysMap
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
            if (dayRoute.requests.size() == 0) {
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
            if (dayRoute.routes.size() == 0) {
                dayRoute.routes.add(new ArrayList<>());
            }
            List<Request> route = dayRoute.routes.get(dayRoute.routes.size() - 1);
            route.add(request);
            dayRoute.update();
            if (dayRoute.totalDayRouteDistance > chromosome.model.maxTripDistance) {
                optimizeRouteDistance(dayRoute);
            }
            dayRoute.update();
            if (dayRoute.routeMaxLoad.get(dayRoute.routes.size() - 1) > chromosome.model.capacity) {
                optimizeRouteCapacity(route);
            }
            dayRoute.update();
            if (dayRoute.routeMaxLoad.get(dayRoute.routes.size() - 1) > chromosome.model.capacity) {
                route.remove(request);
                List<Request> newRoute = new ArrayList<>();
                newRoute.add(request);
                dayRoute.routes.add(newRoute);
                dayRoute.update();
            }
        }
        dayRoute.update();

    }

    private void optimizeRouteDistance(DayRoute dayRoute) {
        for (int i = 0; i < dayRoute.routes.size(); i++) {
            List<Request> smallRoute = dayRoute.routes.get(i);
            Request closestRequest = null;
            // find closest to depot
            for (Request request : smallRoute) {
                if (closestRequest == null) {
                    closestRequest = request;
                    continue;
                }
                if (chromosome.model.distanceMatrix[0][request.customerId] <
                        chromosome.model.distanceMatrix[0][closestRequest.customerId]) {
                    closestRequest = request;
                }
            }
            int indexOfClosestRequest = smallRoute.indexOf(closestRequest);
            if (indexOfClosestRequest == -1) {
                System.err.print("Error Vehicle.java 165.line");
            }
            swap(0, indexOfClosestRequest, smallRoute);
            // rest of list
            for (int oldIndex = 0; oldIndex < smallRoute.size() - 1; oldIndex++) {
                for (int j = oldIndex + 1; j < smallRoute.size(); j++) {
                    if (chromosome.model.distanceMatrix[oldIndex][smallRoute.get(j).customerId] <
                            chromosome.model.distanceMatrix[oldIndex][smallRoute.get(oldIndex + 1).customerId]) {
                        swap(oldIndex + 1, j, smallRoute);
                    }
                }
            }
        }
    }

    /**
     * Set negative requests before positive.
     */
    private void optimizeRouteCapacity(List<Request> route) {
        for (int i = 0; i < route.size(); i++) {
            for (int j = i + 1; j < route.size(); j++) {
                Request request1 = route.get(i);
                Request request2 = route.get(j);
                if (request2.negativeRequest) {
                    if (request2.negativeId == request1.id) {
                        swap(i, j, route);
                    }
                }
            }
        }
    }

    private void swap(int index1, int index2, List<Request> route) {
        Request tmp = route.get(index1);
        route.set(index1, route.get(index2));
        route.set(index2, tmp);
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
