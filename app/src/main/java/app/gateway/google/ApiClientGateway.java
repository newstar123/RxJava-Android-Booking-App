package app.gateway.google;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;

public class ApiClientGateway {

    public Observable<GoogleApiClient> call(Context context) {
        return Observable.create(new Observable.OnSubscribe<GoogleApiClient>() {
            public GoogleApiClient googleApiClient;

            @Override public void call(Subscriber<? super GoogleApiClient> subscriber) {
                GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context);
                builder.addApi(LocationServices.API);
                builder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override public void onConnected(@Nullable Bundle bundle) {
                        subscriber.onNext(googleApiClient);
                        subscriber.onCompleted();
                    }

                    @Override public void onConnectionSuspended(int i) {

                    }
                });
                builder.addOnConnectionFailedListener(connectionResult -> {
                    subscriber.onError(new Exception());
                });
                googleApiClient = builder.build();
                googleApiClient.connect();
            }
        });
    }
}
