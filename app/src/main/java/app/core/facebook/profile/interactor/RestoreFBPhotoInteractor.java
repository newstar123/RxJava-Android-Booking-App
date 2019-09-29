package app.core.facebook.profile.interactor;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.core.BaseOutputInteractor;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.core.login.facebook.gateway.FacebookProfileGateway;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.core.profile.photo.post.entity.PutProfilePhotoModel;
import app.core.profile.photo.post.interactor.PutProfilePhotoInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.bars.image.put.PutImageToFileGateway;
import app.gateway.facebook.profile.FacebookGraphProfileGateway;
import app.gateway.profile.get.GetProfileRestGateway;
import app.gateway.profile.photo.facebook.GetFacebookPhotoGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class RestoreFBPhotoInteractor implements BaseOutputInteractor<Observable<Bitmap>> {

    private FacebookProfileGateway facebookProfileGateway;
    private final GetProfileInteractor profileInteractor;
    private GetFacebookPhotoGateway photoGateway;
    private PutProfilePhotoInteractor photoInteractor;
    private final PutImageToFileGateway putImageToFileGateway;


    public RestoreFBPhotoInteractor(BaseActivity activity) {
        this.photoGateway = new GetFacebookPhotoGateway();
        this.photoInteractor = new PutProfilePhotoInteractor(activity);
        profileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
        putImageToFileGateway = new PutImageToFileGateway();
        facebookProfileGateway = new FacebookGraphProfileGateway();
    }

    @Override public Observable<Bitmap> process() {
        return profileInteractor.process()
                .concatMap(this::checkFBProfilePicValue)
                .concatMap(profileModel -> photoGateway.get(profileModel.getFacebookId()))
                .concatMap(this::savePhoto);
    }

    private Observable<ProfileModel> checkFBProfilePicValue(ProfileModel profileModel) {
        return facebookProfileGateway.get()
                .map(FBresponse -> QorumSharedCache.checkFBPhoto().save(BaseCacheType.BOOLEAN, FBresponse.getPictureData().getPicture().isSilhouette()))
                .concatMap(s -> Observable.just(profileModel));
    }

    private Observable<Bitmap> savePhoto(Bitmap bitmap) {
        return Observable.just(bitmap)
                .concatMap(bitmap1 -> putImageToFileGateway.put(getBitmapRequest(bitmap)))
                .concatMap(response -> photoInteractor.process(new PutProfilePhotoModel(response.getFile())))
                .map(response -> bitmap);
    }

    private ImageBitmapResponse getBitmapRequest(Bitmap bitmap) {
        ImageBitmapResponse response = new ImageBitmapResponse();
        response.setBitmap(bitmap);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        response.setName(imageFileName);
        return response;
    }

}
