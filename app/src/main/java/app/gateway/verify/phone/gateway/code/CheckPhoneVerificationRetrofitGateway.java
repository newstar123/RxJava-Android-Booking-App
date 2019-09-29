package app.gateway.verify.phone.gateway.code;


import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface CheckPhoneVerificationRetrofitGateway {
    @POST("v2/patrons/{patron_id}/verification/phone/check")
    Observable<CheckPhoneResponseVerification> check(@Header("Authorization") String token,
                                                     @Path("patron_id") Long userId,
                                                     @Body CheckPhoneRequestVerification checkPhoneRequestVerification);
}
