package app.core.location.current.interactor;


import app.core.BaseOutputInteractor;
import app.core.location.current.entity.CurrentPlaceResponse;
import app.core.location.current.gateway.CurrentPlaceGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.location.place.current.CurrentPlaceRxLibGateway;
import rx.Observable;

public class CurrentPlaceInteractor implements BaseOutputInteractor<Observable<CurrentPlaceResponse>> {
    private final PermissionInteractor permissionInteractor;
    private final CurrentPlaceGateway currentPlaceRxLibGateway;

    public CurrentPlaceInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        currentPlaceRxLibGateway = new CurrentPlaceRxLibGateway();
    }

    @Override public Observable<CurrentPlaceResponse> process() {
        return permissionInteractor.process()
                .concatMap(isGranted -> currentPlaceRxLibGateway.get())
                .map(CurrentPlaceResponse::new);
    }
}
