package main.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by felentovic on 17.06.17..
 * Each vehicle for each day has its DayRoute object. DayRoute has l,
 * it has routes which is
 */
public class DayRoute {
    /**
     * list of requests that has to be done that day
     */
    public List<Request> requests;
    /**
     * list of requests separated into little routes ( because vehicle can visit depot more times during
     * the day). Depot is not explicitly stored in route.
     */
    public List<List<Request>> routes;
    /**
     *  Max loads at any time for each route. Connected with routes list by the same index
     */
    public List<Integer> routeMaxLoad;
    /**
     * total distance in whole day
     */
    public int totalDayRouteDistance;
    private ProblemModel model;

    public DayRoute(ProblemModel model){
        this.requests = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.routeMaxLoad = new LinkedList<>();
        this.totalDayRouteDistance = 0;
        this.model = model;
    }

    public void add(Request request){
        requests.add(request);
    }

    public void remove(Request request){
        requests.remove(request);
    }

    /**
     * Updates totalDayRouteDistance and routeMaxLoad. Call after changing requests, or creating routes.
     */
    public void update(){
        this.totalDayRouteDistance = 0;

        this.routeMaxLoad = new LinkedList<>();
        //for each route calculate maximal load and distance travelled
        for (List<Request> route : this.routes){
            //add distance from the depot to frist customer
            this.totalDayRouteDistance += model.distanceMatrix[model.depotCoordinateId][route.get(0).customerId];
            //calculate load at depot needed for execution of the route
            int loadAtDepot = 0;
            for (int i = 0; i < route.size(); i++){
                Request req = route.get(i);
                if (!req.negativeRequest){
                    loadAtDepot += req.numOfTools * model.tools[req.toolId].size;
                }
                //update distance
                if (i < route.size() -1) {
                    totalDayRouteDistance += model.distanceMatrix[route.get(i).customerId][route.get(i + 1).customerId];
                }

            }
            //add distance from the last customer to depot
            this.totalDayRouteDistance += model.distanceMatrix[route.get(route.size() - 1).customerId][model.depotCoordinateId];

            //simulate route execution to find max load, vehicle unloads at positive request and loads at negative request
            int maxLoad = loadAtDepot;
            int currentLoad = loadAtDepot;
            for (Request request : route){
                if(request.negativeRequest){
                    currentLoad += request.numOfTools * model.tools[request.toolId].size;
                }else {
                    currentLoad -= request.numOfTools * model.tools[request.toolId].size;
                }
                if (currentLoad > maxLoad){
                    maxLoad = currentLoad;
                }
            }
            this.routeMaxLoad.add(maxLoad);
        }


    }
}
