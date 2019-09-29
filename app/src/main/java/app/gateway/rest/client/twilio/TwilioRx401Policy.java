package app.gateway.rest.client.twilio;

import app.gateway.auth.AndroidAccountTokenInvalidator;
import app.gateway.rest.client.Rx401Policy;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

public class TwilioRx401Policy {
    public static String Unauthorized = "Unauthorized"; // wrong verification code

    public static <T> Observable.Transformer<T, T> apply() {
        return observable -> observable.retryWhen(errors -> errors.concatMap(error -> {
            if (isTokenExpired(error))
                return AndroidAccountTokenInvalidator.invalidateToken();
            return Observable.error(error);
        }));
    }

    private static boolean isTokenExpired(Throwable throwable) {
        return throwable instanceof HttpException && ((HttpException) throwable).code() == 401 &&
                !((HttpException) throwable).message().equals(Unauthorized);
    }
}
