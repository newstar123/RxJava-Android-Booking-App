package app.delivering.component.coach.checkin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import app.delivering.mvp.coach.checkin.binder.FirstCheckInCoachMarkBinder;
import app.delivering.mvp.coach.init.binder.InitFirstCheckInCoachMarkBinder;

public class FirstCheckInCoachMarkFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_check_in_coach_mark, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        InitFirstCheckInCoachMarkBinder initBinder = new InitFirstCheckInCoachMarkBinder(getBaseActivity());
        addItemForViewsInjection(initBinder, view);
    }

    @Override
    public void onDestroyView() {
        if (getArguments() != null) {
            long barId = getArguments().getLong(FirstCheckInCoachMarkBinder.CURRENT_BAR_ID, 0);
            OpenTabEvent clickEvent = new OpenTabEvent();
            clickEvent.setCurrentBarId(barId);
            EventBus.getDefault().postSticky(clickEvent);
        }
        super.onDestroyView();
    }
}
