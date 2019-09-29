package app.core.uber.fares.gateway;


import app.core.uber.fares.entity.PostUberEstimatesRequest;
import app.core.uber.fares.entity.PostUberEstimatesResponse;
import rx.Observable;

public interface PostUberEstimatesGateway {
    Observable<PostUberEstimatesResponse> post(PostUberEstimatesRequest request);
}
