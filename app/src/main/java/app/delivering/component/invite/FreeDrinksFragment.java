package app.delivering.component.invite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.freedrink.actionbar.binder.FreeDrinksActionBarBinder;
import app.delivering.mvp.freedrink.init.binder.FreeDrinksInitBinder;
import app.delivering.mvp.freedrink.init.events.UpdateVerificationsEvent;
import app.delivering.mvp.freedrink.share.FreeDrinkLinkShareBinder;

public class FreeDrinksFragment extends BaseFragment {
    public static final String FREE_DRINKS_CODE = "user_free_drinks_code";
    public static final String FREE_DRINKS_CODE_URL = "user_free_drinks_code_url";
    public static final String FREE_DRINKS_INVITED = "user_free_drinks_invites";
    public static final String FREE_DRINKS_CHECKINED = "user_free_drinks_checkined_friends";
    public static final String FREE_DRINKS_WAITING = "user_free_drinks_waiting";
    public static final String PHONE_VERIFICATION = "phone_verification";
    public static final String EMAIL_VERIFICATION = "email_verified";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_drinks, container, false);
        initViews(view);
        loadCityImage(view);
        return view;
    }

    private void initViews(View view) {
        FreeDrinksActionBarBinder actionBarBinder = new FreeDrinksActionBarBinder(getBaseActivity());
        addItemForViewsInjection(actionBarBinder, view);
        FreeDrinksInitBinder drinksInitBinder = new FreeDrinksInitBinder(this);
        addToEventBusAndViewInjection(drinksInitBinder, view);
        FreeDrinkLinkShareBinder freeDrinkLinkShareBinder = new FreeDrinkLinkShareBinder(this);
        addItemForViewsInjection(freeDrinkLinkShareBinder, view);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle arg = getArguments();
            arg.putInt(FreeDrinksFragment.PHONE_VERIFICATION, 1);
            setArguments(arg);
            EventBus.getDefault().postSticky(new UpdateVerificationsEvent());
        }
    }
}
