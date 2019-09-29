package app.core.feedback.put.gateway;

import app.core.feedback.put.entity.FeedbackRequestModel;
import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface FeedbackGateway {
   Observable<EmptyResponse> put(FeedbackRequestModel body);
}
