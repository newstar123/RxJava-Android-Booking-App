package app.delivering.mvp.bars.detail.init.ubercall.init.presenter;

import com.google.android.gms.maps.model.LatLng;

import app.R;
import app.core.uber.estimate.price.entity.UberPriceEstimatesResponse;
import app.core.uber.estimate.price.interactor.GetUberEstimatesInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.ubercall.init.model.GetUberEstimationsModel;
import rx.Observable;

public class GetUberEstimationPresenter extends BasePresenter<LatLng, Observable<GetUberEstimationsModel>> {
    private GetUberEstimatesInteractor uberEstimatesInteractor;

    public GetUberEstimationPresenter(BaseActivity activity) {
        super(activity);
        uberEstimatesInteractor = new GetUberEstimatesInteractor(getActivity());
    }

    @Override public Observable<GetUberEstimationsModel> process(LatLng latLng) {
        return uberEstimatesInteractor.process(latLng)
                .concatMap(this::createStingEstimation);
    }

    private Observable<GetUberEstimationsModel> createStingEstimation(UberPriceEstimatesResponse uberEstimatesModel) {
        return Observable.create(subscriber -> {
            GetUberEstimationsModel model = new GetUberEstimationsModel();
            int time = Math.round(uberEstimatesModel.getPrices().get(0).getDuration()/60);
            String estimateCost = getCorrectEstimate(uberEstimatesModel);
            String estimateTime = String.format(getActivity().getString(R.string.minutes_with_coma), time);
            String estimation = String.format(getActivity().getString(R.string.value_space_value), estimateTime, estimateCost);
            String fullEstimationText = String.format(getActivity().getString(R.string.to_the_venue), estimation);
            model.setUberTripEstimation(fullEstimationText);
            subscriber.onNext(model);
            subscriber.onCompleted();
        });
    }

    private String getCorrectEstimate(UberPriceEstimatesResponse uberEstimatesModel) {
        String estimate = uberEstimatesModel.getPrices().get(0).getSummeryEstimate();
        String[] estimates = estimate.split("-");
        if (estimates.length > 0 && !estimates[0].contains(estimates[1]))
            return estimate;
        else
        return estimates[0];
    }
}
