package app.delivering.mvp.beacons.scanner;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.core.login.check.CheckAccountInteractor;
import app.delivering.mvp.BaseContextOutputPresenter;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.bars.list.get.GetBarListGateway;
import app.qamode.log.LogToFileHandler;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BeaconInitScannerPresenter extends BaseContextOutputPresenter<Observable<List<BarModel>>> {
    private final GetBarListGateway getBarListGateway;
    private final AuthTokenWithContextGateway authTokenGateway;

    public BeaconInitScannerPresenter(Context context) {
        super(context);
        getBarListGateway = new GetBarListGateway();
        authTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override
    public Observable<List<BarModel>> process() {
        return authTokenGateway.get()
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .observeOn(Schedulers.io())
                .concatMap(token -> getBarListGateway.get(0, 0, 50000))
                .map(this::getBarListWithActiveBeacons);
    }

    private List<BarModel> getBarListWithActiveBeacons(List<BarModel> barModels) {
        ArrayList<BarModel> barModelList = new ArrayList<>();
        for (BarModel bar : barModels)
            if (!bar.getBeacons().isEmpty()) {
                LogToFileHandler.addLog("Venue with active Beacons - " + bar.getName());
                barModelList.add(bar);
            }
        return barModelList;
    }
}
