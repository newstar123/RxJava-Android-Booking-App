package app.delivering.mvp.feedback.send.presenter;

import app.core.feedback.put.entity.FeedbackRequestModel;
import app.core.feedback.put.interactor.PutFeedbackInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class SendFeedbackPresenter extends BasePresenter<FeedbackRequestModel, Observable<EmptyResponse>> {
    private PutFeedbackInteractor interactor;

    public SendFeedbackPresenter(BaseActivity activity) {
        super(activity);
        interactor = new PutFeedbackInteractor(getActivity());
    }

    @Override public Observable<EmptyResponse> process(FeedbackRequestModel requestModel) {
        return interactor.process(requestModel);
    }
}
