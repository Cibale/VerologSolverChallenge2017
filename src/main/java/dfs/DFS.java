package main.java.dfs;

import main.java.Engine;
import main.java.Request;

import java.util.*;

/**
 * Created by mmatak on 6/20/17.
 */
public class DFS {
    // key - toolId, value - list of requests
    Map<Integer, List<Request>> positiveRequests;
    Map<Request, Integer> indexes;
    Engine engine;
    Map<Integer, Map<Integer, Integer>> dayToolCount = new HashMap<>();

    public DFS(Engine engine, List<Request> positiveRequests) {
        this.engine = engine;
        this.positiveRequests = new HashMap<>();
        positiveRequests.sort((o1, o2) -> {
            if (Integer.compare(o1.id, o2.id) != 0) {
                return Integer.compare(o1.id, o2.id);
            }
            int o1TimeWindow = o1.lastDayForDelivery - o1.firstDayForDelivery;
            int o2TimeWindow = o2.lastDayForDelivery - o2.firstDayForDelivery;
            int compareResult = Integer.compare(o1TimeWindow, o2TimeWindow);
            if (compareResult == 0) {
                return -1 * Integer.compare(o1.numOfTools, o2.numOfTools);
            } else {
                return compareResult;
            }
        });
        indexes = new HashMap<>();
        for (Request request : positiveRequests) {
            List<Request> requests = this.positiveRequests.computeIfAbsent(request.toolId, k -> new LinkedList<>());
            indexes.put(request, requests.size());
            requests.add(request);
        }
    }

    public Request getNextRequest(Request request) {
        if (indexes.get(request) == positiveRequests.get(request.toolId).size() - 1) {
            return null;
        }
        return positiveRequests.get(request.toolId).get(indexes.get(request) + 1);
    }

    public List<Request> run() {
        List<Node> goodBranch = new LinkedList<>();
        for(List<Request> requests : positiveRequests.values()){
            Request request = requests.get(0);
            Node root;
            for (int i = request.firstDayForDelivery; i <= request.lastDayForDelivery; i++) {
                request.pickedDayForDelivery = i;
                if (engine.toolUsage(dayToolCount, request, 1)) {
                    root = new Node(null, this, request, i);
                    if (startDFS(root, 1)) {
                        goodBranch.addAll(root.getPickedBranch());
                        break;
                    } else {
                        engine.deleteToolUsage(dayToolCount, request);
                    }
                }
            }
            System.out.println("Finished for tool with id " + request.toolId);
        }
        return constructSolution(goodBranch);
    }

    /**
     * Root at start of list.
     */
    private List<Request> constructSolution(List<Node> branch) {
        if (branch == null) {
            return null;
        }
        List<Request> requests = new ArrayList<>();
        for (Node node : branch) {
            Request request = node.request;
            request.pickedDayForDelivery = node.pickedDay;
            requests.add(request);
        }
        return requests;
    }

    private boolean startDFS(Node node, int level) {
        List<Node> children = node.generateChildren(dayToolCount);
        //System.out.println("Current request: " + node.request.id);
        //System.out.println("level: " + level + " / "+ this.positiveRequests.get(node.request.toolId).size());
        if (children == null) {
            if (!engine.toolUsage(dayToolCount, node.request, 1)) {
                return false;
            }
            // check once again if solution is feasible - it should be
            List<Request> solution = node.constructSolution();
            return engine.feasibleDayAssignment(solution);
        } else {
            Request request = node.request;
            request.pickedDayForDelivery = node.pickedDay;
            //double percentage = Math.max(level*1.0 / engine.model.requests.length, 0.95);
            double percentage = 1.0;
            //System.out.println("level : " + level + ", percentage: " + percentage);
            if (engine.toolUsage(dayToolCount, request, percentage)) {
                for (Node child : children) {
                    if (startDFS(child, level + 1)) {
                        node.pickedChild = child;
                        return true;
                    }
                }
                engine.deleteToolUsage(dayToolCount, request);
            }
            return false;
        }
    }
}
