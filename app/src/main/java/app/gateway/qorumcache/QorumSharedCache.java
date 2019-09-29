package app.gateway.qorumcache;

import app.gateway.qorumcache.basecache.BaseSharedCache;

public class QorumSharedCache extends BaseSharedCache {

    private static final String UBER_FREE_RIDE_MARK = "app.gateway.qorumcache.UBER_FREE_RIDE_MARK";
    private static final String FREE_RIDE_VALUE = "app.gateway.qorumcache.FREE_RIDE_VALUE";
    private static final String FREE_RIDE_DIALOG = "app.gateway.qorumcache.FREE_RIDE_DIALOG";
    private static final String TWILIO_VERIF_TIMER_VALUE = "app.gateway.qorumcache.TWILIO_VERIF_TIMER_VALUE";
    private static final String TWILIO_VERIF_DEVICE_TIME = "app.gateway.qorumcache.TWILIO_VERIF_DEVICE_TIME";
    private static final String AUTO_CHECK_IN_SETTING = "app.gateway.qorumcache.AUTO_CHECK_IN_SETTING";
    private static final String SHARED_CHECK_IN_ID = "app.gateway.qorumcache.SHARED_CHECK_IN_ID";
    private static final String GCM_TOKEN_UPDATES_KEY = "app.gateway.qorumcache.GCM_TOKEN_UPDATES_KEY";
    private static final String LAST_FREE_RIDE_CHECK_IN_ID = "app.gateway.qorumcache.LAST_FREE_RIDE_CHECK_IN_ID";
    private static final String CHECK_USER_AGE = "app.gateway.qorumcache.CHECK_USER_AGE";
    private static final String CHECK_SHARED_ACCOUNT = "app.gateway.qorumcache.CHECK_SHARED_ACCOUNT";
    private static final String CHECK_REMINDER = "app.gateway.qorumcache.CHECK_REMINDER";
    private static final String CHECK_BRANCH = "app.gateway.qorumcache.CHECK_BRANCH";
    private static final String BAR_CHECK_IN_ID = "app.gateway.qorumcache.BAR_CHECK_IN_ID";
    private static final String OPENED_BAR_NAME = "app.gateway.qorumcache.OPENED_BAR_NAME";
    private static final String CHECKOUT_ID = "app.gateway.qorumcache.CHECKOUT_ID";
    private static final String CHECK_TIME_LEFT_TO_RIDE = "app.gateway.qorumcache.CHECK_TIME_LEFT_TO_RIDE";
    private static final String CHECK_IN_COACH_MARK = "app.gateway.qorumcache.CHECK_IN_COACH_MARK";
    private static final String CHECK_LOGIN_COACH_MARK = "app.gateway.qorumcache.CHECK_LOGIN_COACH_MARK";
    private static final String CHECK_ARROW_ANIM = "app.gateway.qorumcache.CHECK_ARROW_ANIM";
    private static final String CHECK_TAB_COACH_MARK = "app.gateway.qorumcache.CHECK_TAB_COACH_MARK";
    private static final String CHECK_FB_PHOTO = "app.gateway.qorumcache.CHECK_FB_PHOTO";
    private static final String FREE_RIDE_WARNING = "app.gateway.qorumcache.FREE_RIDE_WARNING";
    private static final String LOCATION_IMAGE = "app.gateway.qorumcache.LOCATION_IMAGE";
    private static final String LOCATION_REASON = "app.gateway.qorumcache.LOCATION_REASON";
    private static final String PROFILE_PHOTO = "app.gateway.qorumcache.PROFILE_PHOTO";
    private static final String PERMISSIONS_ACCOUNT = "app.gateway.qorumcache.PERMISSIONS_ACCOUNT";
    private static final String UBER_RIDE_ID = "app.gateway.qorumcache.UBER_RIDE_ID";
    private static final String SETTINGS_FB = "app.gateway.qorumcache.SETTINGS_FB";
    private static final String SETTINGS_START_TOUR = "app.gateway.qorumcache.SETTINGS_START_TOUR";
    private static final String FB_USER_ID = "app.gateway.qorumcache.FB_USER_ID";
    private static final String USER_ID = "app.gateway.qorumcache.USER_ID";

    protected QorumSharedCache(String key) {
        super(key);
    }


    public static QorumSharedCache checkLocationReason() {
        return new QorumSharedCache(LOCATION_REASON);
    }

