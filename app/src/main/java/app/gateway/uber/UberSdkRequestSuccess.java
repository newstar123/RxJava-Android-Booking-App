package app.gateway.uber;


import java.io.IOException;

import retrofit2.Response;
import rx.exceptions.Exceptions;

public class UberSdkRequestSuccess {

    public static <T> void check(Response<T> response) {
        if (!response.isSuccessful())
            throwRequestFailed(response);
    }

    private static <T> void throwRequestFailed(Response<T> response) {
        try {
            throw new RuntimeException(response.errorBody().string());
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }
}
