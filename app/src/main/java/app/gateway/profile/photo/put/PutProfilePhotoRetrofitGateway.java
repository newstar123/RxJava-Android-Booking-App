package app.gateway.profile.photo.put;

import app.core.payment.regular.model.EmptyResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface PutProfilePhotoRetrofitGateway {
    @Multipart
    @POST("v2/patrons/{id}/profilePic/") Observable<EmptyResponse> put(@Header("Authorization") String token,
                                                                       @Path("id") long userId,
                                                                       @Part MultipartBody.Part filePart);
}
