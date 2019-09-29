package app.delivering.mvp.zendesk.presenter;


import android.util.Pair;

import app.R;
import app.core.profile.get.entity.ProfileModel;
import app.core.zendesk.InitZendeskInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.gateway.profile.cache.get.GetProfileRealTimeGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.zendesk.get.GetZendeskIdentityInfoGateway;
import app.gateway.zendesk.put.PutZendeskIdentityInfoGateway;
import rx.Observable;
import rx.schedulers.Schedulers;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.request.RequestUiConfig;

public class InitZendeskPresenter extends BasePresenter<Pair<String, String>, Observable<RequestUiConfig.Builder>> {
    private final GetZendeskIdentityInfoGateway getZendeskIdentityInfoGateway;
    private final PutZendeskIdentityInfoGateway putZendeskIdentityInfoGateway;
    private final GetProfileRealTimeGateway getProfileRealTimeGateway;
    private final InitZendeskInteractor initZendeskInteractor;


    public InitZendeskPresenter(BaseActivity activity) {
        super(activity);
        getZendeskIdentityInfoGateway = new GetZendeskIdentityInfoGateway(activity);
        putZendeskIdentityInfoGateway = new PutZendeskIdentityInfoGateway(activity);
        getProfileRealTimeGateway = new GetProfileRealTimeGateway();
        initZendeskInteractor = new InitZendeskInteractor(activity);
    }

    @Override
    public Observable<RequestUiConfig.Builder> process(Pair<String, String> inputData) {
        return getZendeskIdentityInfoGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(this::setUpLogicForIds)
                .flatMap(isOk -> isOk ? setUpAnonymousIdentity() : Observable.just(isOk))
                .concatMap(val -> initZendeskInteractor.process(inputData));
    }

    private Observable<ProfileModel> setUpAnonymousIdentity() {
        return getProfileRealTimeGateway.get()
                .doOnNext(this::setUpZendeskIdentity);
    }

    private void setUpZendeskIdentity(ProfileModel model) {
        Identity identity = new AnonymousIdentity.Builder()
                .withNameIdentifier(formatString(model.getFirstName(), model.getLastName()))
                .withEmailIdentifier(model.getEmail())
                .build();
        Zendesk.INSTANCE.setIdentity(identity);
    }

    private String formatString(String firstName, String lastName) {
        return String.format(getActivity().getString(R.string.value_space_value), firstName, lastName);
    }

    private Observable<Boolean> setUpLogicForIds(long zendeskUserId) {
        return zendeskUserId == 0 ? rewriteIds() : compareIds(zendeskUserId);
    }

    private Observable<Boolean> compareIds(long zendeskUserId) {
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .map(sharedUserId -> sharedUserId == zendeskUserId);
    }

    private Observable<Boolean> rewriteIds() {
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .map(Long::intValue)
                .concatMap(putZendeskIdentityInfoGateway::put)
                .map(value -> false);
    }

}
