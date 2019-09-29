package app.delivering.component.coach.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.coach.profile.init.binder.InitProfileCoachMarkBinder;
import app.delivering.mvp.coach.profile.init.events.UpdateBackgroundEvent;

public class ProfileCoachMarkFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_coach_mark, container, false);
        initViews(view);
        return view;
    }

    @Override public void onStop() {
        EventBus.getDefault().post(new UpdateBackgroundEvent());
        super.onStop();
    }

    private void initViews(View view) {
        InitProfileCoachMarkBinder initBinder = new InitProfileCoachMarkBinder(getBaseActivity());
        addItemForViewsInjection(initBinder, view);
    }
}
