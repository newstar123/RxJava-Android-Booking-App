package app.gateway.shared.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import app.CustomApplication;

public class BaseTextCacheString {
    private Context context;
    private String key;

    public BaseTextCacheString(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public void save(String value) {
        try {
            getPreferences(context).edit().putString(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get() {
        try {
            return getPreferences(context).getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void reset() {
        try {
            getPreferences(context).edit().putString(key, "").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exist() {
        return !TextUtils.isEmpty(get());
    }

    private SharedPreferences getPreferences(Context context) {
        if (context == null)
            if (getAppContext() != null)
                return getAppContext().getSharedPreferences(BaseTextCacheLong.LOCAL_SETTINGS, Context.MODE_PRIVATE);
            else
                return null;
        return context.getSharedPreferences(BaseTextCacheLong.LOCAL_SETTINGS, Context.MODE_PRIVATE);
    }

    private Context getAppContext() {
        return CustomApplication.get();
    }

}
