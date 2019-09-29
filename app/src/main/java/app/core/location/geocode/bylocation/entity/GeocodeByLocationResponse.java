package app.core.location.geocode.bylocation.entity;


import android.location.Address;

public class GeocodeByLocationResponse {
    private Address address;

    public GeocodeByLocationResponse(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
