package app.delivering.mvp.ride.order.map.moved.model;


import android.location.Address;

public class MapCameraMovedModel {
    private String addressLine;
    private Address address;

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public Address getAddress() {
        return address;
    }
}
