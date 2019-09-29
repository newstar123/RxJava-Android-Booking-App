package app.delivering.mvp.tab.tip.init.validator;


import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public abstract class BaseValidator<INPUT, OUTPUT> extends BasePresenter<ConfirmTipsRequest, Observable<CheckInResponse>> {

    public BaseValidator(BaseActivity activity) {
        super(activity);
    }

    public abstract OUTPUT validate(INPUT input);
}
