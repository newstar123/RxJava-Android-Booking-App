package app.delivering.mvp.tab.init.events;

import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.mvp.tab.discount.model.DiscountModel;

public class UpdateTabEvent {
    private DiscountModel roundDiscountModel;
    private CheckInResponse checkIn;

    public DiscountModel getDiscountModel() {
        return roundDiscountModel;
    }

    public void setRoundDiscountModel(DiscountModel roundDiscountModel) {
        this.roundDiscountModel = roundDiscountModel;
    }

    public CheckInResponse getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(CheckInResponse checkIn) {
        this.checkIn = checkIn;
    }
}
