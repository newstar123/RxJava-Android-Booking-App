package app.delivering.mvp.feedback.share.send.presenter;

import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.feedback.share.ShareFeedbackInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.feedback.send.model.ShareFeedbackResponse;
import rx.Observable;

public class ShareFeedbackPresenter extends BasePresenter<ImageBitmapRequest, Observable<ShareFeedbackResponse>> {
    private ShareFeedbackInteractor feedbackInteractor;

    public ShareFeedbackPresenter(BaseActivity activity) {
        super(activity);
        feedbackInteractor = new ShareFeedbackInteractor(getActivity());
    }

    @Override public Observable<ShareFeedbackResponse> process(ImageBitmapRequest request) {
        return feedbackInteractor.process(request);
    }
}
