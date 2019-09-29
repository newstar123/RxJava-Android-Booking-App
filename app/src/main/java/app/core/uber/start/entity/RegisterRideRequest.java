package app.core.uber.start.entity;


import com.google.gson.annotations.SerializedName;

public class RegisterRideRequest {
    @SerializedName("request_id") String requestId;
    @SerializedName("ride_type") RideDirection rideType;
    @SerializedName("start_address") String startAddress;
    @SerializedName("start_latitude") double startLatitude;
    @SerializedName("start_longitude") double startLongitude;
    @SerializedName("destination_address") String destinationAddress;
    @SerializedName("destination_latitude") double destinationLatitude;
    @SerializedName("destination_longitude") double destinationLongitude;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRideType(RideDirection rideType) {
        this.rideType = rideType;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
}
