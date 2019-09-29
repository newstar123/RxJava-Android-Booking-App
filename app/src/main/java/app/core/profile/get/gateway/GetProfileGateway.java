package app.core.profile.get.gateway;

import app.core.profile.get.entity.ProfileModel;
import rx.Observable;

public interface GetProfileGateway {
    Observable<ProfileModel> get();
}
