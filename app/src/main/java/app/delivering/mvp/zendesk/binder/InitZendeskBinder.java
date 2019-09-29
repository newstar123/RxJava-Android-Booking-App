package app.delivering.mvp.zendesk.binder;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.zendesk.presenter.InitZendeskPresenter;
import butterknife.BindView;
import butterknife.OnEditorAction;
import rx.android.schedulers.AndroidSchedulers;
import zendesk.support.request.RequestUiConfig;

public class InitZendeskBinder extends BaseBinder {
    @BindView(R.id.zendesk_screen_toolbar) Toolbar toolBar;
    @BindView(R.id.zendesk_screen_subject_input) EditText editText;

    private String label;

    private final InitZendeskPresenter zendeskPresenter;

    public InitZendeskBinder(BaseActivity activity) {
        super(activity);
        zendeskPresenter = new InitZendeskPresenter(activity);
    }

    @Override
    public void afterViewsBounded() {
        super.afterViewsBounded();
        setUpToolBar();
    }

    private void setUpToolBar() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getString(R.string.menu_support));
        toolBar.setNavigationOnClickListener(v -> getActivity().finish());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCitySelected(CitiesModel citiesModel) {
        label = citiesModel.getNearLocation().getLabel();
    }

    @OnEditorAction({R.id.zendesk_screen_subject_input})
    public boolean checkSubjectInputEditor(int actionId) {
        if (!editText.getText().toString().isEmpty()) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeSoftKeyboard();
                setUpZendeskOptions(editText.getText().toString());
                return true;
            }
        }
        return false;
    }

    public void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void setUpZendeskOptions(String text) {
        zendeskPresenter.process(new Pair<>(label, text))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startZendesk, err -> { }, () -> {});
    }

    private void startZendesk(RequestUiConfig.Builder builder) {
        builder.show(getActivity());
        getActivity().finish();
    }
}
