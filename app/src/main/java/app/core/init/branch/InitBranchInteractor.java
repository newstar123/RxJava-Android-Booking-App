package app.core.init.branch;

import org.json.JSONException;
import org.json.JSONObject;

import app.core.BaseOutputInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import io.branch.referral.Branch;
import rx.Observable;

public class InitBranchInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private static final String FIRST_SESSION_KEY = "+is_first_session";
    private static final String DEEP_LINK_CLICKED_KEY = "+clicked_branch_link";
    private static final String MAIL_VERIFICATION_LINK_KEY = "+clicked_branch_link";
    private static final String INVITATION_LINK_KEY = "+clicked_branch_link";
    private static final String VENUE_ID_KEY = "venue_id";
    private BaseActivity activity;
    private int venueIdFromBranch = 0;
    private boolean isFirstSession;
    private boolean isDeepLinkClicked;


    public InitBranchInteractor(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public Observable<EmptyResponse> process() {
        return Observable.create((Observable.OnSubscribe<Integer>) subscriber ->
            Branch.getInstance(activity).initSession((referringParams, error) -> {
                    if (error == null)
                        parseLinkContent(referringParams);
                    subscriber.onNext(venueIdFromBranch);
                    subscriber.onCompleted();
                }, activity.getIntent().getData(), activity))
                .concatMap(integer -> Observable.just((int)QorumSharedCache.checkBranch().get(BaseCacheType.INT)))
                .map(this::checkVenueDetailWasOpened)
                .map(val -> QorumSharedCache.checkBranch().save(BaseCacheType.INT, val))
                .map(value -> new EmptyResponse());
    }

    private Integer checkVenueDetailWasOpened(Integer savedVenueId) {
        if(savedVenueId > 0)
            return savedVenueId;
        else return isDeepLinkClicked ? venueIdFromBranch : savedVenueId;
    }

    private void parseLinkContent(JSONObject referringParams) {
        if (!(referringParams == null || referringParams.length() == 0)) {
            try {
                isDeepLinkClicked = referringParams.getBoolean(DEEP_LINK_CLICKED_KEY);
                if (isDeepLinkClicked) {
                    venueIdFromBranch = referringParams.optInt(VENUE_ID_KEY);
                    boolean isMailVerificationLinkType = referringParams.getBoolean(MAIL_VERIFICATION_LINK_KEY);
                    boolean isVendorInvitationLinkType = referringParams.getBoolean(INVITATION_LINK_KEY);
                }
                isFirstSession = referringParams.getBoolean(FIRST_SESSION_KEY);
                sendFirstLaunchAnalytics(isFirstSession);
                //TODO: process responses

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            resetSavedParams();
        }
    }

    private void resetSavedParams() {
        isDeepLinkClicked = false;
        isFirstSession = false;
        venueIdFromBranch = 0;
    }

    private void sendFirstLaunchAnalytics(boolean isFirstLaunch) {
        if (isFirstLaunch)
            MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getFirstLaunchEvent());
    }
}
