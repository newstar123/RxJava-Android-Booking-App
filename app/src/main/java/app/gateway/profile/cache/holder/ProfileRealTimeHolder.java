package app.gateway.profile.cache.holder;

import app.core.profile.get.entity.ProfileModel;
import rx.Observable;

public class ProfileRealTimeHolder {
    private static ProfileModel profile;
    private static final Object profileLock = new Object();

    public static Observable<ProfileModel> setProfile(ProfileModel profile){
        synchronized (profileLock){
            ProfileRealTimeHolder.profile = profile;
           return getProfile();
        }
    }

    public static Observable<ProfileModel> getProfile(){
        synchronized (profileLock){
          return Observable.just(ProfileRealTimeHolder.profile);
        }
    }

    public static void clear(){
        synchronized (profileLock){
            profile = null;
        }
    }
}
