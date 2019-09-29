package app.core.init.notoken.interactor;


import app.core.BaseOutputInteractor;
import app.core.location.image.interactor.LocationImageInteractor;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import rx.Observable;

public class InitNoTokenInteractor implements BaseOutputInteractor<Observable<String>> {
    private final PermissionInteractor permissionInteractor;
    private final LocationImageInteractor locationImageInteractor;

    public InitNoTokenInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        locationImageInteractor = new LocationImageInteractor(activity);
    }

    @Override public Observable<String> process() {
        return permissionInteractor.process()
                .concatMap(isGranted -> locationImageInteractor.process());
    }




}