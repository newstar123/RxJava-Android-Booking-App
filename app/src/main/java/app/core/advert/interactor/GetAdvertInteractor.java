package app.core.advert.interactor;


import app.core.BaseOutputInteractor;
import app.core.advert.entity.AdvertResponse;
import app.core.advert.gateway.AdvertGateway;
import app.delivering.component.BaseActivity;
import app.gateway.advert.AdvertRestGateway;
import app.gateway.permission.NetworkAccessSimpleGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetAdvertInteractor implements BaseOutputInteractor<Observable<AdvertResponse>> {
    private final AdvertGateway advertGateway;
    private final NetworkAccessSimpleGateway networkAccessSimpleGateway;

    public GetAdvertInteractor(BaseActivity activity) {
        advertGateway = new AdvertRestGateway();
        networkAccessSimpleGateway = new NetworkAccessSimpleGateway(activity);
    }

    @Override public Observable<AdvertResponse> process() {
        return networkAccessSimpleGateway.check()
                .observeOn(Schedulers.io())
                .concatMap(isEnabled -> advertGateway.get());
    }
}
