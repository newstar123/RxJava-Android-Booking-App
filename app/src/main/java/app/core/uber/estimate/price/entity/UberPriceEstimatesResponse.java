package app.core.uber.estimate.price.entity;

import java.util.List;

public class UberPriceEstimatesResponse {
    private List<UberPriceEstimateResponse> prices;

    public List<UberPriceEstimateResponse> getPrices() {
        return prices;
    }

    public void setPrices(List<UberPriceEstimateResponse> prices) {
        this.prices = prices;
    }
}
