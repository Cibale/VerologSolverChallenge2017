package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmatak on 6/15/17.
 */
public class Solution {
    public String dataset;
    public String name;

    public int numOfVehicles;
    public int numOfVehicleDays;
    // index is id of tool, value is how many of those tools are used
    public int[] toolUse;
    public int distance;
    public int cost;
    public List<Day> days = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (Day day : days) {
            buff.append(day.toString());
            buff.append("\n");
        }
        buff.deleteCharAt(buff.length() - 1);
        return buff.toString();
    }
}
