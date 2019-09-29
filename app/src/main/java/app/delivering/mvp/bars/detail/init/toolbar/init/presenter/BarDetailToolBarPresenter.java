package app.delivering.mvp.bars.detail.init.toolbar.init.presenter;

import android.text.TextUtils;

import java.util.ArrayList;

import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewModel;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import rx.Observable;

public class BarDetailToolBarPresenter extends BasePresenter<BarModel, Observable<AboutBarViewModel>> {
    private static final String APPROVED_STATUS = "approved";

    public BarDetailToolBarPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<AboutBarViewModel> process(BarModel barModel) {
        return Observable.create(subscriber -> {
            ArrayList<AboutBarViewTypeModel> urls = getUrlsList(barModel);
            String promotion = barModel.getSpecialNoticeStatus().equalsIgnoreCase(APPROVED_STATUS) ? barModel.getSpecialNotice() : "";
            AboutBarViewModel result = new AboutBarViewModel(urls, barModel.getVideoUrl(), promotion);
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }

    private ArrayList<AboutBarViewTypeModel> getUrlsList(BarModel barModel) {
        ArrayList<AboutBarViewTypeModel> viewTypeModels = new ArrayList<>();
        String thumbUrl = barModel.getVideoThumbUrl();
        String videoUrl = barModel.getVideoUrl();
        if (!TextUtils.isEmpty(videoUrl)){
            AboutBarViewTypeModel videoUrlModel = new AboutBarViewTypeModel(TextUtils.isEmpty(thumbUrl) ? barModel.getBackgroundImageUrl() : thumbUrl);
            videoUrlModel.setVideoUrl(true);
            viewTypeModels.add(videoUrlModel);
        }
        viewTypeModels.add(new AboutBarViewTypeModel(barModel.getBackgroundImageUrl()));
        for(String url : barModel.getImageUrls())
            viewTypeModels.add(new AboutBarViewTypeModel(url));
        return viewTypeModels;
    }
}
