package app.gateway.gcm;

import app.core.gcm.entity.GcmTokenRequest;
import app.core.payment.regular.model.EmptyResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutGcmTokenGateway {
    @PUT("v2/patrons/{id}/device-token") Observable<EmptyResponse> put(@Header("Authorization") String token,
                                                                       @Path("id") long userId,
                                                                       @Body GcmTokenRequest request);
}
