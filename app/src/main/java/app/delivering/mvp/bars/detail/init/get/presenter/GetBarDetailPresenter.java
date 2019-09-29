package app.delivering.mvp.bars.detail.init.get.presenter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.R;
import app.core.bars.detail.interactor.GetItemBarInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import rx.Observable;

public class GetBarDetailPresenter extends BasePresenter<Long, Observable<BarDetailModel>> {
    private static final String EMPTY_BAR_VIEW = "[object Object]";
    private GetItemBarInteractor interactor;

    public GetBarDetailPresenter(BaseActivity activity) {
        super(activity);
        interactor = new GetItemBarInteractor(getActivity());
    }

    @Override public Observable<BarDetailModel> process(Long barId) {
        return interactor.process(barId)
                .concatMap(this::convertToBarDetailsModel);
    }

    private Observable<BarDetailModel> convertToBarDetailsModel(BarModel barModel) {
        return Observable.create(subscriber -> {
            BarDetailModel detailModel = new BarDetailModel();
            detailModel.setBarModel(barModel);
            detailModel.setRouting(getFormatRouting(barModel.getDistMiles(), barModel.getNeighborhood()));
            detailModel.setDiscountText(getFormatDiscount(barModel.getCurrentDiscount()));
            detailModel.setBarImageUrls(getListExceptEmptyUrl(barModel.getImageUrls()));
            detailModel.setBarDescriptions(parseDescription(barModel));
            subscriber.onNext(detailModel);
            subscriber.onCompleted();
        });
    }

    private String getFormatRouting(double distMiles, String neighborhood) {
        int distance = (int) Math.round(distMiles);
        return String.format(getActivity().getString(R.string.value_miles_to_neighborhood), String.valueOf(distance), neighborhood);
    }

    private String getFormatDiscount(double value) {
        int discount = (int)Math.round(value);
        return String.format(getActivity().getString(R.string.value_percent_off), String.valueOf(discount));
    }

    private List<String> getListExceptEmptyUrl(List<String> imageUrls) {
        ArrayList<String> newListUrls = new ArrayList<>();
        if (imageUrls == null)
            return newListUrls;
        for (String url : imageUrls)
            if (!TextUtils.isEmpty(url) && !url.equals(EMPTY_BAR_VIEW))
                newListUrls.add(url);
        if (newListUrls.isEmpty())
            newListUrls.add(EMPTY_BAR_VIEW);
        return newListUrls;
    }

    private List<String> parseDescription(BarModel barModel) {
        String description = barModel.getDescription();
        String[] descriptions = description.split("\\u002A\\u002A\\u002A");
        return Arrays.asList(descriptions);
    }
}
