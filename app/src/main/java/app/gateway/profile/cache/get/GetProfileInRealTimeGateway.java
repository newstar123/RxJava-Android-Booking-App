package app.gateway.profile.cache.get;

import app.core.profile.get.entity.ProfileModel;
import rx.Observable;

public interface GetProfileInRealTimeGateway {
    Observable<ProfileModel> get();
}
