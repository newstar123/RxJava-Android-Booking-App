package app.delivering.mvp.coach.tab.init;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.core.coachmark.tab.put.PutTabCoachMarkInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class InitTabCoachMarkBinder extends BaseBinder {
    @BindView(R.id.close_coach_mark) public ImageView imageView;
    @BindView(R.id.tab_coach_mark_container) public RelativeLayout rootLayout;
    private final PutTabCoachMarkInteractor interactor;

    public InitTabCoachMarkBinder(BaseActivity activity) {
        super(activity);
        interactor = new PutTabCoachMarkInteractor();
    }

    public void setUpCoachMarkState() {
        interactor.process(true)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> EventBus.getDefault().post(new StartTabEvent()), e -> {}, ()-> {});
    }

    @OnClick(R.id.close_coach_mark) void onClickClose() {
        getActivity().onBackPressed();
    }
}
