package app.core.ride.delayed.entity;

import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class DelayedRidesResponse {
    private List<GetCheckInsResponse> tabsWithFreeRides;

    private boolean isFreeRideMarkAlreadyShown;

    public DelayedRidesResponse(List<GetCheckInsResponse> tabsWithFreeRides) {
        this.tabsWithFreeRides = tabsWithFreeRides;
    }

    public List<GetCheckInsResponse> getTabsWithFreeRides() {
        return tabsWithFreeRides;
    }

    public boolean isFreeRideMarkAlreadyShown() {
        return isFreeRideMarkAlreadyShown;
    }

    public void setFreeRideMarkAlreadyShown(boolean freeRideMarkAlreadyShown) {
        isFreeRideMarkAlreadyShown = freeRideMarkAlreadyShown;
    }
}
