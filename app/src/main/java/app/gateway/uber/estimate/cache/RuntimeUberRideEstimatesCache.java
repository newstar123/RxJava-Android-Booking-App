package app.gateway.uber.estimate.cache;


import java.util.List;

import app.core.uber.fares.entity.PostUberEstimateResponse;

public class RuntimeUberRideEstimatesCache {
    public static volatile List<PostUberEstimateResponse> rideEstimatesResponse;
}
