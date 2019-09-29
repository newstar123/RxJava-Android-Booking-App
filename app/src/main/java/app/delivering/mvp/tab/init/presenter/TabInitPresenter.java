package app.delivering.mvp.tab.init.presenter;

import app.core.checkin.cacheid.GetSharedCheckInIdInteractor;
import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.get.entity.RideDiscount;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.coachmark.tab.get.GetTabCoachMarkInteractor;
import app.core.ride.delayed.discount.interactor.GetRideDiscountInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.tab.init.model.GetCheckInRequestModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class TabInitPresenter extends BasePresenter<GetCheckInRequestModel, Observable<CheckInResponse>> {
    private GetCheckInInteractor checkInByIdInterface;
    private final GetTabCoachMarkInteractor getTabCoachMarkInteractor;
    private final GetRideDiscountInteractor getRideDiscountInteractor;
    private final GetSharedCheckInIdInteractor getSharedCheckInIdInteractor;
    private CheckInResponse checkInResponse;


    public TabInitPresenter(BaseActivity activity) {
        super(activity);
        getTabCoachMarkInteractor = new GetTabCoachMarkInteractor();
        checkInByIdInterface = new GetCheckInByIdInteractor(getActivity());
        getRideDiscountInteractor = new GetRideDiscountInteractor();
        getSharedCheckInIdInteractor = new GetSharedCheckInIdInteractor();
    }

    @Override
    public Observable<CheckInResponse> process(GetCheckInRequestModel requestModel) {
        return getCheckInInfo(requestModel)
                .doOnNext(paramCheckInResponse -> this.checkInResponse = paramCheckInResponse)
                .concatMap(checkInResponse -> getTabCoachMarkInteractor.process())
                .doOnNext(this::setUpMarkParam)
                .concatMap(val -> isTabAlreadyClosed(checkInResponse))
                .concatMap(this::checkFreeRides);
    }

    private void setUpMarkParam(boolean isMarkAlreadyDisplayed) {
        checkInResponse.setMarkAlreadyDisplayed(isMarkAlreadyDisplayed);
    }

    private Observable<CheckInResponse> getCheckInInfo(GetCheckInRequestModel requestModel) {
        return requestModel.isForceRequest() ?
                checkInByIdInterface.forceLoad(requestModel.getId())
                : checkInByIdInterface.process(requestModel.getId());
    }

    private Observable<CheckInResponse> checkFreeRides(CheckInResponse checkInResponse) {
        return Observable.zip(Observable.just((boolean)QorumSharedCache.checkFreeRideWarning().get(BaseCacheType.BOOLEAN)),
                getRideDiscountInteractor.process(), Observable.just((long)QorumSharedCache.checkLastFreeRideCheckInId().get(BaseCacheType.LONG)),
                (isErrorAlreadyShown, prevDiscountVal, sharedCheckInId) -> {

            checkInResponse.setDiscountAvailable(isErrorAlreadyShown || (prevDiscountVal > 0d ||
                    isCurrentDiscountAvailable(checkInResponse.getCheckin().getRideDiscount())));
            boolean isFreeRideAvailable = IsFreeRideAvailable(sharedCheckInId, checkInResponse.getCheckin().getId());
            checkInResponse.setFreeRideAlreadyAvailable(isFreeRideAvailable);
            return true;
        })
                .map(val -> QorumSharedCache.checkFreeRideWarning().save(BaseCacheType.BOOLEAN,false))
                .map(val -> checkInResponse);
    }

    private boolean IsFreeRideAvailable(long cacheCheckInId, long responseCheckInId) {
        return getSavedRideDiscount() > 0 && !getSavedRidesDialogState() && cacheCheckInId != responseCheckInId;
    }

    private float getSavedRideDiscount() {
        return QorumSharedCache.checkSavedFreeRideValue().get(BaseCacheType.FLOAT);
    }

    private boolean getSavedRidesDialogState() {
        return QorumSharedCache.checkFreeRideDialogAlreadyShown().get(BaseCacheType.BOOLEAN);
    }

    private boolean isCurrentDiscountAvailable(RideDiscount rideDiscount) {
        return (rideDiscount != null && rideDiscount.getDiscountValue() > 0);
    }

    private Observable<CheckInResponse> isTabAlreadyClosed(CheckInResponse checkInResponse) {
        return checkInResponse.getCheckin().getCheckoutTime() == null ?
                Observable.just(checkInResponse) : setUpOptionsForClosedTab(checkInResponse);
    }

    private Observable<CheckInResponse> setUpOptionsForClosedTab(CheckInResponse checkInResponse) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getId()))
                .map(checkInId -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0)))
                .map(checkInBarId -> QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getId()))
                .map(checkInBarId -> checkInResponse);
    }
}
