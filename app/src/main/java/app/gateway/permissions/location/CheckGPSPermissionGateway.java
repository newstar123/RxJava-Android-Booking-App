package app.gateway.permissions.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import app.delivering.component.BaseActivity;
import app.delivering.component.main.location.InitialLocationReasonActivity;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.permissions.PermissionGatewayImpl;
import rx.Observable;

public class CheckGPSPermissionGateway extends PermissionGatewayImpl {

    public CheckGPSPermissionGateway(BaseActivity activity) {
        super(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public Observable<Boolean> askLocationPermission() {
        return MixpanelSendGateway.send(MixpanelEvents.getLocationGpsPermissionAskedEvent())
                .concatMap(aBoolean -> super.askLocationPermission());
    }

    @Override
    public void onAccessGranted(Context context) {
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getLocationGpsPermissionResolvedEvent(true));
    }

    @Override
    public void onAccessDenied(Context context) {
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getLocationGpsPermissionResolvedEvent(false));
        Intent intent = new Intent(context, InitialLocationReasonActivity.class);
        intent.putExtra(InitialLocationReasonActivity.IS_CHECK_GPS_RESULT_CANCELED, true);
        context.startActivity(intent);
    }

    @Override
    public void onDoNotAskAgainActivated(Context context) {
        Intent intent = new Intent(context, InitialLocationReasonActivity.class);
        intent.putExtra(InitialLocationReasonActivity.IS_CHECK_GPS_RESULT_CANCELED, true);
        intent.putExtra(InitialLocationReasonActivity.provideToAppSettings, true);
        context.startActivity(intent);
    }
}
