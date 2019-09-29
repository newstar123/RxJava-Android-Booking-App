package app.delivering.mvp.tab.content.model;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.post.entity.CheckInResponse;

public class TabContentRequest {
    private GetCheckInsResponse checkIn;
    private int gratuity;
    private double exGratuity;

    public TabContentRequest(int gratuity, double exGratuity, CheckInResponse checkInResponse) {
        this.gratuity = gratuity;
        this.exGratuity = exGratuity;
        this.checkIn = checkInResponse.getCheckin();
    }

    public TabContentRequest(int gratuity, double exGratuity) {
        this.gratuity = gratuity;
        this.exGratuity = exGratuity;
    }

    public GetCheckInsResponse getCheckIn() {
        return checkIn;
    }

    public int getGratuity() {
        return gratuity;
    }

    public double getExGratuity() {
        return exGratuity;
    }
}
