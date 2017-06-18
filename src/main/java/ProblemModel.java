package main.java;

import java.awt.*;

/**
 * Created by mmatak on 6/15/17.
 */
public class ProblemModel {
    public String dataset;
    public String name;

    public int days;
    public static int capacity;
    public int maxTripDistance;
    public int depotCoordinateId;

    public int vehicleCost;
    public int vehicleDayCost;
    public int distanceCost;

    // id is index of array
    public Tool[] tools;
    public Point[] coordinates;
    public Request[] requests;
    // requests for picking up tools
    public Request[] negativeRequests;

    public static int[][] distanceMatrix;
}
