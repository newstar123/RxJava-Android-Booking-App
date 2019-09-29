package app.gateway.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import app.CustomApplication;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetAvailableAppVersionGateway implements GetAvailableAppVersion {
    private static final String PLAY_STORE_DETAIL_URL = "https://play.google.com/store/apps/details?id=%s";
    private static final String REQUEST_PROPERTY_KEY = "User-Agent";
    private static final String REQUEST_PROPERTY_VALUE = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    private static final String CURRENT_VERSION_PATTERN = "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>";
    private static final String APP_VERSION_PATTERN = "htlgb\">([^<]*)</s";

    @Override
    public Observable<String> getAppVersion() {
        return Observable.just(String.format(PLAY_STORE_DETAIL_URL, CustomApplication.get().getPackageName()))
                .observeOn(Schedulers.io())
                .map(this::getPlayStoreAppVersion);
    }

    private String getPlayStoreAppVersion(String appUrl) {
        String appVersion = "";
        try {
            URLConnection connection = new URL(appUrl).openConnection();
            connection.setRequestProperty(REQUEST_PROPERTY_KEY, REQUEST_PROPERTY_VALUE);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder sourceCode = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sourceCode.append(line);
                String versionString = getAppVersion(CURRENT_VERSION_PATTERN, sourceCode.toString());
                if (versionString == null) return appVersion;
                appVersion = getAppVersion(APP_VERSION_PATTERN, versionString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    private static String getAppVersion(String patternString, String input) {
        try {
            Pattern pattern = Pattern.compile(patternString);
            if (pattern == null) return null;
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) return matcher.group(1);
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}