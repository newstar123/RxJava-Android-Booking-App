package app.delivering.mvp.tab.discount.presenter;

import android.text.Spanned;

import app.R;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.get.entity.RideDiscount;
import app.core.checkin.user.get.entity.RideSafeDiscountStatus;
import app.core.checkin.user.get.entity.RideSafeDiscountTime;
import app.core.ride.delayed.discount.interactor.GetRideDiscountInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.tab.discount.model.DiscountModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class TabDiscountInitPresenter extends BasePresenter<GetCheckInsResponse, Observable<DiscountModel>> {

    private final GetRideDiscountInteractor getRideDiscountInteractor;

    public TabDiscountInitPresenter(BaseActivity activity) {
        super(activity);
        getRideDiscountInteractor = new GetRideDiscountInteractor();
    }

    @Override
    public Observable<DiscountModel> process(GetCheckInsResponse checkIn) {
        return Observable.just(checkIn)
                .map(this::getTimeLeftValue)
                .map(val -> QorumSharedCache.checkTimeLeftToRide().save(BaseCacheType.INT, val))
                .concatMap(val -> setUpDiscountLogic(checkIn))
                .concatMap(discountModel -> setUpTabLogic(discountModel, checkIn));
    }

    private int getTimeLeftValue(GetCheckInsResponse checkIn) {
        RideSafeDiscountStatus rideSafeDiscount = checkIn.getRideSafeDiscountStatus();
        RideSafeDiscountTime rideSafeDiscountTime = rideSafeDiscount.getTime();
        return (int) rideSafeDiscountTime.getTimeLeftToRideDiscount();
    }

    private Observable<DiscountModel> setUpDiscountLogic(GetCheckInsResponse checkIn) {
        long timeLeftToRideDiscount = checkIn.getRideSafeDiscountStatus().getTime().getTimeLeftToRideDiscount();
        String estimateTime = convertTimeToMinSec(timeLeftToRideDiscount);

        if (timeLeftToRideDiscount <= 0 && checkIn.getRideDiscount().getDiscountValue() > 0)
            QorumSharedCache.checkSavedFreeRideValue().save(BaseCacheType.FLOAT, checkIn.getRideDiscount().getDiscountValue());

        DiscountModel discountModel = new DiscountModel();
        discountModel.setMaxDiscount(getCorrectDiscount(checkIn.getRideDiscount()));
        discountModel.setTimerValue(estimateTime);
        discountModel.setTime(timeLeftToRideDiscount);

        setTitleText(discountModel, timeLeftToRideDiscount, estimateTime);

        return getRideDiscountInteractor.process()
                .doOnNext(discountModel::setFreeRideValFromCache)
                .map(isFreeRideAvailable -> discountModel);
    }

    private Observable<DiscountModel> setUpTabLogic(DiscountModel discountModel, GetCheckInsResponse checkIn) {
        boolean isDiscountAlreadyExists = discountModel.isFreeRideValFromCache() > 0d;
        boolean isFreeRideAvailable = (checkIn.getTotals().getSubTotal() >= checkIn.getRideSafeDiscountStatus().getMinSpendToRideDiscount());

        discountModel.setFreeRideAndNoDiscount(isFreeRideAvailable && !isDiscountAlreadyExists);
        discountModel.setNoFreeRideAndNoDiscount(!isFreeRideAvailable && !isDiscountAlreadyExists);
        discountModel.setFreeRideAndDiscount(isFreeRideAvailable && isDiscountAlreadyExists);
        discountModel.setDiscountAndNoFreeRide(!isFreeRideAvailable && isDiscountAlreadyExists);
        return Observable.just(discountModel);
    }

    private String convertTimeToMinSec(long timeLeftToRideDiscount) {
        return String.format(getActivity().getString(R.string.tab_time_format),
                timeLeftToRideDiscount / 60, timeLeftToRideDiscount % 60);
    }

    private double getCorrectDiscount(RideDiscount rideDiscount) {
        return (rideDiscount == null) ? 0 : rideDiscount.getDiscountValue();
    }

    private void setTitleText(DiscountModel model, long timeLeftToRideDiscount, String estimateTime) {
        String freeTitle;
        if (timeLeftToRideDiscount > 0)
            freeTitle = getActivity().getString(R.string.tab_timer_title, estimateTime);
        else
            freeTitle = getActivity().getString(R.string.tab_timer_stop_title, String.valueOf(model.getMaxDiscount()));

        Spanned spannedFreeTitle = formatHtmlToSpanned(freeTitle);
        model.setTimeTitle(spannedFreeTitle);
    }
}
