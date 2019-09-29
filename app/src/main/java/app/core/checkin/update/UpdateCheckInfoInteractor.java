package app.core.checkin.update;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.checkin.list.GetListCheckInsInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.service.feedback.events.ShowFeedbackEvent;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class UpdateCheckInfoInteractor implements BaseOutputInteractor<Observable<CurrentCheckInInfo>> {

    private GetListCheckInsInteractor getListCheckInsInteractor;


    public UpdateCheckInfoInteractor(BaseActivity activity) {
        getListCheckInsInteractor = new GetListCheckInsInteractor(activity);
    }

    @Override
    public Observable<CurrentCheckInInfo> process() {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .concatMap(val -> loadCheckInInfo((long)val));
    }

    private Observable<CurrentCheckInInfo> loadCheckInInfo(Long checkInId) {
        if (checkInId == 0)
            return getListCheckInsInteractor.process()
                    .concatMap(this::getListOfCheckIns);
        else
            return Observable.just((long)QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG))
                    .map(barId -> new CurrentCheckInInfo(checkInId, barId));
    }

    private Observable<CurrentCheckInInfo> getListOfCheckIns(List<GetCheckInsResponse> activeCheckIn) {
        for (GetCheckInsResponse singleCheckIn : activeCheckIn) {
            if (singleCheckIn.getCheckoutTime() == null)
                return getDataFromOpenedCheckIn(singleCheckIn);
            else
                checkFeedbackAlreadyShown(singleCheckIn);
        }
        return Observable.just(new CurrentCheckInInfo());
    }

    private void checkFeedbackAlreadyShown(GetCheckInsResponse checkIn) {
        if (checkIn.getFeedback() == null && (checkIn.getBillItems() != null && !checkIn.getBillItems().isEmpty())){
            EventBus.getDefault().postSticky(new ShowFeedbackEvent(checkIn));
        }
    }

    private Observable<CurrentCheckInInfo> getDataFromOpenedCheckIn(GetCheckInsResponse response) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, response.getId()))
                .concatMap(val -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, response.getVendorId())))
                .concatMap(val -> Observable.just(new CurrentCheckInInfo(response.getId(), (long) response.getVendorId())));
    }
}
