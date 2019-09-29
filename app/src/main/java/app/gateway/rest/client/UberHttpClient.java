package app.gateway.rest.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import app.BuildConfig;
import app.CustomApplication;
import app.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UberHttpClient {
    public static final String HEADER_AUTHORIZATION_PREFIX = "Bearer ";
    private static final Retrofit retrofit = create();

    public static String createTokenWithPrefix(String string) {
        return UberHttpClient.HEADER_AUTHORIZATION_PREFIX + string;
    }

    public static synchronized Retrofit get() {
        return retrofit;
    }

    private static Retrofit create() {
        Gson gson = new GsonBuilder()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(UberHttpClient::addDefaultHeaders).build();
        return new Retrofit.Builder()
                .baseUrl(getUberHost())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private static String getUberHost() {
        return CustomApplication.get().getResources().getStringArray(R.array.uber_host)[0];
    }

    private static Response addDefaultHeaders(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("Accept-Language", "en_US")
                .addHeader("Content-Type", "application/json");
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

}
