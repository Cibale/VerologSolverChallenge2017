package main.java.dfs;

import main.java.ProblemModel;
import main.java.Request;

import java.util.*;

/**
 * Created by mmatak on 6/20/17.
 */
public class Node {
    Node parent;
    DFS dfs;
    Request request;
    int pickedDay;
    public Node pickedChild;

    public Node(Node parent, DFS dfs, Request request, int pickedDay) {
        this.parent = parent;
        this.dfs = dfs;
        this.request = request;
        this.pickedDay = pickedDay;
    }

    List<Node> generateChildren(Map<Integer, Map<Integer, Integer>> dayToolCount) {

        List<Node> children = new ArrayList<>();
        Request nextRequest = dfs.getNextRequest(request);
        if (nextRequest == null) {
            return null;
        }
        for (int i = nextRequest.firstDayForDelivery; i <= nextRequest.lastDayForDelivery; i++) {
            children.add(new Node(this, dfs, nextRequest, i));
        }
        children.sort(Comparator.comparingInt(o -> dayToolCount.getOrDefault(o.pickedDay, new HashMap<>()).getOrDefault(o.request.toolId, 0)));
        return children;
    }

    /**
     * First index - root, last index - leaf.
     */
    public List<Node> getPickedBranch() {
        List<Node> branchHead = new LinkedList<>();
        branchHead.add(this);
        // this is not leaf
        if (pickedChild != null) {
            List<Node> restOfBranch = pickedChild.getPickedBranch();
            branchHead.addAll(restOfBranch);
        }
        return branchHead;
    }

    /**
     * This is leaf node. Return whole branch.
     */
    public List<Request> constructSolution() {
        List<Request> solution = new LinkedList<>();
        Node currentNode = this;
        do {
            currentNode.request.pickedDayForDelivery = currentNode.pickedDay;
            solution.add(0, currentNode.request);
            currentNode = currentNode.parent;
        } while (currentNode != null);
        return solution;
    }
}
