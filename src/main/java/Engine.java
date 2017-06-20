package main.java;

import main.java.genetic_algorithm.Chromosome;
import main.java.genetic_algorithm.GA;
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

    public void decideDaysGreedy(){
        Map<Integer,Map<Integer,Integer>> days = new HashMap<>();
        model.negativeRequests = new Request[model.requests.length];
        for(Request request : model.requests){
            //for every request look in days how many tools of that ids is in use that day
            int minNumOfToolsDay = -1;
            int minDay = -1;
            for (int i = request.firstDayForDelivery ; i <= request.lastDayForDelivery; i++){
                //map (toolId, numOfTool)
                Map<Integer,Integer> day = days.get(i);
                if(day == null){
                    day = new HashMap<>();
                    days.put(i,day);
                }
                Integer numOfTools = day.getOrDefault(request.toolId,0);
                if(minNumOfToolsDay == -1 || minNumOfToolsDay > numOfTools){
                    minNumOfToolsDay = numOfTools;
                    minDay = i;
                }

            }
            request.pickedDayForDelivery = minDay;
            Map<Integer,Integer> day = days.get(minDay);
            Integer numOfTools = day.getOrDefault(request.toolId,0);
            numOfTools+=request.numOfTools;
            day.put(request.toolId, numOfTools);

            model.negativeRequests[request.id] = request.getNegativeRequest();
            model.negativeRequests[request.id].id = request.id + model.requests.length;
            Integer deliveryDay = model.negativeRequests[request.id].pickedDayForDelivery;
            day = days.get(deliveryDay);
            if(day == null){
                day = new HashMap<>();
                days.put(deliveryDay,day);
            }
            day.getOrDefault(request.toolId,0);
            numOfTools+=request.numOfTools;
            day.put(request.toolId, numOfTools);

        }
 //       createNegativeRequests();
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

    public void run() {
        GA ga = new GA(model);
        ga.start();
        bestSolution = new Solution(model);
        bestSolution.constructFrom(ga.bestSolution);
    }

}
