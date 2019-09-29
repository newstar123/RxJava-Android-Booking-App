package app.gateway.shared.base;


import android.content.Context;
import android.content.SharedPreferences;

import app.CustomApplication;

public class BaseTextCacheLong {
    public static final String LOCAL_SETTINGS = "local_settings";
    private Context context;
    private String key;

    public BaseTextCacheLong(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public void save(long value) {
        try {
            getPreferences(context).edit().putLong(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long get() {
        try {
            return getPreferences(context).getLong(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reset() {
        try {
            getPreferences(context).edit().putLong(key, 0).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exist() {
        return get() != 0;
    }

    private SharedPreferences getPreferences(Context context) {
        if (context == null)
            if (getAppContext() != null)
                return getAppContext().getSharedPreferences(LOCAL_SETTINGS, Context.MODE_PRIVATE);
            else
                return null;
        return context.getSharedPreferences(LOCAL_SETTINGS, Context.MODE_PRIVATE);
    }

    private Context getAppContext() {
        return CustomApplication.get();
    }
}
