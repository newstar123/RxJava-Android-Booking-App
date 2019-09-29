package app.delivering.mvp.profile.drawer.logout.presenter;


import android.content.Intent;

import app.CustomApplication;
import app.core.init.token.entity.NoAccountException;
import app.core.init.token.entity.Token;
import app.core.logout.logout.LogoutInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.service.beacon.BeaconService;
import app.delivering.component.service.beacon.broadcast.RangeCheckInReceiver;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.service.order.DeleteOrderService;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.bars.list.cache.holder.BarListRealTimeHolder;
import app.gateway.bars.locations.cache.holder.LocationsListRealTimeHolder;
import app.gateway.facebook.friends.cache.holder.FacebookFriendsRealTimeHolder;
import app.gateway.profile.cache.holder.ProfileRealTimeHolder;
import rx.Observable;


public class LogOutPresenter extends BaseOutputPresenter<Observable<Token>> {
    private final LogoutInteractor logoutInteractor;

    public LogOutPresenter(BaseActivity activity) {
        super(activity);
        logoutInteractor = new LogoutInteractor(activity);
    }

    @Override
    public Observable<Token> process() {
        return logoutInteractor.process()
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NoAccountException){
                        stopBeaconScanning();
                        stopCheckInUpdating();
                        stopMockRide();
                        stopGpsCheckOutRanging();
                        CustomApplication.get().setUpRestClient();
                        CustomApplication.get().setUpAutoCheckInController();
                        clearSavedRestResponses();
                    }
                    return Observable.error(throwable);
                });
    }

    private void stopBeaconScanning() {
        Intent stopIntent = new Intent(getActivity(), BeaconService.class);
        stopIntent.setAction(BeaconService.STOP_FOREGROUND_ACTION);
        getActivity().startService(stopIntent);
    }

    private void stopCheckInUpdating() {
        Intent intent = new Intent(getActivity(), TabStatusForegroundService.class);
        intent.setAction(TabStatusForegroundService.STOP_FOREGROUND_ACTION);
        getActivity().startService(intent);
    }

    private void stopMockRide() {
        Intent intent = new Intent(getActivity(), DeleteOrderService.class);
        getActivity().startService(intent);
    }

    private void stopGpsCheckOutRanging() {
        getActivity().sendBroadcast(new Intent(RangeCheckInReceiver.ON_CHECK_OUT_ACTION));
    }

    private void clearSavedRestResponses() {
        BarListRealTimeHolder.clear();
        LocationsListRealTimeHolder.clear();
        ProfileRealTimeHolder.clear();
        FacebookFriendsRealTimeHolder.clear();
    }


}
