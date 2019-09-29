package app.delivering.mvp.bars.detail.init.ubercall.call.presenter;

import app.R;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.ubercall.call.model.BarDetailValidateUberModel;
import app.delivering.mvp.bars.detail.init.ubercall.init.exceptions.GuestUberCallException;
import app.delivering.mvp.bars.detail.init.ubercall.init.exceptions.UberCallTooFarException;
import rx.Observable;

public class BarDetailValidateUberPresenter extends BasePresenter<BarDetailValidateUberModel, Observable<EmptyResponse>> {

    public BarDetailValidateUberPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<EmptyResponse> process(BarDetailValidateUberModel model) {
        return validate(model.getButtonCallText());
    }

    private Observable<EmptyResponse> validate(String buttonText) {
        return Observable.create(subscriber -> {
            if (buttonText.equals(getActivity().getString(R.string.too_far)))
                throw new UberCallTooFarException();
            else if (buttonText.equals(getActivity().getString(R.string.guest_mode)))
                throw new GuestUberCallException();
            else {
                subscriber.onNext(new EmptyResponse());
                subscriber.onCompleted();
            }
        });
    }
}
