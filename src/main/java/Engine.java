package main.java;

import main.java.dfs.DFS;
import main.java.genetic_algorithm.DaysChromosome;
import main.java.genetic_algorithm.GA;
import main.java.genetic_algorithm.VehicleChromosome;
import main.java.output.Solution;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Created by mmatak on 6/15/17.
 */
public class Engine {
    public ProblemModel model;
    public Solution bestSolution;
    public Random rand = new Random();

    public Engine(ProblemModel model) {
        this.model = model;
    }

    public void decideDays() {
        // random method just to start with
        for (int i = 0; i < model.requests.length; i++) {
            Request request = model.requests[i];
            request.pickedDayForDelivery = request.firstDayForDelivery +
                    rand.nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery);
        }
        createNegativeRequests();
    }

    public void decideDaysDFS() {
        DFS dfs = new DFS(this, Arrays.asList(model.requests));
        long tStart = System.currentTimeMillis();
        List<Request> requests = dfs.run();
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedMinutes = tDelta / 1000.0 / 60;
        System.out.println("Minutes needed: " + elapsedMinutes);
        requests.sort(Comparator.comparingInt(o -> o.id));
        for (int i = 0; i < model.requests.length; i++) {
            model.requests[i] = new Request(requests.get(i));
        }
        try {
            FileOutputStream out = new FileOutputStream("secondInstance.out");
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(requests);
            oos.flush();
        } catch (Exception e) {
            System.out.println("Problem serializing: " + e);
        }
        createNegativeRequests();
    }

    /**
     * Returns true if this day assignment for requests is feasible (no too much usage of some tool)
     */
    public boolean feasibleDayAssignment(List<Request> requests) {
        Map<Integer, Map<Integer, Integer>> days = new HashMap<>();
        //System.out.println("Testing solution: " + requests.hashCode());
        for (Request request : requests) {
            for (int j = request.pickedDayForDelivery; j < request.pickedDayForDelivery + request.durationInDays; j++) {
                Map<Integer, Integer> day = days.computeIfAbsent(j, k -> new HashMap<>());
                Integer numOfTools = day.getOrDefault(request.toolId, 0);
                numOfTools += request.numOfTools;
                if (numOfTools > model.tools[request.toolId].availableNum) {
                    System.out.println("Too much of tool " + request.toolId + "(" + numOfTools + ")");
                    //System.out.println(numOfTools);
                    return false;
                }
                day.put(request.toolId, numOfTools);
            }
        }

        return true;
    }

    public void decideDaysGreedy() {
        Map<Integer, Map<Integer, Integer>> days = new HashMap<>();
        for (Request request : model.requests) {
            //for every request look in days how many tools of that ids is in use that day
            int minNumOfToolsDay = -1;
            int minDay = -1;
            // find minDay - day with minimum tools of that kind
            for (int i = request.firstDayForDelivery; i <= request.lastDayForDelivery; i++) {
                //map (toolId, numOfTool)
                Map<Integer, Integer> day = days.get(i);
                if (day == null) {
                    day = new HashMap<>();
                    days.put(i, day);
                }
                Integer numOfTools = day.getOrDefault(request.toolId, 0);
                if (minNumOfToolsDay == -1 || minNumOfToolsDay > numOfTools) {
                    minNumOfToolsDay = numOfTools;
                    minDay = i;
                }
            }
            request.pickedDayForDelivery = minDay;
            for (int j = request.pickedDayForDelivery; j < request.pickedDayForDelivery + request.durationInDays; j++) {
                Map<Integer, Integer> day = days.get(j);
                if (day == null) {
                    day = new HashMap<>();
                    days.put(j, day);
                }
                Integer numOfTools = day.getOrDefault(request.toolId, 0);
                numOfTools += request.numOfTools;
                day.put(request.toolId, numOfTools);
            }


        }
        createNegativeRequests();
    }

    /**
     * Once when day of delivery of all requests is decided,
     * we also need to create negative requests,
     * i.e. requests for picking up those tools from customers.
     */
    private void createNegativeRequests() {
        model.negativeRequests = new Request[model.requests.length];
        for (int i = 0; i < model.negativeRequests.length; i++) {
            model.negativeRequests[i] = model.requests[i].getNegativeRequest();
            model.negativeRequests[i].id = model.requests[i].id + model.requests.length;
        }
    }

    /**
     * Returns true or false if tool for request is used too much.
     * If it is used too much, map stays untouched!
     *
     * @param dayToolCount map to fill
     * @param request      request with tool
     */
    public boolean toolUsage(Map<Integer, Map<Integer, Integer>> dayToolCount, Request request, double percentage) {
        for (int j = request.pickedDayForDelivery; j <= request.pickedDayForDelivery + request.durationInDays; j++) {
            Map<Integer, Integer> day = dayToolCount.computeIfAbsent(j, k -> new HashMap<>());
            Integer numOfTools = day.getOrDefault(request.toolId, 0);
            numOfTools += request.numOfTools;
            day.put(request.toolId, numOfTools);
            if (Double.compare(numOfTools, percentage * model.tools[request.toolId].availableNum) > 0) {
                //System.out.println("Too much of tool " + request.toolId + "(" + numOfTools + ")");
                for (int k = j; k >= request.pickedDayForDelivery; k--) {
                    day = dayToolCount.get(k);
                    day.put(request.toolId, day.get(request.toolId) - request.numOfTools);
                }
                return false;
            }
        }
        return true;
    }


    public long decideDaysGA() {
        GA ga = new GA(model, () -> DaysChromosome.createNew(model), 100, 3);
        ga.NUMBER_OF_GENERATIONS = 500;
        ga.CROSSOVER_PROBABILITY = 0.85;
        ga.MUTATION_PROBABILITY = 0.20;
        ga.start();
        model.requests = ga.bestSolution.requests;
        createNegativeRequests();
        return ga.bestSolution.totalCost;
    }

    public long assignRequestsToVehicles() {
        GA ga = new GA(model, () -> VehicleChromosome.createNew(model), 100, 3);
        ga.NUMBER_OF_GENERATIONS = 500;
        ga.CROSSOVER_PROBABILITY = 0.8;
        ga.MUTATION_PROBABILITY = 0.3;
        ga.start();
        bestSolution = new Solution(model);
        bestSolution.constructFrom((VehicleChromosome)ga.bestSolution);
        return bestSolution.cost;
    }

    public void deleteToolUsage(Map<Integer, Map<Integer, Integer>> dayToolCount, Request request) {
        for (int j = request.pickedDayForDelivery; j <= request.pickedDayForDelivery + request.durationInDays; j++) {
            Map<Integer, Integer> day;
            day = dayToolCount.get(j);
            day.put(request.toolId, day.get(request.toolId) - request.numOfTools);

        }
    }
}
