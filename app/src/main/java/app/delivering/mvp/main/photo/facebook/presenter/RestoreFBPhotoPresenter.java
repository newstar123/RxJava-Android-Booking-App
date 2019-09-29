package app.delivering.mvp.main.photo.facebook.presenter;

import android.graphics.Bitmap;

import app.core.facebook.profile.interactor.RestoreFBPhotoInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class RestoreFBPhotoPresenter extends BaseOutputPresenter<Observable<Bitmap>> {
    private final RestoreFBPhotoInteractor restoreFBPhotoInteractor;

    public RestoreFBPhotoPresenter(BaseActivity activity) {
        super(activity);
        restoreFBPhotoInteractor = new RestoreFBPhotoInteractor(getActivity());
    }

    @Override public Observable<Bitmap> process() {
        return restoreFBPhotoInteractor.process();
    }
}
