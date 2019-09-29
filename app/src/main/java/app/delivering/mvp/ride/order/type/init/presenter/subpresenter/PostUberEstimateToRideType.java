package app.delivering.mvp.ride.order.type.init.presenter.subpresenter;

import com.uber.sdk.rides.client.model.RideEstimate;

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.product.entity.UberProductResponse;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;



public class PostUberEstimateToRideType {
    private final RideTypeImageFactory rideTypeImageFactory;
    private double discount;

    public PostUberEstimateToRideType(double discount) {
        rideTypeImageFactory = new RideTypeImageFactory();
        this.discount = discount;
    }

    public RideType convert(PostUberEstimateResponse response) {
        UberProductResponse product = response.getProduct();
        RideType rideType = new RideType();
        rideType.setProductId(product.getProductId());
        rideType.setName(product.getDisplayName());
        rideType.setCapacity(String.valueOf(product.getCapacity()));
        rideType.setSlogan(product.getDescription());
        rideType.setShare(product.isShared());
        rideType.setUpfrontFareEnabled(product.isUpfrontFareEnabled());
        int rideTypeImageId = rideTypeImageFactory.get(rideType.getName());
        rideType.setImageId(rideTypeImageId);
        if (response.getRideEstimate() != null)
            prepareFareEstimates(rideType, response.getRideEstimate());
        else
            fillEmptyFareEstimates(rideType);
        return rideType;
    }

    private void prepareFareEstimates(RideType rideType, RideEstimate rideEstimate) {
        RideEstimate.Fare fare = rideEstimate.getFare();
        String display = calculateFare(rideEstimate);
        rideType.setFare(display);
        String farePlusDiscountValue = calculateDiscount(rideEstimate);
        rideType.setFarePlusDiscountValue(farePlusDiscountValue);
        rideType.setFareId(fare.getFareId());
        rideType.setFareExpired(fare.getExpiresAt());
        Integer pickupEstimate = rideEstimate.getPickupEstimate();
        rideType.setPickupEstimate(String.valueOf(pickupEstimate));
        int durationEstimate = rideEstimate.getTrip().getDurationEstimate();
        rideType.setDurationEstimate(String.valueOf(durationEstimate));
    }

    private String calculateFare(RideEstimate rideEstimate) {
        RideEstimate.Fare fare = rideEstimate.getFare();
        CurrencyUnit currency = CurrencyUnit.of(fare.getCurrencyCode());
        BigDecimal fareValue = fare.getValue();
        if (discount == 0)
            return currency.getSymbol() + fareValue.setScale(2, RoundingMode.CEILING);
        BigDecimal fareValueWithDiscount = fareValue.subtract(BigDecimal.valueOf(discount));
        BigDecimal fareValueWithDiscountAboveZero = fareValueWithDiscount.max(BigDecimal.ZERO);
        return currency.getSymbol() + fareValueWithDiscountAboveZero.setScale(2, RoundingMode.HALF_EVEN);
    }

    private String calculateDiscount(RideEstimate rideEstimate) {
        RideEstimate.Fare fare = rideEstimate.getFare();
        CurrencyUnit currency = CurrencyUnit.of(fare.getCurrencyCode());
        if (discount > 0)
            return currency.getSymbol() + fare.getValue().setScale(2, RoundingMode.CEILING);
        return "";
    }

    private void fillEmptyFareEstimates(RideType rideType) {
        rideType.setFare("");
        rideType.setFareId("");
        rideType.setPickupEstimate("");
        rideType.setDurationEstimate("");
        rideType.setFarePlusDiscountValue("");
    }
}
