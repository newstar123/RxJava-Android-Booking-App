package app.gateway.profile.cache.get;

import app.core.profile.get.entity.ProfileModel;
import app.gateway.profile.cache.holder.ProfileRealTimeHolder;
import rx.Observable;

public class GetProfileRealTimeGateway implements GetProfileInRealTimeGateway {

    @Override public Observable<ProfileModel> get() {
        return ProfileRealTimeHolder.getProfile();
    }
}
