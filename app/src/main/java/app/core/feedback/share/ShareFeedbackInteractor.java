package app.core.feedback.share;

import app.core.BaseInteractor;
import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.core.bars.image.get.interactor.GetImageBitmapInteractor;
import app.core.bars.image.put.interactor.PutImageInteractor;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.feedback.send.model.ShareFeedbackResponse;
import app.gateway.profile.get.GetProfileRestGateway;
import rx.Observable;

public class ShareFeedbackInteractor implements BaseInteractor<ImageBitmapRequest, Observable<ShareFeedbackResponse>>{
    private GetProfileInteractor getProfileInteractor;
    private PutImageInteractor putImageInteractor;
    private GetImageBitmapInteractor getImageBitmapInteractor;

    public ShareFeedbackInteractor(BaseActivity activity){
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
        putImageInteractor = new PutImageInteractor(activity);
        getImageBitmapInteractor = new GetImageBitmapInteractor(activity);
    }

    @Override public Observable<ShareFeedbackResponse> process(ImageBitmapRequest request) {
        return Observable.zip(getProfileInteractor.process(), saveImage(request), this::combineResponses);
    }

    private Observable<ImageBitmapResponse> saveImage(ImageBitmapRequest request) {
        return getImageBitmapInteractor.process(request)
        .concatMap(response -> putImageInteractor.process(response));
    }

    private ShareFeedbackResponse combineResponses(ProfileModel profileModel, ImageBitmapResponse imageBitmapResponse) {
        ShareFeedbackResponse response = new ShareFeedbackResponse();
        response.setProfileRefCode(profileModel.getReferralCode().getBranchLink());
        response.setShareImage(imageBitmapResponse.getBitmap());
        response.setPath(imageBitmapResponse.getPath());
        return response;
    }
}
