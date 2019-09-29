package app.gateway.profile.cache.put;

import app.core.profile.get.entity.ProfileModel;
import app.gateway.profile.cache.holder.ProfileRealTimeHolder;
import rx.Observable;

public class PutProfileRealTimeGateway implements PutProfileInRealTimeGateway {
    @Override public Observable<ProfileModel> put(ProfileModel profileModel) {
        return ProfileRealTimeHolder.setProfile(profileModel);
    }
}
