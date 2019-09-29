package app.gateway.uber.fares;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.fares.entity.PostUberEstimatesRequest;
import app.core.uber.fares.entity.PostUberEstimatesResponse;
import app.core.uber.fares.gateway.PostUberEstimatesGateway;
import app.core.uber.product.entity.UberProductResponse;
import app.delivering.component.BaseActivity;
import app.gateway.uber.fare.PostUberEstimateCacheGateway;
import app.gateway.uber.fare.PostUberEstimateRestGateway;
import rx.Observable;
import rx.schedulers.Schedulers;


public class PostUberEstimatesMainGateway implements PostUberEstimatesGateway {
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final PostUberEstimateGateway postUberEstimateGateway;
    private final PostUberEstimateCacheGateway postUberEstimateCacheGateway;

    public PostUberEstimatesMainGateway(BaseActivity activity) {
        postUberEstimateGateway = new PostUberEstimateRestGateway(activity);
        postUberEstimateCacheGateway = new PostUberEstimateCacheGateway();
    }

    @Override public Observable<PostUberEstimatesResponse> post(PostUberEstimatesRequest requests) {
        return Observable.from(requests.getProducts().getProducts())
                .map(product -> convertRequest(product, requests))
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.from(executorService))
                .concatMap(request -> Observable.just(request).delay(300, TimeUnit.MILLISECONDS, Schedulers.from(executorService)))
                .concatMap(this::getFare)
                .toList()
                .map(this::convert);
    }

    private PostUberEstimateRequest convertRequest(UberProductResponse product, PostUberEstimatesRequest request) {
        PostUberEstimateRequest postUberEstimateRequest = new PostUberEstimateRequest();
        postUberEstimateRequest.setCapacity(0);
        postUberEstimateRequest.setDeparture(request.getDeparture());
        postUberEstimateRequest.setDestination(request.getDestination());
        postUberEstimateRequest.setProduct(product);
        return postUberEstimateRequest;
    }

    private Observable<PostUberEstimateResponse> getFare(PostUberEstimateRequest request) {
        return Observable.concat(postUberEstimateCacheGateway.post(request),
                postUberEstimateGateway.post(request))
                .filter(result -> result != null);
    }

    private PostUberEstimatesResponse convert(List<PostUberEstimateResponse> estimates) {
        PostUberEstimatesResponse postUberEstimatesResponse = new PostUberEstimatesResponse();
        postUberEstimatesResponse.setEstimates(estimates);
        return postUberEstimatesResponse;
    }


}
