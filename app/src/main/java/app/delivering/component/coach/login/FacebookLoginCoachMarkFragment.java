package app.delivering.component.coach.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.coach.login.init.binder.InitFacebookLoginCoachMarkBinder;

public class FacebookLoginCoachMarkFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_login_coach_mark, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        InitFacebookLoginCoachMarkBinder initBinder = new InitFacebookLoginCoachMarkBinder(getBaseActivity());
        addToEventBusAndViewInjection(initBinder, view);
    }
}
