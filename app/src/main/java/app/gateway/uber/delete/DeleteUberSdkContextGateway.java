package app.gateway.uber.delete;

import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.activity.gateway.DeleteUberGateway;
import app.gateway.uber.UberSdkServiceWithoutTokenCheck;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class DeleteUberSdkContextGateway implements DeleteUberGateway {

    @Override public Observable<DeleteUberResponse> delete(DeleteUberRequest request) {
        return doRequest(request);
    }

    private Observable<DeleteUberResponse> doRequest(DeleteUberRequest request) {
        return Observable.create(new Observable.OnSubscribe<DeleteUberResponse>() {
            @Override public void call(Subscriber<? super DeleteUberResponse> subscriber) {
                RidesService service = UberSdkServiceWithoutTokenCheck.create();
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
