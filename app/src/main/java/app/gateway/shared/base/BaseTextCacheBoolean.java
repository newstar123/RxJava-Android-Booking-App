package app.gateway.shared.base;


import android.content.Context;
import android.content.SharedPreferences;

import app.CustomApplication;

public class BaseTextCacheBoolean {
    private Context context;
    private String key;

    public BaseTextCacheBoolean(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public void save(boolean value) {
        try {
            getPreferences(context).edit().putBoolean(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean get() {
        try {
            return getPreferences(context).getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reset() {
        try {
            getPreferences(context).edit().putBoolean(key, false).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getPreferences(Context context) {
        if (context == null)
            if (getAppContext() != null)
                return getAppContext().getSharedPreferences("local_settings", Context.MODE_PRIVATE);
            else
                return null;
        return context.getSharedPreferences("local_settings", Context.MODE_PRIVATE);
    }

    private Context getAppContext() {
        return CustomApplication.get();
    }
}
