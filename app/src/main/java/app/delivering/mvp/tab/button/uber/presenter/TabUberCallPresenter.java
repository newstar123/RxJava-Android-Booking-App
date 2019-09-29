package app.delivering.mvp.tab.button.uber.presenter;

import app.core.checkout.entity.PhoneVerificationException;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.profile.get.GetProfileRestGateway;
import rx.Observable;

public class TabUberCallPresenter extends BaseOutputPresenter<Observable<ProfileModel>> {
    private final GetProfileInteractor getProfileInteractor;


    public TabUberCallPresenter(BaseActivity activity) {
        super(activity);
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
    }

    @Override
    public Observable<ProfileModel> process() {
        return getProfileInteractor.process()
                .map(profileModel -> {
                    if (profileModel.getIsPhoneVerified() == 0)
                        throw new PhoneVerificationException();
                    return profileModel;
                });
    }
}
