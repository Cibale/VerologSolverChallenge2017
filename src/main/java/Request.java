package main.java;

/**
 * Created by mmatak on 6/15/17.
 */
public class Request {
    public int id;
    public boolean negativeRequest;
    public int customerId;
    public int firstDayForDelivery;
    public int lastDayForDelivery;
    // how many days tools must stay at the customer
    public int durationInDays;
    public int toolId;
    public int numOfTools;
    public int pickedDayForDelivery;

    public Request(int id, int customerId, int firstDayForDelivery, int lastDayForDelivery, int durationInDays, int toolId, int numOfTools) {
        this.id = id;
        this.customerId = customerId;
        this.firstDayForDelivery = firstDayForDelivery;
        this.lastDayForDelivery = lastDayForDelivery;
        this.durationInDays = durationInDays;
        this.toolId = toolId;
        this.numOfTools = numOfTools;
        this.negativeRequest = false;
    }

    /**
     * For negative requests, i.e. requests for picking up tools
     */
    public Request(Request positiveRequest) {
        //TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (id != request.id) return false;
        if (customerId != request.customerId) return false;
        if (firstDayForDelivery != request.firstDayForDelivery) return false;
        if (lastDayForDelivery != request.lastDayForDelivery) return false;
        if (durationInDays != request.durationInDays) return false;
        if (toolId != request.toolId) return false;
        return numOfTools == request.numOfTools;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + customerId;
        result = 31 * result + firstDayForDelivery;
        result = 31 * result + lastDayForDelivery;
        result = 31 * result + durationInDays;
        result = 31 * result + toolId;
        result = 31 * result + numOfTools;
        return result;
    }
}
