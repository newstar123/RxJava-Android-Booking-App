package app.core.facebook.visibility.gateway;

import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import rx.Observable;

public interface PutRestFacebookVisibilityGateway {
    Observable<FacebookVisibilityModel> put(FacebookVisibilityModel model);
}
