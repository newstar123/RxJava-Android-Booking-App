package app.gateway.feedback;


import app.core.feedback.put.entity.FeedbackRequestBody;
import app.core.payment.regular.model.EmptyResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface FeedbackRetrofitGateway {

    @PUT("v2/patrons/{id}/checkins/{cid}/review")
    Observable<EmptyResponse> put(@Header("Authorization") String token,
                                  @Path("id") long userId,
                                  @Path("cid") long checkinId,
                                  @Body FeedbackRequestBody body);
}
