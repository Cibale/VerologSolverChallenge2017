package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;
import main.java.Tool;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 28/08/17.
 */
public class DaysChromosome extends Chromosome {
    private Map<Integer, Map<Integer, UsageToolRequests>> daysMap = new HashMap<>();
    private Set<ExceededToolId> exceededToolIdSet;
    private int sumExceeded = 0;
    public int[] toolUsed;

    public DaysChromosome(ProblemModel model) {
        super(model);
        this.daysMap = new HashMap<>();
        this.exceededToolIdSet = new TreeSet<>((a, b) -> this.compare(a, b));
    }

    public DaysChromosome(ProblemModel model, Request[] requests) {
        this(model);
        this.requests = new Request[requests.length];
        for (int i = 0; i < requests.length; i++) {
            this.requests[i] = new Request(requests[i]);
        }

    }

    public static Chromosome createNew(ProblemModel model) {
        return new DaysChromosome(model);
    }

    @Override
    public int getGenomeValue(int index) {
        return requests[index].pickedDayForDelivery;
    }

    @Override
    public void setGenomeValue(int index, int value) {
        detachRequest(requests[index]);
        assignRequestToDay(requests[index], value);
    }

    @Override
    public int getChromosomeLength() {
        return requests.length;
    }


    @Override
    public Chromosome mutateOnIndex(int index) {
        return null;
    }

    public Long evaluate() {
        long punishment = 0;
        sumExceeded = 0;
        exceededToolIdSet.clear();
        int[] toolsUsed = new int[model.tools.length];
        for (Map.Entry<Integer, Map<Integer, DaysChromosome.UsageToolRequests>> day_ToolUsedRequests : daysMap.entrySet()) {
            for (Map.Entry<Integer, DaysChromosome.UsageToolRequests> toolId_UsageToolClass : day_ToolUsedRequests.getValue().entrySet()) {
                Tool tool = model.tools[toolId_UsageToolClass.getKey()];
                if (toolsUsed[tool.id] < toolId_UsageToolClass.getValue().usage) {
                    toolsUsed[tool.id] = toolId_UsageToolClass.getValue().usage;
                }
                //punishment
                int exceeded = (toolId_UsageToolClass.getValue().usage - tool.availableNum);
                if (exceeded > 0) {
                    punishment += exceeded;//* tool.cost;
                    boolean bol = exceededToolIdSet.add(new ExceededToolId(exceeded, tool.id, day_ToolUsedRequests.getKey()));
                    exceededToolIdSet.add(new ExceededToolId(exceeded, tool.id, day_ToolUsedRequests.getKey()));
                    sumExceeded += exceeded;
                }
            }
        }
        long cost = 0;
        for (int i = 1; i < toolsUsed.length; i++) {
            cost += toolsUsed[i] * model.tools[i].cost;
        }
        realCost = cost;
        totalCost = cost + punishment;
        toolUsed = toolsUsed;
        return totalCost;
    }

    @Override
    public void update() {

    }

    @Override
    public Chromosome clone() {
        DaysChromosome tmp = new DaysChromosome(model, requests);
        for (Request request : requests) {
            tmp.assignRequestToDay(request, request.pickedDayForDelivery);
        }
        tmp.totalCost = totalCost;
        tmp.realCost = realCost;

        return tmp;
    }

    private void assignRequestToDay(Request request, Integer pickedDay) {
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

    private void detachRequest(Request request) {
        for (int i = request.pickedDayForDelivery; i <= request.pickedDayForDelivery + request.durationInDays; i++) {
            Map<Integer, UsageToolRequests> toolUsageMap = this.daysMap.get(i);
            UsageToolRequests usageToolRequests = toolUsageMap.get(request.toolId);

            usageToolRequests.usage -= request.numOfTools;
            usageToolRequests.requestList.remove(request);
            toolUsageMap.put(request.toolId, usageToolRequests);
        }
        request.pickedDayForDelivery = -1;
    }

    public ExceededToolId proportionalySelectExceedToolId() {
        if (sumExceeded == 0) {
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

    private Integer compare(ExceededToolId a, ExceededToolId b) {
        Comparator<ExceededToolId> comparator1 = (d, c) -> Integer.compare(d.execeeded, c.execeeded);
        Comparator<ExceededToolId> comparator2 = (d, c) -> Integer.compare(d.day, c.day);
        Comparator<ExceededToolId> comparator3 = (d, c) -> Integer.compare(d.toolId, c.toolId);
        List<Comparator<ExceededToolId>> comparators = new LinkedList<>();
        comparators.add(comparator1);
        comparators.add(comparator2);
        comparators.add(comparator3);

        for (Comparator<ExceededToolId> comparator : comparators) {
            if (comparator.compare(a, b) == 0) {
                continue;
            } else {
                return comparator.compare(a, b);
            }
        }
        return 0;
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
