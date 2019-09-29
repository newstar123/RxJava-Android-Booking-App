package app.gateway.shared.base;

import android.content.Context;
import android.content.SharedPreferences;

import app.CustomApplication;

public class BaseTextCacheInteger {
    private Context context;
    private String key;

    public BaseTextCacheInteger(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public void save(int value) {
        try {
            getPreferences(context).edit().putInt(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get() {
        try {
            return getPreferences(context).getInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reset() {
        try {
            getPreferences(context).edit().putInt(key, 0).commit();
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
