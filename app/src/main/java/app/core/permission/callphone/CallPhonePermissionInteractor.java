package app.core.permission.callphone;

import app.core.BaseOutputInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.phone.CheckCallPhonePermissionGateway;
import rx.Observable;

public class CallPhonePermissionInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private CheckCallPhonePermissionGateway callPhonePermissionGateway;

    public CallPhonePermissionInteractor(BaseActivity activity) {
        callPhonePermissionGateway = new CheckCallPhonePermissionGateway(activity);
    }

    @Override public Observable<Boolean> process() {
        return  callPhonePermissionGateway.check();
    }

}
