package main.java;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mmatak on 6/16/17.
 */
public class Vehicle {
    private List<Request> requestList = new LinkedList<>();
    public int distance;

    public void addRequest(Request newRequest){
        requestList.add(newRequest);
    }

    public void removeRequest(Request request){
        requestList.remove(request);
    }

    public boolean usedVehicle(){
        return requestList.size() != 0;
    }
    public void optimizeTSP(){
        //update distancCOst here
        return;
    }
}
