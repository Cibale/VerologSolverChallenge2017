package main.java;

import main.java.output.Solution;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by mmatak on 6/15/17.
 */
public class Parser {

    public static void parseInput(String inputFile, ProblemModel model) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("DATASET")) {
                    model.dataset = line.split("=")[1].trim();
                    continue;
                }
                if (line.startsWith("NAME")) {
                    model.name = line.split("=")[1].trim();
                    continue;
                }
                if (line.startsWith("DAYS")) {
                    model.days = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("CAPACITY")) {
                    model.capacity = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("MAX_TRIP_DISTANCE")) {
                    model.maxTripDistance = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("DEPOT_COORDINATE")) {
                    model.depotCoordinateId = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("VEHICLE_COST")) {
                    model.vehicleCost = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("VEHICLE_DAY_COST")) {
                    model.vehicleDayCost = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("DISTANCE_COST")) {
                    model.distanceCost = Integer.parseInt(line.split("=")[1].trim());
                    continue;
                }
                if (line.startsWith("TOOLS")) {
                    int numOfTools = Integer.parseInt(line.split("=")[1].trim());
                    //because IDs of tools start from 1, not from 0 as array indexes
                    model.tools = new Tool[numOfTools + 1];
                    parseTools(numOfTools, model, br);
                    continue;
                }
                if (line.startsWith("COORDINATES")) {
                    int numOfCoordinates = Integer.parseInt(line.split("=")[1].trim());
                    //because IDs of coordinates start from 0, as array indexes do also
                    model.coordinates = new Point[numOfCoordinates];
                    parseCoordinates(numOfCoordinates, model, br);
                    continue;
                }
                if (line.startsWith("REQUESTS")) {
                    int numOfRequests = Integer.parseInt(line.split("=")[1].trim());
                    model.requests = new Request[numOfRequests];
                    parseRequests(numOfRequests, model, br);
                    continue;
                }
                if (line.equals("DISTANCE")) {
                    parseDistances(model, br);
                }
            }
        }
        if (model.distanceMatrix == null){
            createDistanceMatrix(model);
        }
    }

    private static void createDistanceMatrix(ProblemModel model) {
        int numOfCoordinates = model.coordinates.length;
        model.distanceMatrix = new int[numOfCoordinates][numOfCoordinates];
        for (int i = 0; i < numOfCoordinates; i++){
            for (int j = i; j < numOfCoordinates; j++){
                if (i == j){
                    model.distanceMatrix[i][j] = 0;
                    continue;
                }
                Point point1 = model.coordinates[i];
                Point point2 = model.coordinates[j];
                int distance = (int) euclidDistance(point1, point2);
                model.distanceMatrix[i][j] = distance;
            }
        }
        for (int i = 0; i < numOfCoordinates; i++){
            for (int j = 0; j < i; j++){
                model.distanceMatrix[i][j] = model.distanceMatrix[j][i];
            }
        }
    }

    private static double euclidDistance(Point point1, Point point2) {
        int firstClosure = point1.x  - point2.x;
        int secondClosure = point1.y - point2.y;
        return Math.sqrt(firstClosure*firstClosure + secondClosure*secondClosure);
    }


    private static void parseDistances(ProblemModel model, BufferedReader br) throws IOException {
        int numOfCoordinates = model.coordinates.length;
        model.distanceMatrix = new int[numOfCoordinates][numOfCoordinates];
        String line;
        for (int i = 0; i < numOfCoordinates; i++){
            line = br.readLine().trim();
            String[] chunks = line.split("\\t");
            for (int j = 0; j < numOfCoordinates; j++){
                model.distanceMatrix[i][j] = Integer.parseInt(chunks[j]);
            }
        }
    }

    private static void parseRequests(int numOfRequests, ProblemModel model, BufferedReader br) throws IOException {
        String line;
        for (int i = 0; i < numOfRequests; i++) {
            line = br.readLine().trim();
            String[] chunks = line.split("\\t");
            // because it starts from 1 in input
            int id = Integer.parseInt(chunks[0]) - 1;
            int depotId = Integer.parseInt(chunks[1]);
            int firstDay = Integer.parseInt(chunks[2]);
            int lastDay = Integer.parseInt(chunks[3]);
            int duration = Integer.parseInt(chunks[4]);
            int toolId = Integer.parseInt(chunks[5]);
            int numOfTools = Integer.parseInt(chunks[6]);
            model.requests[id] = new Request(id, depotId, firstDay, lastDay, duration, toolId, numOfTools, false);
        }
    }

    private static void parseCoordinates(int numOfCoordinates, ProblemModel model, BufferedReader br) throws IOException {
        String line;
        for (int i = 0; i < numOfCoordinates; i++) {
            line = br.readLine().trim();
            String[] chunks = line.split("\\t");
            int id = Integer.parseInt(chunks[0]);
            int x = Integer.parseInt(chunks[1]);
            int y = Integer.parseInt(chunks[2]);
            model.coordinates[id] = new Point(x, y);
        }
    }

    private static void parseTools(int numOfTools, ProblemModel model, BufferedReader br) throws IOException {
        String line;
        for (int i = 0; i < numOfTools; i++) {
            line = br.readLine().trim();
            String[] chunks = line.split("\\t");
            int id = Integer.parseInt(chunks[0]);
            int size = Integer.parseInt(chunks[1]);
            int count = Integer.parseInt(chunks[2]);
            int cost = Integer.parseInt(chunks[3]);
            model.tools[id] = new Tool(id, size, count, cost);
        }
    }

    public static void writeOutput(Path output, Solution bestSolution) throws IOException {
        Files.write(output,bestSolution.toString().getBytes(), StandardOpenOption.CREATE);
    }
}
