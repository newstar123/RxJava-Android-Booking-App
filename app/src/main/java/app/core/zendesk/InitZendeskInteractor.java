package app.core.zendesk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.BaseInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import zendesk.support.request.RequestActivity;
import zendesk.support.request.RequestUiConfig;

public class InitZendeskInteractor implements BaseInteractor<Pair<String, String>, Observable<RequestUiConfig.Builder>> {

    private final static String OS_VERSION = "android:";
    private final static String APP_VERSION = "app_version:";
    private final static String VENUE_ID = "venue_id:";
    private final static String MARKET = "market:";
    private final static String CHECK_IN_ID = "Checkin_Venue::";

    private final List<String> tagsList;
    private final BaseActivity activity;


    public InitZendeskInteractor(BaseActivity activity) {
        super();
        this.activity = activity;
        tagsList = new ArrayList<>();
    }

    private boolean addSingleTag(String tag) {
        boolean isTagFilled = false;
        if (!tag.isEmpty()) {
            try {
                isTagFilled = tagsList.add(tag);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return isTagFilled;
    }

    @Override
    public Observable<RequestUiConfig.Builder> process(Pair<String, String> inputData) {
        return Observable.just((long)QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG))
                .map(this::fillTagWithVenueId)
                .map(isOk -> fillTagWithOSVersion())
                .map(isOk -> fillTagWithAppVersion())
                .map(isOk -> fillTagWithDeviceModel())
                .map(isOk -> fillTagWithMarketName(inputData.first))
                .map(isOk -> fillTagWithOpenedVenueName())
                .concatMap(isOk -> setUpRequestForZendesk(inputData.second));
    }

    private Observable<RequestUiConfig.Builder> setUpRequestForZendesk(String requestSubject) {
        return Observable.just(this.tagsList)
                .map(list -> RequestActivity.builder()
                                            .withRequestSubject(requestSubject)
                                            .withTags(list));
    }

    private boolean fillTagWithOSVersion() {
        String OSVersion = OS_VERSION.concat(android.os.Build.VERSION.RELEASE)
                .replaceAll(" ", "_").toLowerCase();

        return addSingleTag(OSVersion);
    }

    private boolean fillTagWithVenueId(Long venueId) {
        String modifiedVenueId = VENUE_ID.concat(String.valueOf(venueId))
                .replaceAll(" ", "_").toLowerCase();
        return addSingleTag(modifiedVenueId);
    }

    private boolean fillTagWithMarketName(String name) {
        String market = MARKET.concat(name).replaceAll(" ", "_").toLowerCase();
        return addSingleTag(market);
    }

    private boolean fillTagWithAppVersion() {
        Context applicationContext = activity.getApplicationContext();
        PackageManager packageManager = applicationContext.getPackageManager();
        String appVersion = "not_available";

        try {
            appVersion = packageManager.getPackageInfo(applicationContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return addSingleTag(APP_VERSION.concat(appVersion).replaceAll(" ", "_").toLowerCase());
    }

    private boolean fillTagWithDeviceModel() {
        return addSingleTag(String.format(activity.getString(R.string.value_space_value),
                Build.BRAND, Build.MODEL).replaceAll(" ", "_").toLowerCase());
    }

    private boolean fillTagWithOpenedVenueName() {
        final String sharedCheckInId = QorumSharedCache.checkOpenedBarName().get(BaseCacheType.STRING);
        return addSingleTag(sharedCheckInId.isEmpty() ? "" :
                CHECK_IN_ID.concat(sharedCheckInId).replaceAll(" ", "_").toLowerCase());
    }

}
