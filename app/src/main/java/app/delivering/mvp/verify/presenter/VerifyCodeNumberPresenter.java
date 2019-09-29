package app.delivering.mvp.verify.presenter;

import java.util.Calendar;

import app.core.verify.interactor.code.VerifyCodeInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.verify.binder.init.LockButtonTimerInterface;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import rx.Observable;

public class VerifyCodeNumberPresenter extends BasePresenter<CheckPhoneRequestVerification,
        Observable<CheckPhoneResponseVerification>> implements LockButtonTimerInterface {

    private VerifyCodeInteractor verifyCodeInteractor;

    public VerifyCodeNumberPresenter(BaseActivity baseActivity) {
        super(baseActivity);
        verifyCodeInteractor = new VerifyCodeInteractor(baseActivity);
    }

    @Override
    public Observable<CheckPhoneResponseVerification> process(CheckPhoneRequestVerification checkPhoneRequestVerification) {
        return verifyCodeInteractor.process(checkPhoneRequestVerification);
    }

    @Override
    public void saveDeviceTimeInSec(int timerVal) {
        QorumSharedCache.checkTimeForCodeVerif().save(BaseCacheType.INT, timerVal);
    }

    @Override
    public int getCurrTimeValInSec() {
        Calendar rightNow = Calendar.getInstance();
        final int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        final int min = rightNow.get(Calendar.MINUTE);
        final int sec = rightNow.get(Calendar.SECOND);
        return (hour * 60 * 60) + (min * 60) + sec;
    }

    @Override
    public int getSavedDeviceTimeInSec() {
        return QorumSharedCache.checkTimeForCodeVerif().get(BaseCacheType.INT);
    }

    @Override
    public void saveTimerValInSec(int timerVal) {
        QorumSharedCache.checkTimerValInSecForCodeVerif().save(BaseCacheType.INT, timerVal);
    }

    @Override
    public int getSavedTimerValInSec() {
        return QorumSharedCache.checkTimerValInSecForCodeVerif().get(BaseCacheType.INT);
    }
}
