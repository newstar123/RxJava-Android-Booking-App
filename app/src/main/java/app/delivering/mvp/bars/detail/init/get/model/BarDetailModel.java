package app.delivering.mvp.bars.detail.init.get.model;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;

public class BarDetailModel {
    private BarModel barModel;
    private List<String> barImageUrls;
    private List<String> barDescriptions;
    private String routing;
    private String discount;
    private String discountText;

    public BarModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarModel barModel) {
        this.barModel = barModel;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public List<String> getBarImageUrls() {
        return barImageUrls;
    }

    public void setBarImageUrls(List<String> barImageUrls) {
        this.barImageUrls = barImageUrls;
    }

    public List<String> getBarDescriptions() {
        return barDescriptions;
    }

    public void setBarDescriptions(List<String> barDescriptions) {
        this.barDescriptions = barDescriptions;
    }
}
