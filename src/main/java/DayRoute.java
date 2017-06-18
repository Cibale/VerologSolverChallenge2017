package main.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by felentovic on 17.06.17..
 */
public class DayRoute {
    public List<Request> requests;
    public List<List<Request>> routes;
    public int totalRouteDistance;
    /**
     * stores max loads for each route
     */
    public List<Integer> routeMaxLoad;

    public DayRoute(){
        this.requests = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.totalRouteDistance = 0;
    }

    public void add(Request request){
        requests.add(request);
    }

    public void remove(Request request){
        requests.remove(request);
    }

    public void update(){
        this.totalRouteDistance = 0;
        for(int i = 0; i < requests.size() - 1; i++){
            this.totalRouteDistance += ProblemModel.distanceMatrix[requests.get(i).customerId][requests.get(i+1).customerId];
        }

        this.routeMaxLoad = new LinkedList<>();
        //for each route calculate maximal load
        for (List<Request> route : this.routes){
            int loadAtDepot = 0;
            for (Request request : route){
                if (!request.negativeRequest){
                    loadAtDepot += request.numOfTools * ProblemModel.tools[request.toolId].size;
                }
            }
            int maxLoad = loadAtDepot;
            int currentLoad = loadAtDepot;
            for (Request request : route){
                if(request.negativeRequest){
                    currentLoad += request.numOfTools * ProblemModel.tools[request.toolId].size;
                }else {
                    currentLoad -= request.numOfTools * ProblemModel.tools[request.toolId].size;
                }
                if (currentLoad > maxLoad){
                    maxLoad = currentLoad;
                }
            }
            this.routeMaxLoad.add(maxLoad);
        }
    }
}