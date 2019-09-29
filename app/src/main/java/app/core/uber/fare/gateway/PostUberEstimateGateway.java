package app.core.uber.fare.gateway;


import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import rx.Observable;

public interface PostUberEstimateGateway {
    Observable<PostUberEstimateResponse> post(PostUberEstimateRequest request);
}
