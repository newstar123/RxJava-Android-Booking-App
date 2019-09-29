package app.gateway.uber.delete;

import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.activity.gateway.DeleteUberGateway;
import app.delivering.component.BaseActivity;
import app.gateway.uber.auth.GetUberAuthTokenSdkGateway;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class DeleteUberSdkGateway implements DeleteUberGateway {
    private final GetUberAuthTokenSdkGateway getUberAuthTokenGateway;

    public DeleteUberSdkGateway(BaseActivity activity) {
        getUberAuthTokenGateway = new GetUberAuthTokenSdkGateway(activity);
    }

    @Override public Observable<DeleteUberResponse> delete(DeleteUberRequest request) {
        return getUberAuthTokenGateway.get()
                .concatMap(uberAuthToken -> doRequest(uberAuthToken, request));
    }

    private Observable<DeleteUberResponse> doRequest(UberAuthToken uberAuthToken,
                                                     DeleteUberRequest request) {
        return Observable.create(new Observable.OnSubscribe<DeleteUberResponse>() {
            @Override public void call(Subscriber<? super DeleteUberResponse> subscriber) {
                RidesService service = UberRidesApi.with(uberAuthToken.getSession()).build().createService();
                Call<Void> cancelCurrentRide = service.cancelCurrentRide();
                try {
                    Response<Void> execute = cancelCurrentRide.execute();
                    if (!execute.isSuccessful())
                        subscriber.onError(new RuntimeException(execute.errorBody().string()));
                    subscriber.onNext(new DeleteUberResponse());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
