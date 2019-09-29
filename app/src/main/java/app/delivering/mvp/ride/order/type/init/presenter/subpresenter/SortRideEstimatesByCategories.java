package app.delivering.mvp.ride.order.type.init.presenter.subpresenter;


import android.text.TextUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import rx.Observable;

public class SortRideEstimatesByCategories {
    private final RideCategoryFactory rideCategoryFactory;
    private final PostUberEstimateToRideType postUberEstimateToRideType;

    public SortRideEstimatesByCategories(double discount) {
        rideCategoryFactory = new RideCategoryFactory();
        postUberEstimateToRideType = new PostUberEstimateToRideType(discount);
    }

    public List<RideCategory> prepareRides(List<PostUberEstimateResponse> response) {
        List<RideType> rideTypes = Observable.from(response)
                .map(postUberEstimateToRideType::convert)
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        List<String> categories = Observable.from(response)
                .map(product -> rideCategoryFactory.get(product.getProduct().getDisplayName()))
                .filter(category -> !TextUtils.isEmpty(category))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        Set<String> uniqueCategories = new HashSet<>(categories);
        return Observable.from(uniqueCategories)
                .flatMap(categoryName -> applyCategoryToRide(categoryName, rideTypes))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
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
