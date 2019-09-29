package app.gateway.verify.phone.gateway.number;

import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface StartPhoneVerificationRetrofitGateway {
    @POST("v2/patrons/{patron_id}/verification/phone/start") Observable<StartPhoneResponseVerification> post(
                                                                @Header("Authorization") String token,
                                                                @Path("patron_id") Long userId,
                                                                @Body StartPhoneRequestVerification startPhoneRequestVerification);
}
