package app.core.facebook.visibility.interactor;

import app.core.BaseInteractor;
import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import app.core.facebook.visibility.entity.UpdateVisibilityPeriodException;
import app.delivering.component.BaseActivity;
import app.gateway.facebook.visibility.PutVisibilityGateway;
import rx.Observable;

public class PutRestFacebookVisibilityInteractor implements BaseInteractor<FacebookVisibilityModel, Observable<FacebookVisibilityModel>> {
    private PutVisibilityGateway gateway;
    private static final String VISIBILITY_LIMBO_STATE = "limbo";

    public PutRestFacebookVisibilityInteractor(BaseActivity activity) {
        gateway = new PutVisibilityGateway(activity);
    }

    @Override public Observable<FacebookVisibilityModel> process(FacebookVisibilityModel model) {
        return gateway.put(model)
                .map(this::checkUpdateResult);
    }

    private FacebookVisibilityModel checkUpdateResult(FacebookVisibilityModel response) {
        if (response.getFacebookVisible().equals(VISIBILITY_LIMBO_STATE))
            throw new UpdateVisibilityPeriodException();
        else
            return response;
    }
}
