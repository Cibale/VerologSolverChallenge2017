package main.java.genetic_algorithm;

import main.java.Request;
import main.java.Vehicle;

import java.util.Arrays;

/**
 * Created by mmatak on 6/16/17.
 */
public class Chromosome {
    public Vehicle[] vehicles;
    public Request[] requests;
    public int partlyCost;

    public Chromosome(Vehicle[] vehicles, Request[] requests) {
        this.vehicles = Arrays.copyOf(vehicles,vehicles.length);
        this.requests = Arrays.copyOf(requests, requests.length);
    }
}
