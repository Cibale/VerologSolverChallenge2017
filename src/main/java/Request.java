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
    public int correspondingVehicleId;

    public Request(int id,
                   int customerId,
                   int firstDayForDelivery,
                   int lastDayForDelivery,
                   int durationInDays,
                   int toolId,
                   int numOfTools,
                   boolean negativeRequest) {
        this.id = id;
        this.customerId = customerId;
        this.firstDayForDelivery = firstDayForDelivery;
        this.lastDayForDelivery = lastDayForDelivery;
        this.durationInDays = durationInDays;
        this.toolId = toolId;
        this.numOfTools = numOfTools;
        this.negativeRequest = negativeRequest;
    }

    /**
     * Hard copy constructor.
     *
     * @param request request to be copied
     */
    public Request(Request request) {
        this(
                request.id,
                request.customerId,
                request.firstDayForDelivery,
                request.lastDayForDelivery,
                request.durationInDays,
                request.toolId,
                request.numOfTools,
                request.negativeRequest
        );
        this.correspondingVehicleId = request.correspondingVehicleId;
    }

    /**
     * Creates negative request from this one. Id of those requests is the same.
     *
     * @return negative request.
     */
    public Request getNegativeRequest() {
        Request negativeRequest = new Request(this);
        negativeRequest.pickedDayForDelivery = this.pickedDayForDelivery + this.durationInDays;
        this.negativeRequest = true;
        return negativeRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (id != request.id) return false;
        if (negativeRequest != request.negativeRequest) return false;
        if (customerId != request.customerId) return false;
        if (firstDayForDelivery != request.firstDayForDelivery) return false;
        if (lastDayForDelivery != request.lastDayForDelivery) return false;
        if (durationInDays != request.durationInDays) return false;
        if (toolId != request.toolId) return false;
        if (numOfTools != request.numOfTools) return false;
        if (pickedDayForDelivery != request.pickedDayForDelivery) return false;
        return correspondingVehicleId == request.correspondingVehicleId;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (negativeRequest ? 1 : 0);
        result = 31 * result + customerId;
        result = 31 * result + firstDayForDelivery;
        result = 31 * result + lastDayForDelivery;
        result = 31 * result + durationInDays;
        result = 31 * result + toolId;
        result = 31 * result + numOfTools;
        result = 31 * result + pickedDayForDelivery;
        result = 31 * result + correspondingVehicleId;
        return result;
    }
}
