package app.gateway.analytics.mixpanel.events;

import org.json.JSONException;
import org.json.JSONObject;

import app.CustomApplication;
import app.R;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.gateway.analytics.mixpanel.MixpanelLogModel;
import app.gateway.analytics.mixpanel.enums.CloseTabMethods;
import app.gateway.analytics.mixpanel.enums.OpenTabMethods;

public class MixpanelEvents {

    private static final String LOGIN = "Log In";
    private static final String LOGIN_KEY = "Login Method";

    private static final String REGISTRATION = "Register for Account";
    private static final String REGISTRATION_METHOD = "Registration Method";
    private static final String REGISTRATION_GENDER = "Gender";
    private static final String REGISTRATION_ZIP = "Zip Code";
    private static final String REGISTRATION_YEAR = "Birth Year";

    private static final String SEARCH_LOCATION = "Search Location";
    private static final String SEARCH_KEY = "Search Query";

    private static class VenueSelect {
        private static final String EVENT_NAME = "Venue Selected";
        private static final String PROPERTY_NAME = "Venue Name";
        private static final String PROPERTY_DISCOUNT = MixpanelEvents.DISCOUNT;
        private static final String PROPERTY_MARKET = "Market";
        private static final String PROPERTY_NEIGHBORHOOD = MixpanelEvents.NEIGHBORHOOD;
        private static final String PROPERTY_SORT = "Venue Sort";
        private static final String PROPERTY_VIEW = "Venue View";
    }

    private static final String VENUE = "Venue";
    public static final String DISCOUNT = "Discount";
    private static final String MARKET = "Market";
    private static final String NEIGHBORHOOD = "Neighborhood";
    public static final String NEARBY = "Nearby";
    public static final String NAME = "Name";
    public static final String LIST = "List";
    public static final String MAP = "Map";

    private static final String TAB_OPEN = "Tab Open";
    private static final String TAB_OPEN_METHOD = "Tab Open Method";

    private static final String TAB_CLOSE = "Tab Close";
    private static final String TIME_AT_VENUE = "Time at Venue";
    private static final String TAB_CLOSE_METHOD = "Tab Close Method";

    private static final String INVITE = "Invite a Friend";
    private static final String INVITE_KEY = "Invite Option";
    public static final String TYPE_EMAIL = "Email";
    public static final String TYPE_FACEBOOK = "Facebook";
    public static final String TYPE_TWITTER = "Twitter";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_OTHER = "Other";

    private static final String SEARCH_BAR = "Search Bar";

    private static final String FIRST_LAUNCH = "First Launch of App";
    private static final String AGE_VERIFICATION_PASSED = "Submits 21 and Over Bday";
    private static final String TUTORIAL_EVENT_NAME_PREFIX = "View Tutorial Screen ";
    private static final String LOCATION_EXPLANATION = "View Location Permissions Explanation";
    private static final String LOCATION_EXPLANATION_UNDERSTOOD = "Press Got It on Location Permissions Explanation";
    private static final String LOCATION_GPS_PERMISSION_ASKED = "View Access Location Alert";
    private static final String LOCATION_GPS_PERMISSION_RESOLVED = "Respond to Access Location Alert";
    private static final String PROPERTY_LOCATION_GPS_PERMISSION_RESOLUTION = "Response";
    private static final String LOCATION_GPS_PERMISSION_RESOLVED_POSITIVE = "Allow";
    private static final String LOCATION_GPS_PERMISSION_RESOLVED_NEGATIVE = "Don't Allow";
    private static final String ENTERED_TO_LOGIN_SCREEN = "View Registration Login Screen";
    private static final String LOGON = "Respond to Registration Login Screen";
    private static final String LOGON_METHOD = "Response";
    private static final String LOGON_METHOD_FB = "Login with Facebook";
    private static final String LOGON_METHOD_GUEST = "Continue As Guest";
    private static final String EMAIL_VERIFICATION_ASKED = "View Email Capture Screen";
    private static final String EMAIL_VERIFICATION_RESOLVED = "Submit Email Capture Screen";
    private static final String PHONE_VERIFICATION_ASKED = "View Phone Verification Screen";
    private static final String PHONE_VERIFICATION_RESOLVED = "Verify Phone Verification Success";


