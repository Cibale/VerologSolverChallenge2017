package main.java;

import main.java.genetic_algorithm.GA;
import main.java.genetic_algorithm.GA_Days;
import main.java.output.Solution;

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
            for (int j = request.pickedDayForDelivery; j <= request.pickedDayForDelivery + request.durationInDays; j++) {
                Map<Integer, Integer> day = days.get(j);
                if (day == null) {
                    System.out.println("Creating hashmap for day " + j);
                    day = new HashMap<>();
                    days.put(j, day);
                }
                Integer numOfTools = day.getOrDefault(request.toolId, 0);
                numOfTools += request.numOfTools;
                day.put(request.toolId, numOfTools);
                if (request.toolId == 1){
                    System.out.println("Now tool 1 is used on these days [availableNum of tools]");
                    for (Integer dayTool1 : days.keySet()){
                        System.out.println("day " + dayTool1 + ", num of tool1: " + days.getOrDefault(dayTool1, new HashMap<>()).getOrDefault(1, 0));
                    }
                }
                System.out.println("Day " + j + ", now has " + numOfTools + " instances of tool kind " + request.toolId);
                System.out.println();
            }

            System.out.println("Finished with processing request " + request.id);

//            model.negativeRequests[request.id] = request.getNegativeRequest();
//            model.negativeRequests[request.id].id = request.id + model.requests.length;
//            Integer deliveryDay = model.negativeRequests[request.id].pickedDayForDelivery;
//            Map<Integer,Integer> day = days.get(deliveryDay);
//            if(day == null){
//                day = new HashMap<>();
//                days.put(deliveryDay,day);
//            }
//            Integer numOfTools = day.getOrDefault(request.toolId,0);
//            numOfTools-=request.numOfTools;
//            day.put(request.toolId, numOfTools);

        }
        createNegativeRequests();
        System.out.print("dd");
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

    public void decideDaysGA(){
        GA_Days ga = new GA_Days(model);
        ga.start();
        model.requests = ga.bestSolution.requests;
        createNegativeRequests();
    }

    public void run() {
        GA ga = new GA(model);
        ga.start();
        bestSolution = new Solution(model);
        bestSolution.constructFrom(ga.bestSolution);
    }

}
