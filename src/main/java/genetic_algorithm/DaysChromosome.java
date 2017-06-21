package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */

public class DaysChromosome {
    public Request[] requests;
    // less is better
    public long totalCost;
    //cost without punishments
    public long realCost;
    public ProblemModel model;
    public Map<Integer, Map<Integer, UsageToolRequests>> daysMap = new HashMap<>();
    public Set<ExceededToolId> exceededToolIdSet;
    public int sumExceeded = 0;
    public int[] toolUsed;

    public DaysChromosome(DaysChromosome chromosomeToCopy) {
        this(chromosomeToCopy.model, chromosomeToCopy.requests);
        for (Request request : chromosomeToCopy.requests) {
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
        this.daysMap = new HashMap<>();
        this.exceededToolIdSet = new TreeSet<>((a, b) -> this.compare(a,b));
    }

    private Integer compare(ExceededToolId a, ExceededToolId b){
        Comparator<ExceededToolId> comparator1 = (d,c) -> Integer.compare(d.execeeded,c.execeeded);
        Comparator<ExceededToolId> comparator2 = (d,c) -> Integer.compare(d.day,c.day);
        Comparator<ExceededToolId> comparator3 = (d,c) -> Integer.compare(d.toolId,c.toolId);
        List<Comparator<ExceededToolId>> comparators = new LinkedList<>();
        comparators.add(comparator1);
        comparators.add(comparator2);
        comparators.add(comparator3);

        for (Comparator<ExceededToolId> comparator : comparators){
            if(comparator.compare(a,b) == 0){
                continue;
            }else{
                return comparator.compare(a,b);
            }
        }
        return 0;
    }
    public DaysChromosome(ProblemModel model) {
        this(model, new Request[0]);
    }


    public ExceededToolId proportionalySelectExceedToolId() {
        if(sumExceeded == 0){
        }
        int rand = ThreadLocalRandom.current().nextInt(sumExceeded);
        int counter = 0;
        ExceededToolId tmp = null;
        for (ExceededToolId exceededToolId : exceededToolIdSet) {
            counter += exceededToolId.execeeded;
            if (rand <= counter) {
                tmp = exceededToolId;
                break;
            }
        }


        return tmp;
    }

    public void assignRequestToDay(Request request, Integer pickedDay) {
        for (int i = pickedDay; i <= pickedDay + request.durationInDays; i++) {
            Map<Integer, UsageToolRequests> toolUsageMap = this.daysMap.get(i);
            if (toolUsageMap == null) {
                toolUsageMap = new HashMap<>();
                daysMap.put(i, toolUsageMap);
            }
            UsageToolRequests usageToolRequests = toolUsageMap.getOrDefault(request.toolId, new UsageToolRequests());
            usageToolRequests.usage += request.numOfTools;
            usageToolRequests.requestList.add(request);
            toolUsageMap.put(request.toolId, usageToolRequests);

        }
        request.pickedDayForDelivery = pickedDay;

    }

    public void detachRequest(Request request) {
        for (int i = request.pickedDayForDelivery; i <= request.pickedDayForDelivery + request.durationInDays; i++) {
            Map<Integer, UsageToolRequests> toolUsageMap = this.daysMap.get(i);
            UsageToolRequests usageToolRequests = toolUsageMap.get(request.toolId);

            usageToolRequests.usage -= request.numOfTools;
            usageToolRequests.requestList.remove(request);
            toolUsageMap.put(request.toolId, usageToolRequests);
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
            assignRequestToDay(request, request.firstDayForDelivery +
                    ThreadLocalRandom.current().nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery));

        }
    }


    public class UsageToolRequests {
        public int usage;
        public List<Request> requestList = new LinkedList<>();
    }

    public class ExceededToolId {
        public int execeeded;
        public int toolId;
        public int day;

        public ExceededToolId(int execeeded, int toolId, int day) {
            this.execeeded = execeeded;
            this.toolId = toolId;
            this.day = day;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExceededToolId that = (ExceededToolId) o;

            if (execeeded != that.execeeded) return false;
            if (toolId != that.toolId) return false;
            return day == that.day;

        }

        @Override
        public int hashCode() {
            int result = execeeded;
            result = 31 * result + toolId;
            result = 31 * result + day;
            return result;
        }
    }
}
