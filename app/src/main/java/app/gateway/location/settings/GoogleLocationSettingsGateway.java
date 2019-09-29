package app.gateway.location.settings;


import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import app.core.init.token.gateway.LocationSettingsGateway;
import app.core.permission.entity.LocationSettingsException;
import app.delivering.component.BaseActivity;
import app.gateway.google.ApiClientGateway;
import rx.Observable;
import rx.Subscriber;

public class GoogleLocationSettingsGateway implements LocationSettingsGateway {
    public final static int CHECK_GPS_ACTIVE = 57555;
    private final ApiClientGateway apiClientGateway;
    private BaseActivity activity;

    public GoogleLocationSettingsGateway(BaseActivity activity) {
        this.activity = activity;
        apiClientGateway = new ApiClientGateway();
    }

    @Override public Observable<Boolean> get() {
        return apiClientGateway.call(activity).concatMap(this::create);
    }

    private Observable<Boolean> create(GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override public void call(Subscriber<? super Boolean> subscriber) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                        .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));

                builder.setAlwaysShow(true);
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(locationSettingsResult->{
                    Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(activity, CHECK_GPS_ACTIVE);
                                subscriber.onNext(false);
                                subscriber.onCompleted();
                            } catch (IntentSender.SendIntentException | NullPointerException ignored) {
                                subscriber.onError(new Exception());
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            subscriber.onError(new Exception());
                            break;
                    }
                });
            }
        }).doOnNext(isGranted->{
            if (!isGranted) throw new LocationSettingsException();
        });
    }
}


