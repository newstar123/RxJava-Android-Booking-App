package app.gateway.qorumcache.basecache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import app.CustomApplication;

public class BaseSharedCache implements BaseCacheInterface {
    private static final String LOCAL_SETTINGS = "local_settings";
    private String key;

    protected BaseSharedCache(String key) {
        this.key = key;
    }

    private SharedPreferences.Editor getEditor(Context context) {
        try {
            return getPreferences(context).edit();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    public <T> boolean save(BaseCacheType type, T value) {
        try {
            return getEditor(getAppContext()).putString(key, String.valueOf(value)).commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public <T> T get(BaseCacheType type) {
        try {
            String value = getPreferences(getAppContext()).getString(key, "");
            if (!TextUtils.isEmpty(value))
                switch (type) {
                    case STRING:
                        return (T) value;
                    case BOOLEAN:
                        return (T) Boolean.valueOf(Boolean.parseBoolean(value));
                    case INT:
                        return (T) Integer.valueOf(Integer.parseInt(value));
                    case LONG:
                        return (T) Long.valueOf(Long.parseLong(value));
                    case FLOAT:
                        return (T) Float.valueOf(Float.parseFloat(value));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDefault(type);
    }

    private <T> T returnDefault(BaseCacheType type) {
        switch (type) {
            case BOOLEAN:
                return (T) Boolean.valueOf(false);
            case INT:
                return (T) Integer.valueOf(0);
            case LONG:
                return (T) Long.valueOf(0);
            case FLOAT:
                return (T) Float.valueOf(0.f);
            default:
                return (T) "";
        }
    }

    @Override
    public void reset() {
        save(BaseCacheType.STRING, "");
    }

    @Override
    public boolean exist() {
        return !TextUtils.isEmpty(get(BaseCacheType.STRING));
    }

}
