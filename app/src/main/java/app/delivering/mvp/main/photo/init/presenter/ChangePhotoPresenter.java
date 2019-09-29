package app.delivering.mvp.main.photo.init.presenter;

import java.io.File;

import app.core.profile.photo.post.entity.PutProfilePhotoModel;
import app.core.profile.photo.post.interactor.PutProfilePhotoInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.main.photo.init.events.CropResultEvent;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class ChangePhotoPresenter extends BasePresenter<CropResultEvent, Observable<Boolean>> {
    private PutProfilePhotoInteractor photoInteractor;

    public ChangePhotoPresenter(BaseActivity activity) {
        super(activity);
        photoInteractor = new PutProfilePhotoInteractor(activity);
    }

    @Override public Observable<Boolean> process(CropResultEvent event) {
        PutProfilePhotoModel putProfilePhoto = new PutProfilePhotoModel();
        putProfilePhoto.setFile(new File(event.getPath()));
        return photoInteractor.process(putProfilePhoto)
                .map(s -> QorumSharedCache.checkFBPhoto().save(BaseCacheType.BOOLEAN, false));
    }
}
