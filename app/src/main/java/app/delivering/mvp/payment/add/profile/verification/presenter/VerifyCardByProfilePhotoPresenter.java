package app.delivering.mvp.payment.add.profile.verification.presenter;

import android.text.TextUtils;

import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.get.entity.ProfileModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.profile.cache.get.GetProfileRealTimeGateway;
import rx.Observable;

public class VerifyCardByProfilePhotoPresenter extends BaseOutputPresenter<Observable<EmptyResponse>> {
    private final GetProfileRealTimeGateway getProfileRealTimeGateway;

    public VerifyCardByProfilePhotoPresenter(BaseActivity activity) {
        super(activity);
        getProfileRealTimeGateway = new GetProfileRealTimeGateway();
    }

    @Override public Observable<EmptyResponse> process() {
        return getProfileRealTimeGateway.get()
                .map(this::checkProfilePhotoEmpty);
    }

    private EmptyResponse checkProfilePhotoEmpty(ProfileModel profileModel) {
        if (TextUtils.isEmpty(profileModel.getImageUrl()))
            return new EmptyResponse();
        else
            throw new RuntimeException();
    }
}
