package main.java;

/**
 * Created by mmatak on 6/15/17.
 */
public class Request {
    public int id;
    public int depotId;
    public int firstDayForDelivery;
    public int lastDayForDelivery;
    // how many days tools must stay at the customer
    public int durationInDays;
    public int toolId;
    public int numOfTools;
    public int pickedDayForDelivery;

    public Request(int id, int depotId, int firstDayForDelivery, int lastDayForDelivery, int durationInDays, int toolId, int numOfTools) {
        this.id = id;
        this.depotId = depotId;
        this.firstDayForDelivery = firstDayForDelivery;
        this.lastDayForDelivery = lastDayForDelivery;
        this.durationInDays = durationInDays;
        this.toolId = toolId;
        this.numOfTools = numOfTools;
    }
}
