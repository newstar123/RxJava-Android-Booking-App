package app.core.version;

import android.text.TextUtils;

import app.BuildConfig;
import app.core.BaseOutputInteractor;
import app.core.version.entity.OutdatedAppVersionException;
import app.gateway.version.GetAvailableAppVersionGateway;
import rx.Observable;

public class CheckAppUpdatesInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private final static String PRODUCTION_FLAVOR = "production";
    private final static String FLAVOR_NAME_SUFFIX_DIVIDER = " ";
    private final static String APP_VERSION_DIVIDER = ".";
    private final GetAvailableAppVersionGateway appVersionGateway;

    public CheckAppUpdatesInteractor() {
        appVersionGateway = new GetAvailableAppVersionGateway();
    }

    @Override
    public Observable<Boolean> process() {
        return appVersionGateway.getAppVersion()
                .map(this::compareAppVersions)
                .doOnNext(isLatestVersionInUse -> {
                    if (!isLatestVersionInUse)
                        throw new OutdatedAppVersionException();
                });
    }

    private boolean compareAppVersions(String availableVersion) {
        if (TextUtils.isEmpty(availableVersion)) return true;  //The app has not been released on the market.
        try {
            String versionName = BuildConfig.FLAVOR.equals(PRODUCTION_FLAVOR) ? //The production version doesn't have a suffix.
                    BuildConfig.VERSION_NAME :
                    BuildConfig.VERSION_NAME.split(FLAVOR_NAME_SUFFIX_DIVIDER)[0];
            String[] availableVersionMarkers = availableVersion.split(APP_VERSION_DIVIDER);
            String[] currentVersionMarker = versionName.split(APP_VERSION_DIVIDER);
            if (Integer.parseInt(availableVersionMarkers[0]) > Integer.parseInt(currentVersionMarker[0]))
                return false;
            if (Integer.parseInt(availableVersionMarkers[1]) > Integer.parseInt(currentVersionMarker[1]))
                return false;
            if (Integer.parseInt(availableVersionMarkers[2]) > Integer.parseInt(currentVersionMarker[2]))
                return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
