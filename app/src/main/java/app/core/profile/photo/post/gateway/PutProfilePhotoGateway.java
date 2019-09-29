package app.core.profile.photo.post.gateway;

import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.photo.post.entity.PutProfilePhotoModel;
import rx.Observable;

public interface PutProfilePhotoGateway {
    Observable<EmptyResponse> put(PutProfilePhotoModel model);
}
