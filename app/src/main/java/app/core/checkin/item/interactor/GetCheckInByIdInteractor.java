package app.core.checkin.item.interactor;

import android.content.Context;

import app.core.checkin.item.gateway.GetCheckInGateway;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.gateway.checkin.item.GetCheckInByIdRestGateway;
import rx.Observable;

public class GetCheckInByIdInteractor implements GetCheckInInteractor {
    private GetCheckInGateway getCheckInGateway;

    public GetCheckInByIdInteractor(Context context) {
        getCheckInGateway = new GetCheckInByIdRestGateway(context);
    }

    @Override
    public Observable<CheckInResponse> forceLoad(long checkInId) {
        return getCheckInGateway.get(checkInId, true);
    }

    @Override
    public Observable<CheckInResponse> process(Long checkInId) {
        return getCheckInGateway.get(checkInId, false);
    }
}
