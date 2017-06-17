package main.java;

import java.util.*;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle {
    public Map<Integer, List<Request>> dayRequestsMap = new HashMap<>();
    private List<Request> requestList = new ArrayList<>();
    public int distance;

    public void addRequest(Request newRequest){
        List<Request> requestInDay = null;
        if (!dayRequestsMap.containsKey(newRequest.pickedDayForDelivery)){
            requestInDay = new ArrayList<>();
            dayRequestsMap.put(newRequest.pickedDayForDelivery, requestInDay);
        }else {
            requestInDay = dayRequestsMap.get(newRequest.pickedDayForDelivery);
        }
        requestInDay.add(newRequest);
        requestList.add(newRequest);
    }

    public void removeRequest(Request request){
        List<Request> requestsDay = dayRequestsMap.get(request.pickedDayForDelivery);
        requestsDay.remove(request);
        requestList.remove(request);
    }

    public boolean usedVehicle(){
        return requestList.size() != 0;
    }

    public void optimizeTSP(){
        //update distancCost here
        return;
    }
}
