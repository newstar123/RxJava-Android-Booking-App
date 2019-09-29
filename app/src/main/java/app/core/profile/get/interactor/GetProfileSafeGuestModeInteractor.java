package app.core.profile.get.interactor;

import app.core.BaseOutputInteractor;
import app.core.login.check.CheckAccountInteractor;
import app.core.profile.get.entity.ProfileModel;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.permissions.account.CheckAccountPermissionGateway;
import app.gateway.profile.get.GetProfileRestGateway;
import rx.Observable;

public class GetProfileSafeGuestModeInteractor implements BaseOutputInteractor<Observable<ProfileModel>> {
    private final GetProfileInteractor getProfileInteractor;
    private final AndroidAuthTokenGateway authTokenGateway;
    private final CheckAccountPermissionGateway permissionGateway;

    public GetProfileSafeGuestModeInteractor(BaseActivity activity) {
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
        authTokenGateway = new AndroidAuthTokenGateway(activity);
        permissionGateway = new CheckAccountPermissionGateway(activity);
    }

    @Override public Observable<ProfileModel> process() {
        return permissionGateway.check()
                .concatMap(isGranted -> authTokenGateway.get())
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .concatMap(t -> getProfileInteractor.process());
    }
}
