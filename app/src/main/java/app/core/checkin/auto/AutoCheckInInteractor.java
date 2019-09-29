package app.core.checkin.auto;

import android.content.Context;

import app.core.BaseInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.checkin.auto.entity.AutoCheckInResult;
import app.core.checkin.context.interactor.CheckInWithContextInteractor;
import app.core.checkin.user.post.entity.CheckInRequest;
import app.delivering.mvp.bars.detail.checkin.open.model.OpenTabRequest;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

public class AutoCheckInInteractor implements BaseInteractor<BarModel, Observable<AutoCheckInResult>> {
    private final CheckInWithContextInteractor checkInWithContextInteractor;

    public AutoCheckInInteractor(Context context) {
        checkInWithContextInteractor = new CheckInWithContextInteractor(context);
    }

    @Override
    public Observable<AutoCheckInResult> process(BarModel barModel) {
        return Observable.just(barModel)
                //check is user already has opened Tab
                .map(venue -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .filter(checkInID -> (long) checkInID == 0)
                .doOnNext(venue -> LogToFileHandler.addLog("No Active checkins. Start auto-check in process"))
                //check user settings
                .concatMap(venue -> Observable.just(QorumSharedCache.checkAutoCheckInSettings().get(BaseCacheType.BOOLEAN)))
                .concatMap(isAutoOpeningSettingOn -> getAutoCheckInResult(barModel, (boolean) isAutoOpeningSettingOn));
    }

    private Observable<AutoCheckInResult> getAutoCheckInResult(BarModel barModel, boolean isAutoOpeningSettingOn) {
        LogToFileHandler.addLog("AutoCheckInInteractor. isAutoOpeningSettingOn - " + isAutoOpeningSettingOn);
        AutoCheckInResult result = new AutoCheckInResult();
        result.setAutoOpeningSettingOn(isAutoOpeningSettingOn);
        result.setVenue(barModel);
        if (isAutoOpeningSettingOn) {
            OpenTabRequest request = new OpenTabRequest();
            request.setIgnoreAnotherCheckIns(false);
            CheckInRequest checkInRequest = new CheckInRequest(barModel.getId());
            request.setCheckinRequest(checkInRequest);
            return checkInWithContextInteractor.process(request)
                    .doOnNext(response -> LogToFileHandler.addLog("AutoCheckInInteractor. New tab - " + response.getCheckin().getId()))
                    .map(checkInResponse -> {
                        result.setCheckIn(checkInResponse);
                        return result;
                    });
        } else
            return Observable.just(result);
    }
}
