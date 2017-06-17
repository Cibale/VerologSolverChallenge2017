package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felentovic on 17.06.17..
 */
public class Route {
    public List<Request> requests;
    public int oldRouteDistance;

    public Route(){
        this.requests = new ArrayList<>();
        this.oldRouteDistance = 0;
    }

    public void add(Request request){
        requests.add(request);
    }

    public void remove(Request request){
        requests.remove(request);
    }

    public int calculateDistance(){
        int dist = 0;
        for(int i = 0; i < requests.size() - 1; i++){
            dist += ProblemModel.distanceMatrix[requests.get(i).customerId][requests.get(i+1).customerId];
        }
        return dist;
    }
}