    public static QorumSharedCache checkLocationImage() {
        return new QorumSharedCache(LOCATION_IMAGE);
    }

    public static QorumSharedCache checkFreeRideWarning() {
        return new QorumSharedCache(FREE_RIDE_WARNING);
    }

    public static QorumSharedCache checkReminder() {
        return new QorumSharedCache(CHECK_REMINDER);
    }

    public static QorumSharedCache checkSharedAccount() {
        return new QorumSharedCache(CHECK_SHARED_ACCOUNT);
    }

    public static QorumSharedCache checkUserAge() {
        return new QorumSharedCache(CHECK_USER_AGE);
    }

    public static QorumSharedCache checkBranch() {
        return new QorumSharedCache(CHECK_BRANCH);
    }

    public static QorumSharedCache checkTimeLeftToRide() {
        return new QorumSharedCache(CHECK_TIME_LEFT_TO_RIDE);
    }

    public static QorumSharedCache checkCheckoutId() {
        return new QorumSharedCache(CHECKOUT_ID);
    }

    public static QorumSharedCache checkBarCacheId() {
        return new QorumSharedCache(BAR_CHECK_IN_ID);
    }

    public static QorumSharedCache checkOpenedBarName() {
        return new QorumSharedCache(OPENED_BAR_NAME);
    }

    public static QorumSharedCache checkTabCoachMark() {
        return new QorumSharedCache(CHECK_TAB_COACH_MARK);
    }

    public static QorumSharedCache checkArrowAnimation() {
        return new QorumSharedCache(CHECK_ARROW_ANIM);
    }

    public static QorumSharedCache checkLoginCoachMark() {
        return new QorumSharedCache(CHECK_LOGIN_COACH_MARK);
    }

    public static QorumSharedCache checkCheckInCoachMark() {
        return new QorumSharedCache(CHECK_IN_COACH_MARK);
    }

    public static QorumSharedCache checkFBPhoto() {
        return new QorumSharedCache(CHECK_FB_PHOTO);
    }

    public static QorumSharedCache checkPermissionsAccount() {
        return new QorumSharedCache(PERMISSIONS_ACCOUNT);
    }

    public static QorumSharedCache checkProfilePhotoVal() {
        return new QorumSharedCache(PROFILE_PHOTO);
    }

    public static QorumSharedCache checkUberRideId() {
        return new QorumSharedCache(UBER_RIDE_ID);
    }

    public static QorumSharedCache checkSettingsFB() {
        return new QorumSharedCache(SETTINGS_FB);
    }

    public static QorumSharedCache checkSettingsStartTour() {
        return new QorumSharedCache(SETTINGS_START_TOUR);
    }

    public static QorumSharedCache checkFBUserId() {
        return new QorumSharedCache(FB_USER_ID);
    }

    public static QorumSharedCache checkUserId() {
        return new QorumSharedCache(USER_ID);
    }

    public static QorumSharedCache checkUberFreeRideMark() {
        return new QorumSharedCache(UBER_FREE_RIDE_MARK);
    }

    public static QorumSharedCache checkSavedFreeRideValue() {
        return new QorumSharedCache(FREE_RIDE_VALUE);
    }

    public static QorumSharedCache checkLastFreeRideCheckInId() {
        return new QorumSharedCache(LAST_FREE_RIDE_CHECK_IN_ID);
    }

    public static QorumSharedCache checkFreeRideDialogAlreadyShown() {
        return new QorumSharedCache(FREE_RIDE_DIALOG);
    }

    public static QorumSharedCache checkTimeForCodeVerif() {
        return new QorumSharedCache(TWILIO_VERIF_TIMER_VALUE);
    }

    public static QorumSharedCache checkTimerValInSecForCodeVerif() {
        return new QorumSharedCache(TWILIO_VERIF_DEVICE_TIME);
    }

    public static QorumSharedCache checkAutoCheckInSettings() {
        return new QorumSharedCache(AUTO_CHECK_IN_SETTING);
    }

    public static QorumSharedCache checkSharedCheckInId() {
        return new QorumSharedCache(SHARED_CHECK_IN_ID);
    }

    public static QorumSharedCache checkGCMTokenUpdates() {
        return new QorumSharedCache(GCM_TOKEN_UPDATES_KEY);
    }

}