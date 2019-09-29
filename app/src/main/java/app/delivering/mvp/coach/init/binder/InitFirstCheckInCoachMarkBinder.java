package app.delivering.mvp.coach.init.binder;

import app.R;
import app.core.coach.checkin.put.interactor.PutFirstCheckInCoachStateInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class InitFirstCheckInCoachMarkBinder extends BaseBinder {
    private final PutFirstCheckInCoachStateInteractor interactor;

    public InitFirstCheckInCoachMarkBinder(BaseActivity activity) {
        super(activity);
        interactor = new PutFirstCheckInCoachStateInteractor(activity);
    }

    @Override public void afterViewsBounded() {
       interactor.process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {}, e -> {}, ()-> {});
    }

    @OnClick(R.id.close) void onClickGotIt(){
        getActivity().onBackPressed();
    }
}
