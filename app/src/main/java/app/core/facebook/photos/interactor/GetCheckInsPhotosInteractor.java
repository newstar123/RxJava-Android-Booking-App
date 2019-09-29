package app.core.facebook.photos.interactor;

import java.util.ArrayList;
import java.util.List;

import app.core.BaseInteractor;
import app.core.checkin.friends.entity.CheckinsFriendModel;
import app.core.checkin.friends.entity.WhoseHereModel;
import app.core.checkin.friends.interactor.GetCheckInsInteractor;
import app.core.facebook.friends.entity.FacebookFriendModel;
import app.core.facebook.friends.interactor.GetFacebookFriendsInteractor;
import app.core.facebook.mock.entity.MockPhotosResponse;
import app.core.facebook.mock.interactor.GetMockPhotosInteractor;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.profile.get.GetProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetCheckInsPhotosInteractor implements BaseInteractor<Long,Observable<WhoseHereModel>> {
    private GetCheckInsInteractor checkInsInteractor;
    private GetFacebookFriendsInteractor friendsInteractor;
    private GetMockPhotosInteractor getMockPhotosInteractor;
    private final GetProfileInteractor getProfileInteractor;


    public GetCheckInsPhotosInteractor(BaseActivity activity) {
        checkInsInteractor = new GetCheckInsInteractor(activity);
        friendsInteractor = new GetFacebookFriendsInteractor(activity);
        getMockPhotosInteractor = new GetMockPhotosInteractor(activity);
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
    }

    @Override public Observable<WhoseHereModel> process(Long barId) {
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(id -> checkLoadPhotosType(id, barId));
    }

    private Observable<WhoseHereModel> checkLoadPhotosType(Long id, Long barId) {
        return Observable.just((boolean)QorumSharedCache.checkSettingsFB().get(BaseCacheType.BOOLEAN))
                .concatMap(isVisible -> checkPhotoType(id, barId, isVisible));
    }

    private Observable<WhoseHereModel> checkPhotoType(Long id, Long barId, Boolean isVisible) {
        if (id > 0)
            return getFriendsCheckins(barId, isVisible);
        else
            return getConvertedMockPhoto(false, isVisible);
    }

    private Observable<WhoseHereModel> getFriendsCheckins(Long barId, Boolean isVisible) {
        if (isVisible)
            return Observable.zip(friendsInteractor.process(), checkInsInteractor.process(barId),
                                  getFacebookUserId(),
                                  (facebookFriends, checkInsFriends, userId) -> {
                                      ArrayList<CheckinsFriendModel> commonModels = new ArrayList<>();
                                      ArrayList<CheckinsFriendModel> checkInsModels = new ArrayList<>();
                                      WhoseHereModel hereModel = new WhoseHereModel();
                                      hereModel.setAuthorized(true);
                                      hereModel.setFBVisible(true);

                                      for (CheckinsFriendModel checkin : checkInsFriends) {
                                          if (checkin.getFacebookId() != userId) {
                                              if (isFriendsCheckIn(checkin, facebookFriends))
                                                  commonModels.add(checkin);
                                              else
                                                  checkInsModels.add(checkin);
                                          }
                                      }

                                      hereModel.setCheckIns(checkInsModels);
                                      hereModel.setFriends(commonModels);
                                      return hereModel;
                                  });
        else
            return getConvertedMockPhoto(true, isVisible);
    }

    private boolean isFriendsCheckIn(CheckinsFriendModel checkin, List<FacebookFriendModel> facebookFriends) {
        for (FacebookFriendModel friend : facebookFriends)
            if (checkin.getFacebookId() == friend.getId())
                return true;
        return false;
    }

    private Observable<Long> getFacebookUserId() {
        return Observable.just((long)QorumSharedCache.checkFBUserId().get(BaseCacheType.LONG))
                .concatMap(this::checkTruthId);
    }

    private Observable<Long> checkTruthId(Long id) {
        if (id > 0)
            return Observable.just(id);
        else
            return getProfileInteractor.process()
                    .concatMap(profileModel -> Observable.just(profileModel.getId()));
    }

    private Observable<WhoseHereModel> getConvertedMockPhoto(boolean isAuthenticate, boolean isVisible) {
        return getMockPhotosInteractor.process()
                .concatMap(response -> convertPhotosToFriendsModel(response, isAuthenticate, isVisible));
    }

    private Observable<WhoseHereModel> convertPhotosToFriendsModel(MockPhotosResponse response,
                                                                   boolean isAuthenticate, boolean isVisible) {
        return Observable.create(subscriber -> {
            WhoseHereModel hereModel = new WhoseHereModel();
            hereModel.setAuthorized(isAuthenticate);
            hereModel.setFBVisible(isVisible);
            ArrayList<CheckinsFriendModel> friends = new ArrayList<>();
            for (String photoUrl : response.getPhotos()) {
                CheckinsFriendModel model = new CheckinsFriendModel();
                model.setImageUrl(photoUrl);
                friends.add(model);
            }
            hereModel.setCheckIns(friends);
            subscriber.onNext(hereModel);
            subscriber.onCompleted();
        });
    }
}
