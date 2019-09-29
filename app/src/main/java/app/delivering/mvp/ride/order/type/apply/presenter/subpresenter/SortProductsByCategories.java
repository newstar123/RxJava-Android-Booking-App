package app.delivering.mvp.ride.order.type.apply.presenter.subpresenter;


import android.text.TextUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.core.uber.product.entity.UberProductResponse;
import app.core.uber.product.entity.UberProductsResponse;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.apply.model.ApplyTypesModel;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideTypeImageFactory;
import rx.Observable;

public class SortProductsByCategories {
    private final RideCategoryFactory rideCategoryFactory;
    private final RideTypeImageFactory rideTypeImageFactory;

    public SortProductsByCategories() {
        rideCategoryFactory = new RideCategoryFactory();
        rideTypeImageFactory = new RideTypeImageFactory();
    }

    public ApplyTypesModel prepareRides(UberProductsResponse response) {
        List<RideType> rideTypes = Observable.from(response.getProducts())
                .map(this::convert)
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        List<String> categories = Observable.from(response.getProducts())
                .map(product -> rideCategoryFactory.get(product.getDisplayName()))
                .filter(category -> !TextUtils.isEmpty(category))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        Set<String> uniqCategories = new HashSet<>(categories);
        List<RideCategory> rideWithCategories = Observable.from(uniqCategories)
                .flatMap(categoryName -> applyCategoryToRide(categoryName, rideTypes))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        ApplyTypesModel applyTypesModel = new ApplyTypesModel();
        applyTypesModel.setProducts(response);
        applyTypesModel.setRideWithCategories(rideWithCategories);
        return applyTypesModel;
    }

    private RideType convert(UberProductResponse product) {
        RideType rideType = new RideType();
        rideType.setProductId(product.getProductId());
        rideType.setName(product.getDisplayName());
        rideType.setCapacity(String.valueOf(product.getCapacity()));
        rideType.setSlogan(product.getDescription());
        rideType.setShare(product.isShared());
        rideType.setUpfrontFareEnabled(product.isUpfrontFareEnabled());
        int rideTypeImageId = rideTypeImageFactory.get(rideType.getName());
        rideType.setImageId(rideTypeImageId);
        rideType.setFare("");
        rideType.setFareId("");
        rideType.setPickupEstimate("");
        rideType.setDurationEstimate("");
        return rideType;
    }

    private Observable<RideCategory> applyCategoryToRide(String categoryName, List<RideType> rideTypes) {
        return Observable.from(rideTypes)
                .filter(rideType -> rideCategoryFactory.get(rideType.getName()).equals(categoryName))
                .toList()
                .map(rides -> create(categoryName, rides));
    }

    private RideCategory create(String categoryName, List<RideType> rideType) {
        RideCategory rideCategory = new RideCategory();
        rideCategory.setName(categoryName);
        rideCategory.setRideTypes(rideType);
        return rideCategory;
    }
}