    public static MixpanelLogModel getRegistrationEvent(String method, String gender, String zip, String year){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(REGISTRATION);
        try {
            JSONObject props = new JSONObject();
            props.put(REGISTRATION_METHOD, method);
            props.put(REGISTRATION_GENDER, gender);
            props.put(REGISTRATION_ZIP, zip);
            props.put(REGISTRATION_YEAR, year);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getLoginEvent(String type){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOGIN);
        try {
            JSONObject props = new JSONObject();
            props.put(LOGIN_KEY, type);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getSearchLocationEvent(String cityName){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(SEARCH_LOCATION);
        try {
            JSONObject props = new JSONObject();
            props.put(SEARCH_KEY, cityName);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getBarSelectedEvent(String barName, String barDiscount,
                                                       String barCity, String barNeighbor,
                                                       String barSort, String barView){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(VenueSelect.EVENT_NAME);
        try {
            JSONObject props = new JSONObject();
            props.put(VenueSelect.PROPERTY_NAME, barName);
            props.put(VenueSelect.PROPERTY_DISCOUNT, barDiscount);
            props.put(VenueSelect.PROPERTY_MARKET, barCity);
            props.put(VenueSelect.PROPERTY_NEIGHBORHOOD, barNeighbor);
            props.put(VenueSelect.PROPERTY_SORT, barSort);
            props.put(VenueSelect.PROPERTY_VIEW, barView);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getOpenTabEvent(OpenTabMethods method, CheckInResponse response) {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(TAB_OPEN);
        try {
            JSONObject props = new JSONObject();
            props.put(VENUE, response.getCheckin().getVendor().getName());
            props.put(TAB_OPEN_METHOD, method.getName());
            props.put(DISCOUNT, String.format(CustomApplication.get().getResources().getString(R.string.value_percent_off),
                    String.valueOf(response.getCheckin().getDiscount())));
            props.put(MARKET, response.getCheckin().getVendor().getCity());
            props.put(NEIGHBORHOOD, response.getCheckin().getVendor().getNeighborhood());
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getCloseTabEvent(CloseTabMethods method, CheckInResponse response) {
        double minutesUntilTabWasClosed = 0.d;
        try {
            minutesUntilTabWasClosed = response.getCheckin().getCheckoutTime().getTime() - response.getCheckin().getCreatedAt().getTime();
        } catch (NullPointerException e){
            minutesUntilTabWasClosed = System.currentTimeMillis() - response.getCheckin().getCreatedAt().getTime();
            e.printStackTrace();
        }
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(TAB_CLOSE);
        try {
            JSONObject props = new JSONObject();
            props.put(VENUE, response.getCheckin().getVendor().getName());
            props.put(TAB_CLOSE_METHOD, method.getName());
            props.put(DISCOUNT, String.format(CustomApplication.get().getResources().getString(R.string.value_percent_off),
                    String.valueOf(response.getCheckin().getDiscount())));
            props.put(MARKET, response.getCheckin().getVendor().getCity());
            props.put(NEIGHBORHOOD, response.getCheckin().getVendor().getNeighborhood());
            props.put(TIME_AT_VENUE, minutesUntilTabWasClosed / 60000);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getInviteEvent(String type){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(INVITE);
        try {
            JSONObject props = new JSONObject();
            props.put(INVITE_KEY, type);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getSearchBarEvent(String barName){
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(SEARCH_BAR);
        try {
            JSONObject props = new JSONObject();
            props.put(SEARCH_KEY, barName);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }
  
    public static MixpanelLogModel getFirstLaunchEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(FIRST_LAUNCH);
        model.setProperties(new JSONObject());
        return model;
    }
  
    public static MixpanelLogModel getAgeVerificationPassedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(AGE_VERIFICATION_PASSED);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getTutorialEvent(int tutorialIndex) {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(TUTORIAL_EVENT_NAME_PREFIX + (tutorialIndex + 1));
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getLocationPermissionExplanationEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOCATION_EXPLANATION);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getLocationPermissionExplanationUnderstoodEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOCATION_EXPLANATION_UNDERSTOOD);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getEnteredToLoginScreenEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(ENTERED_TO_LOGIN_SCREEN);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getLogonEvent(boolean isGuestMode) {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOGON);
        try {
            JSONObject props = new JSONObject();
            props.put(LOGON_METHOD, isGuestMode ? LOGON_METHOD_GUEST : LOGON_METHOD_FB);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getLocationGpsPermissionAskedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOCATION_GPS_PERMISSION_ASKED);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getLocationGpsPermissionResolvedEvent(boolean positive) {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(LOCATION_GPS_PERMISSION_RESOLVED);
        try {
            JSONObject props = new JSONObject();
            props.put(PROPERTY_LOCATION_GPS_PERMISSION_RESOLUTION, positive ? LOCATION_GPS_PERMISSION_RESOLVED_POSITIVE : LOCATION_GPS_PERMISSION_RESOLVED_NEGATIVE);
            model.setProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MixpanelLogModel getEmailVerificationAskedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(EMAIL_VERIFICATION_ASKED);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getEmailVerificationResolvedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(EMAIL_VERIFICATION_RESOLVED);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getPhoneAskedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(PHONE_VERIFICATION_ASKED);
        model.setProperties(new JSONObject());
        return model;
    }

    public static MixpanelLogModel getPhoneResolvedEvent() {
        MixpanelLogModel model = new MixpanelLogModel();
        model.setEventName(PHONE_VERIFICATION_RESOLVED);
        model.setProperties(new JSONObject());
        return model;
    }
}
