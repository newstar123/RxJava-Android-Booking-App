package app.delivering.mvp.coach.profile.init.binder;

import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.drawer.init.events.OpenNavigationDrawerEvent;
import butterknife.BindView;
import butterknife.OnClick;

public class InitProfileCoachMarkBinder extends BaseBinder {
    @BindView(R.id.profile_coach_mark_container) RelativeLayout container;

    public InitProfileCoachMarkBinder(BaseActivity activity) {
        super(activity);
    }

    @OnClick(R.id.got_it_button) void onClick(){
        EventBus.getDefault().postSticky(new OpenNavigationDrawerEvent());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.profile_coach_lick_navigation) void onClickOpenProfile(){
        EventBus.getDefault().postSticky(new OpenNavigationDrawerEvent());
        getActivity().onBackPressed();
    }
}
