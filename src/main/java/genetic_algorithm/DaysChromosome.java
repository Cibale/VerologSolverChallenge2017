package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysChromosome {
    public Request[] requests;
    // less is better
    public int totalCost;
    //cost without punishments
    public int realCost;
    public ProblemModel model;
    public Map<Integer, Map<Integer, Integer>> days = new HashMap<>();


    public DaysChromosome(DaysChromosome chromosomeToCopy) {
        this(chromosomeToCopy.model, chromosomeToCopy.requests);
        for(Request request: chromosomeToCopy.requests){
            assignRequestToDay(request, request.pickedDayForDelivery);
        }
        this.totalCost = chromosomeToCopy.totalCost;
        this.realCost = chromosomeToCopy.realCost;
    }

    public DaysChromosome(ProblemModel model, Request[] requests) {
        this.model = model;
        this.requests = new Request[requests.length];
        for (int i = 0; i < requests.length; i++) {
            this.requests[i] = new Request(requests[i]);
        }
        this.days = new HashMap<>();

    }

    public DaysChromosome(ProblemModel model) {
        this.model = model;
    }

    public void assignRequestToDay(Request request, Integer pickedDay){
        for (int i = pickedDay ; i <= pickedDay + request.durationInDays; i++) {
            Map<Integer, Integer> toolUsageMap = this.days.get(i);
            if (toolUsageMap == null) {
                toolUsageMap = new HashMap<>();
                days.put(i, toolUsageMap);
            }
            Integer usage = toolUsageMap.getOrDefault(request.toolId, 0);
            usage += request.numOfTools;
            toolUsageMap.put(request.toolId, usage);
        }
        request.pickedDayForDelivery = pickedDay;

    }

    public void detachRequest(Request request){
        for (int i = request.pickedDayForDelivery ; i <= request.pickedDayForDelivery + request.durationInDays; i++) {
            Map<Integer, Integer> toolUsageMap = this.days.get(i);
            Integer usage = toolUsageMap.get(request.toolId);
            usage -= request.numOfTools;
            toolUsageMap.put(request.toolId, usage);
        }
        request.pickedDayForDelivery = -1;
    }
    public void initialize() {
        requests = new Request[model.requests.length];
        int index = 0;
        for (int i = 0; i < model.requests.length; i++) {
            requests[index++] = new Request(model.requests[i]);
        }

        for (Request request : requests) {
            assignRequestToDay(request,request.firstDayForDelivery +
                    ThreadLocalRandom.current().nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery));

        }
    }
}
