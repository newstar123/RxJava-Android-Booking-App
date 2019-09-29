package app.delivering.component.service.order;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.service.interactor.DeleteUberContextInteractor;
import rx.Observable;
import rx.Subscription;

public class DeleteOrderService extends Service {
    private DeleteUberContextInteractor deleteUberInteractor;
    private Subscription subscribe;

    @Override public void onCreate() {
        super.onCreate();
        if (deleteUberInteractor == null)
            deleteUberInteractor = new DeleteUberContextInteractor();
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (subscribe == null || subscribe.isUnsubscribed())
            deleteRide();
        return Service.START_REDELIVER_INTENT;
    }

    private void deleteRide() {
        subscribe = Observable.interval(2, TimeUnit.SECONDS)
                .concatMap(count -> deleteUberInteractor.process(new DeleteUberRequest()))
                .onErrorResumeNext(this::goOnNextCycle)
                .subscribe(this::onResult, this::onError);
    }

    private Observable<DeleteUberResponse> goOnNextCycle(Throwable throwable) {
        return Observable.just(false)
                .filter(result -> result)
                .map(result -> new DeleteUberResponse());
    }

    private void onResult(DeleteUberResponse deleteUberResponse) {
        subscribe.unsubscribe();
        stopSelf();
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, "The uber ride deleting service was stopped", Toast.LENGTH_LONG).show();
    }
}
