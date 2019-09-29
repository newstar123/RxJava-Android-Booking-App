package app.delivering.mvp.push.ride.complete.to.presenter;

import android.content.Context;

import app.core.checkin.context.interactor.CheckInWithContextInteractor;
import app.core.checkin.user.post.entity.CheckInRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.uber.tracking.to.bar.interactor.EndRideToBarTrackingInteractor;
import app.delivering.mvp.BaseContextPresenter;
import app.delivering.mvp.bars.detail.checkin.open.model.OpenTabRequest;
import app.delivering.mvp.push.ride.complete.to.events.PushRideToBarCompleteEvent;
import rx.Observable;

public class AutoCheckInAfterRideToBarPresenter extends BaseContextPresenter<PushRideToBarCompleteEvent, Observable<CheckInResponse>> {
    private final EndRideToBarTrackingInteractor endRideInteractor;
    private final CheckInWithContextInteractor checkInInteractor;

    public AutoCheckInAfterRideToBarPresenter(Context context) {
        super(context);
        endRideInteractor = new EndRideToBarTrackingInteractor(context);
        checkInInteractor = new CheckInWithContextInteractor(context);
    }

    @Override
    public Observable<CheckInResponse> process(PushRideToBarCompleteEvent pushRideToBarCompleteEvent) {
        return endRideInteractor.process(pushRideToBarCompleteEvent) //BarModel
                .map(barModel -> {
                    OpenTabRequest request = new OpenTabRequest();
                    request.setIgnoreAnotherCheckIns(true);
                    request.setIgnoreBluetoothState(true);
                    request.setCheckinRequest(new CheckInRequest(barModel.getId()));
                    return request;
                })
                .concatMap(checkInInteractor::process);
    }
}
