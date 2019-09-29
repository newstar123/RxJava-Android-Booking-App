package app.core.profile.get.interactor;

import android.content.Context;
import android.text.TextUtils;

import app.core.BaseOutputInteractor;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.gateway.GetProfileGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.profile.cache.put.PutProfileRealTimeGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetProfileInteractor implements BaseOutputInteractor<Observable<ProfileModel>> {
    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetProfileGateway getProfileRestGateway;
    private final PutProfileRealTimeGateway putProfileRealTimeGateway;

    public GetProfileInteractor(Context context, GetProfileGateway getProfileGateway) {
        getProfileRestGateway = getProfileGateway;
        networkPermissionGateway = new CheckNetworkPermissionGateway(context);
        putProfileRealTimeGateway = new PutProfileRealTimeGateway();
    }

    @Override public Observable<ProfileModel> process() {
        return networkPermissionGateway.check()
                .concatMap(isOk -> getProfileRestGateway.get())
                .concatMap(this::updateFacebookParams)
                .map(this::checkVerification)
                .concatMap(putProfileRealTimeGateway::put);
    }

    private Observable<ProfileModel> updateFacebookParams(ProfileModel profileModel) {
        boolean isFacebookVisible = false;
        if (!TextUtils.isEmpty(profileModel.getFacebookVisible()) && profileModel.getFacebookVisible().equals("on"))
            isFacebookVisible = true;
        return Observable.just(QorumSharedCache.checkSettingsFB().save(BaseCacheType.BOOLEAN, isFacebookVisible))
                .map(aLong -> QorumSharedCache.checkFBUserId().save(BaseCacheType.LONG, profileModel.getFacebookId()))
                .concatMap(aBoolean -> Observable.just(profileModel));
    }

    private ProfileModel checkVerification(ProfileModel profileModel) {
        profileModel.setAccountVerified(profileModel.getIsEmailVerified() == 1 && profileModel.getIsPhoneVerified() == 1);
        return profileModel;
    }
}
