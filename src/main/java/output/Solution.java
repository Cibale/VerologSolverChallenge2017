package main.java.output;

import main.java.ProblemModel;
import main.java.genetic_algorithm.Chromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mmatak on 6/15/17.
 */
public class Solution {
    public String dataset;
    public String name;

    public int maxNumOfVehicles;
    public int numOfVehicleDays;
    // index is id of tool, value is how many of those tools are used
    public int[] toolUse;
    public int distance;
    public int cost;
    public List<Day> days = new ArrayList<>();

    public Solution(ProblemModel model) {
        this.dataset = model.dataset;
        this.name = model.name;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("DATASET = ").append(dataset).append("\n");
        buff.append("NAME = ").append(name).append("\n");
        buff.append("\n");
        buff.append("MAX_NUMBER_OF_VEHICLES = ").append(maxNumOfVehicles).append("\n");
        buff.append("NUMBER_OF_VEHICLE_DAYS = ").append(numOfVehicleDays).append("\n");
        buff.append("TOOL_USE =");
        for (int i = 0; i < toolUse.length; i++) {
            buff.append(" ").append(toolUse[i]);
        }
        buff.append("\n");
        buff.append("DISTANCE = ").append(distance);
        buff.append("COST = ").append(cost);
        for (Day day : days) {
            buff.append(day.toString());
            buff.append("\n");
        }
        buff.deleteCharAt(buff.length() - 1);
        return buff.toString();
    }

    /**
     * Constructs solution from chromosme
     *
     * @param chromosome chromosme from which solution is constructed
     */
    public void constructFrom(Chromosome chromosome) {
        /*
        1. sort requests per days (lower day - lower index)
        2. for each day spent:
            2.1. check number of vehicles used that day (update this number as maxNumOfVehicles)
            2.2. create routes per vehicle (*you have this in vehicle class*)
            2.2. set depot values (based on previous day + routes that day)
            2.3. for each vehicle that day:
                2.3.1. write his route
                2.3.2. for each visit to depot:
                    2.3.3. write which tools he has taken / returned to depot
                    2.3.4. update how many which kind of tools is used
                2.3.3. write his DISTANCE COST for that day
        3. set cost based on chromosome cost without punishment
         */
        //1.
        Arrays.sort(chromosome.requests, Comparator.comparingInt(o -> o.pickedDayForDelivery));
        //2.
        //TODO
        //3.
        cost = chromosome.realCost;
    }
}
