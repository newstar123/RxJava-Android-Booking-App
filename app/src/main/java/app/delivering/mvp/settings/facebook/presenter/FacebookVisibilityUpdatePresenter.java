package app.delivering.mvp.settings.facebook.presenter;

import app.core.settings.cache.facebook.put.PutSharedFacebookVisibilityInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.settings.facebook.model.FacebookVisibilityResponse;
import rx.Observable;

public class FacebookVisibilityUpdatePresenter extends BasePresenter<FacebookVisibilityResponse, Observable<FacebookVisibilityResponse>> {
    private PutSharedFacebookVisibilityInteractor facebookVisibilityInteractor;

    public FacebookVisibilityUpdatePresenter(BaseActivity activity) {
        super(activity);
        facebookVisibilityInteractor = new PutSharedFacebookVisibilityInteractor(getActivity());
    }

    @Override public Observable<FacebookVisibilityResponse> process(FacebookVisibilityResponse value) {
        return facebookVisibilityInteractor.process(value);
    }
}
