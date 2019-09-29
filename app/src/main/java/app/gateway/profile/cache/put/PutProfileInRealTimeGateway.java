package app.gateway.profile.cache.put;

import app.core.profile.get.entity.ProfileModel;
import rx.Observable;

public interface PutProfileInRealTimeGateway {
    Observable<ProfileModel> put(ProfileModel profile);
}
