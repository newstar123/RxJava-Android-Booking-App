package app.gateway.checkout.delay.post;


import android.provider.ContactsContract;

import app.core.payment.regular.model.EmptyResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface PostDelayCheckoutGateway {

    @POST("v2/patrons/{patron_id}/checkins/{checkin_id}/delay")
    Observable<Response<ContactsContract.Data>> put(@Header("Authorization") String token,
                                                    @Path("patron_id") long userId,
                                                    @Path("checkin_id") long checkinId,
                                                    @Query("ms") int delayMs,
                                                    @Body() EmptyResponse response);
}
