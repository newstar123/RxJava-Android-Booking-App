package app.core.logout.logout;

import com.facebook.login.LoginManager;

import org.greenrobot.eventbus.EventBus;

import app.core.BaseOutputInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.CheckOutRequestBody;
import app.core.checkout.entity.PhoneVerificationException;
import app.core.checkout.interactor.CheckoutInteractor;
import app.core.coachmark.put.PutShouldShowReminderInteractor;
import app.core.coachmark.tab.put.PutTabCoachMarkInteractor;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.profile.drawer.logout.events.LogOutCallbackEvent;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.logout.LogoutAndroidAccountGateway;
import app.gateway.profile.cache.clear.ClearProfileRealTimeGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.zendesk.put.PutZendeskIdentityInfoGateway;
import app.qamode.log.LogToFileHandler;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

public class LogoutInteractor implements BaseOutputInteractor<Observable<Token>> {
    private final LogoutAndroidAccountGateway logoutGateway;
    private final AndroidAuthTokenGateway authTokenGateway;
    private final ClearProfileRealTimeGateway clearProfileRealTimeGateway;
    private final PutShouldShowReminderInteractor putShouldShowReminderInteractor;
    private final CheckoutInteractor checkoutInteractor;
    private final PutZendeskIdentityInfoGateway putZendeskIdentityInfoGateway;
    private final PutTabCoachMarkInteractor interactor;


    public LogoutInteractor(BaseActivity activity) {
        checkoutInteractor = new CheckoutInteractor(activity);
        logoutGateway = new LogoutAndroidAccountGateway(activity);
        authTokenGateway = new AndroidAuthTokenGateway(activity);
        clearProfileRealTimeGateway = new ClearProfileRealTimeGateway();
        putShouldShowReminderInteractor = new PutShouldShowReminderInteractor();
        putZendeskIdentityInfoGateway = new PutZendeskIdentityInfoGateway(activity);
        interactor = new PutTabCoachMarkInteractor();
    }

    @Override public Observable<Token> process() {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(val -> (long) val)
                .concatMap(this::findLogOutPath);
    }

    private Observable<Token> findLogOutPath(long checkInId) {
        if (checkInId == 0)
            return logOut();
        else
            return logOutAfterCheckOut(checkInId);
    }

    private Observable<Token> logOutAfterCheckOut(long checkInId) {
        EventBus.getDefault().post(new LogOutCallbackEvent(true));
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - auto-check out by LogOut, id-" + checkInId);
        return checkoutInteractor.process(new CheckOutRequestBody(checkInId, false))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException && ((HttpException) throwable).code() == 409 ||
                            throwable instanceof PhoneVerificationException)
                        return Observable.just(new CheckInResponse());
                    else
                        return Observable.error(throwable);
                }).concatMap(value -> logOut());
    }

    private Observable<Token> logOut() {
        EventBus.getDefault().post(new LogOutCallbackEvent(false));
        return logoutGateway.logout()
                .doOnNext(o -> LoginManager.getInstance().logOut())
                .concatMap(o -> clearCache())
                .concatMap(object -> authTokenGateway.get());
    }

    private Observable<Boolean> clearCache() {
        final int valueForClearCacheFlow = 0;

        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, valueForClearCacheFlow))
                .concatMap(emptyId -> interactor.process(false))
                .map(emptyId -> QorumSharedCache.checkTimeLeftToRide().save(BaseCacheType.INT, valueForClearCacheFlow))
                .map(emptyId -> QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, valueForClearCacheFlow))
                .map(emptyId -> QorumSharedCache.checkUserId().save(BaseCacheType.LONG, valueForClearCacheFlow))
                .concatMap(emptyId -> putZendeskIdentityInfoGateway.put(valueForClearCacheFlow))
                .map(emptyId -> QorumSharedCache.checkFBUserId().save(BaseCacheType.LONG, valueForClearCacheFlow))
                .concatMap(emptyId -> putShouldShowReminderInteractor.process(valueForClearCacheFlow))
                .map(emptyId -> QorumSharedCache.checkFBPhoto().save(BaseCacheType.BOOLEAN, false))
                .map(emptyId -> QorumSharedCache.checkProfilePhotoVal().save(BaseCacheType.BOOLEAN, false))
                .concatMap(emptyId -> Observable.just(QorumSharedCache.checkUberFreeRideMark()
                        .save(BaseCacheType.BOOLEAN, false)))
                .concatMap(emptyId -> clearProfileRealTimeGateway.clear())
                .concatMap(emptyResponse -> Observable.just(true));
    }
}
