package app.delivering.mvp.ride.order.init.tobar.model;


import app.core.bars.list.get.entity.BarModel;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;

public class ToBarAddressModel {
    private ApplyAddressEvent applyAddressEvent;
    private BarModel barModel;

    public void setApplyAddressEvent(ApplyAddressEvent applyAddressEvent) {
        this.applyAddressEvent = applyAddressEvent;
    }

    public void setBarModel(BarModel barModel) {
        this.barModel = barModel;
    }

    public ApplyAddressEvent getApplyAddressEvent() {
        return applyAddressEvent;
    }

    public BarModel getBarModel() {
        return barModel;
    }
}
