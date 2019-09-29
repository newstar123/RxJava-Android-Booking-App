package app.gateway.rest.client;


import app.gateway.auth.AndroidAccountTokenInvalidator;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

public class Rx401Policy {
    public static <T> Observable.Transformer<T, T> apply() {
        return observable -> observable.retryWhen(errors -> errors.concatMap(error -> {
            if (isTokenExpired(error))
                return AndroidAccountTokenInvalidator.invalidateToken();
            return Observable.error(error);
        }));
    }

    private static boolean isTokenExpired(Throwable throwable) {
        return throwable instanceof HttpException && ((HttpException) throwable).code() == 401;
    }
}
