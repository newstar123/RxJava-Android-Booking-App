package app.qamode.qacache;

import app.gateway.qorumcache.basecache.BaseSharedCache;

public class QaModeCache extends BaseSharedCache {
    private static final String IS_QA_MODE_ACTIVE = "app.qamode.cache.IS_QA_MODE_ACTIVE";
    private static final String QA_MODE_LOGS = "app.qamode.cache.QA_MODE_LOGS";
    private static final String QA_MODE_LOGS_PATH = "app.qamode.cache.QA_MODE_LOGS_PATH";
    private static final String QA_MODE_LOGS_FILE_NAME = "app.qamode.cache.QA_MODE_LOGS_FILE_NAME";

    private static final String QA_MODE_VENUE_LOCATION = "app.qamode.cache.QA_MODE_VENUE_LOCATION";
    private static final String QA_MODE_VENUE_LOCATION_LATITUDE = "app.qamode.cache.QA_MODE_VENUE_LOCATION_LATITUDE";
    private static final String QA_MODE_VENUE_LOCATION_LONGITUDE = "app.qamode.cache.QA_MODE_VENUE_LOCATION_LONGITUDE";
    private static final String QA_MODE_VENUE_LOCATION_TYPE = "app.qamode.cache.QA_MODE_VENUE_LOCATION_TYPE";
    private static final String QA_MODE_AUTO_CHECKOUT_DISTANCE_VISIBILITY = "app.qamode.cache.QA_MODE_AUTO_CHECKOUT_DISTANCE_VISIBILITY";

    private static final String QA_MODE_CUSTOM_ENVIRONMENT = "app.qamode.cache.QA_MODE_CUSTOM_ENVIRONMENT";
    private static final String QA_MODE_CUSTOM_CONNECTION_TYPE = "app.qamode.cache.QA_MODE_CUSTOM_CONNECTION_TYPE";
    private static final String QA_MODE_CUSTOM_SERVER_HOST = "app.qamode.cache.QA_MODE_CUSTOM_SERVER_HOST";
    private static final String QA_MODE_CUSTOM_SERVER_ROOT_URN = "app.qamode.cache.QA_MODE_CUSTOM_SERVER_ROOT_URN";

    private static final String QA_MODE_UBER_API_STATE = "app.qamode.cache.QA_MODE_UBER_API_STATE";

    private QaModeCache(String key) {
        super(key);
    }

    public static QaModeCache isQaModeActive(){
        return new QaModeCache(IS_QA_MODE_ACTIVE);
    }

    public static QaModeCache getQaModeLogs(){
        return new QaModeCache(QA_MODE_LOGS);
    }

    public static QaModeCache getQaModeLogsPath(){
        return new QaModeCache(QA_MODE_LOGS_PATH);
    }

    public static QaModeCache getQaModeLogsFile(){
        return new QaModeCache(QA_MODE_LOGS_FILE_NAME);
    }

    public static QaModeCache getQaModeVenueLocation(){
        return new QaModeCache(QA_MODE_VENUE_LOCATION);
    }

    public static QaModeCache getQaModeVenueLocationLatitude(){
        return new QaModeCache(QA_MODE_VENUE_LOCATION_LATITUDE);
    }

    public static QaModeCache getQaModeVenueLocationLongitude(){
        return new QaModeCache(QA_MODE_VENUE_LOCATION_LONGITUDE);
    }

    public static QaModeCache getQaModeVenueLocationType(){
        return new QaModeCache(QA_MODE_VENUE_LOCATION_TYPE);
    }

    public static QaModeCache getQaModeAutoCheckoutDistanceVisibility(){
        return new QaModeCache(QA_MODE_AUTO_CHECKOUT_DISTANCE_VISIBILITY);
    }

    public static QaModeCache getQaModeCustomEnvironment(){
        return new QaModeCache(QA_MODE_CUSTOM_ENVIRONMENT);
    }
    public static QaModeCache getQaModeConnectionType(){
        return new QaModeCache(QA_MODE_CUSTOM_CONNECTION_TYPE);
    }
    public static QaModeCache getQaModeServerHost(){
        return new QaModeCache(QA_MODE_CUSTOM_SERVER_HOST);
    }
    public static QaModeCache getQaModeRootUrn(){
        return new QaModeCache(QA_MODE_CUSTOM_SERVER_ROOT_URN);
    }

    public static QaModeCache getQaModeUberApiState(){
        return new QaModeCache(QA_MODE_UBER_API_STATE);
    }
}
