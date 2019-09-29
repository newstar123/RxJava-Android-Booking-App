package app.gateway.rest.client;


import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.BuildConfig;
import app.CustomApplication;
import app.R;
import app.core.init.token.entity.Token;
import app.core.uber.start.entity.RideDirection;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.qacache.QaModeCache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QorumHttpClient {
    private static final int DEFAULT_RESOURCE_POSITION = 0;
    public static final String HEADER_AUTHORIZATION_PREFIX = "Bearer ";

    public static Retrofit get() {
        return CustomApplication.get().getRestClient();
    }

    public static Retrofit create() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateToIso8601TypeAdapter())
                .registerTypeAdapter(RideDirection.class, new FromUpperToLowerAdapter())
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(new LogToFileInterceptor())
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl(CustomApplication.get()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static String getBaseUrl(Context context) {
        Resources resources = context.getResources();
        return getConnectionType(resources) + getHost(resources) + getSuffix(resources);
    }

    private static String getConnectionType(Resources resources) {
        if (isCustomEnvironmentActivate()) {
                int position = QaModeCache.getQaModeConnectionType().get(BaseCacheType.INT);
                return resources.getStringArray(R.array.connection_type)[position];
        }
        return resources.getStringArray(R.array.connection_type)[DEFAULT_RESOURCE_POSITION];
    }

    private static String getHost(Resources resources) {
        if (isCustomEnvironmentActivate()) {
                int position = QaModeCache.getQaModeServerHost().get(BaseCacheType.INT);
                return resources.getStringArray(R.array.server_host_list)[position];
        }
        return resources.getStringArray(R.array.server_host_list)[DEFAULT_RESOURCE_POSITION];
    }

    private static String getSuffix(Resources resources) {
        if (isCustomEnvironmentActivate()) {
                int position = QaModeCache.getQaModeRootUrn().get(BaseCacheType.INT);
                return resources.getStringArray(R.array.server_host_suffix)[position];
        }
        return resources.getStringArray(R.array.server_host_suffix)[DEFAULT_RESOURCE_POSITION];
    }

    private static boolean isCustomEnvironmentActivate() {
        if (QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN))
            if (QaModeCache.getQaModeCustomEnvironment().get(BaseCacheType.BOOLEAN))
                return true;
        return false;
    }

    public static String createTokenWithPrefix(Token token) {
        return QorumHttpClient.HEADER_AUTHORIZATION_PREFIX + token.getAuthToken();
    }
}
