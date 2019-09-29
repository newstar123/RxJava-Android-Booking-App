package app.delivering.mvp.profile.edit.init.binder;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import app.R;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.put.entity.PutProfileModel;
import app.delivering.component.BaseFragment;
import app.delivering.component.profile.ProfileFragment;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.base.binder.BaseDialogBinder;
import app.delivering.mvp.dialog.base.events.ShowNotificationDialogEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.profile.edit.actionbar.clicks.presenters.PutUpdatedProfilePresenter;
import app.delivering.mvp.profile.edit.init.events.OnResumeProfileModelEvent;
import app.delivering.mvp.profile.edit.init.events.OnStopProfileModelEvent;
import app.delivering.mvp.profile.edit.init.model.InitProfileModel;
import app.delivering.mvp.profile.edit.init.presenter.ProfileInitPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileInitBinder extends BaseBinder {
    private static final String GENDER_FEMALE_KEY = "F";
    private static final String PLUS_PREFIX = "+";
    @BindView(R.id.profile_progress) MaterialProgressBar progressBar;
    @BindView(R.id.input_first_name) TextView firstName;
    @BindView(R.id.input_last_name) TextView lastName;
    @BindView(R.id.profile_birthday) TextView birthday;
    @BindView(R.id.profile_gender) Spinner gender;
    @BindView(R.id.input_mail) TextView mail;
    @BindView(R.id.input_zip) EditText zip;
    @BindView(R.id.error_view) View errorView;
    @BindView(R.id.zip_error_text) TextView zipErrorText;
    @BindView(R.id.input_zip_layout) TextInputLayout zipLayout;
    @BindView(R.id.profile_mobile_layout) RelativeLayout phoneLayout;
    @BindView(R.id.profile_mail) RelativeLayout mailLayout;
    @BindView(R.id.input_phone) TextView phone;
    @BindView(R.id.profile_container) View container;
    @BindView(R.id.updateProfileButton) Button profileButton;
    @BindView(R.id.gender_fb_mark) ImageView genderFBMark;

    private final ProfileInitPresenter profileInitPresenter;
    private final PutUpdatedProfilePresenter putUpdatedProfilePresenter;
    private final InitExceptionHandler exceptionHandler;
    private final PutProfileModel putProfileModel;
    private final BaseFragment baseFragment;


    public ProfileInitBinder(ProfileFragment fragment) {
        super(fragment.getBaseActivity());
        baseFragment = fragment;
        profileInitPresenter = new ProfileInitPresenter(getActivity());
        putUpdatedProfilePresenter = new PutUpdatedProfilePresenter(getActivity());
        putProfileModel = new PutProfileModel();
        exceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        baseFragment.loadCityImage(container);
        setUpViewEnablingSetter(false);
        setProgress(progressBar);
        showProgress();
        profileInitPresenter.process()
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::fillingFields, e->hideProgress());
    }

    @OnTextChanged(value = R.id.input_zip, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void zipChanged(CharSequence text) {
        setUpProfileButtonOptions(true);
    }

    @OnTextChanged(value = R.id.input_mail, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void mailChanged(CharSequence text) {
        setUpProfileButtonOptions(true);
    }

    @OnTextChanged(value = R.id.input_phone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void phoneNumChanged(CharSequence text) {
        setUpProfileButtonOptions(true);
    }

    private void setUpProfileButtonOptions(boolean isEnable) {
        ButterKnife.apply(profileButton, isEnable ? ViewActionSetter.VISIBLE : ViewActionSetter.GONE);
    }

    private void fillingFields(InitProfileModel initProfileModel) {
        ProfileModel profileModel = initProfileModel.getProfileModel();
        putProfileModel.setGender(profileModel.getGender());
        firstName.setText(profileModel.getFirstName());
        lastName.setText(profileModel.getLastName());
        if (!TextUtils.isEmpty(initProfileModel.getFormattedBirthday()))
            birthday.setText(initProfileModel.getFormattedBirthday());
        mail.setText(profileModel.getEmail());
        zip.setText(profileModel.getZip());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getActivity().getResources().getStringArray(R.array.gender));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        gender.setAdapter(adapter);
        fillGender(profileModel.getGender());

        String phoneNumber = profileModel.getPhone();
        if (!TextUtils.isEmpty(phoneNumber))
            setFormattedPhone(profileModel.getCountryCode(), phoneNumber);

        hideProgress();
        setUpProfileButtonOptions(false);
    }

    private void fillGender(String genderValue) {
        if (TextUtils.isEmpty(genderValue)) {
            gender.setEnabled(true);
            genderFBMark.setVisibility(View.GONE);
        } else {
            gender.setSelection(genderValue.equals(GENDER_FEMALE_KEY) ? 1 : 0);
            gender.setEnabled(false);
            genderFBMark.setVisibility(View.VISIBLE);
        }
    }

    private void setFormattedPhone(String countryCode, String number) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            String countryISO = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode.substring(1)));
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, countryISO);
            String formattedPhone = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            this.phone.setText(formattedPhone);
        } catch (NumberParseException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            this.phone.setText(String.format(getString(R.string.value_space_value),
                    countryCode.contains(PLUS_PREFIX) ? countryCode : PLUS_PREFIX + countryCode,
                    number));
        }
    }

    @OnClick(R.id.updateProfileButton) void updateProfile() {
        if (zip.getText().toString().length() == 5) {
            setUpViewEnablingSetter(false);
            putProfileModel.setZip(zip.getText().toString());

            if (TextUtils.isEmpty(putProfileModel.getGender()))
                putProfileModel.setGender(((TextView) gender.getSelectedView()).getText().toString().substring(0, 1));

            updateProfilePresenter();
        } else
            setUpViewEnablingSetter(true);
    }

    private void updateProfilePresenter() {
        showProgress();
        putUpdatedProfilePresenter.process(putProfileModel)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isOk->show(putProfileModel), this::showError, this::hideProgress);
    }

    private void show(PutProfileModel event) {
        hideProgress();
        HashMap map = new HashMap();
        map.put(QorumNotifier.MESSAGE, getString(R.string.profile_success_updated));
        setUpProfileButtonOptions(false);
        new BaseDialogBinder(getActivity())
                .onShowDialogEvent(new ShowNotificationDialogEvent(NotificationType.DEF, map));
    }

    private void showError(Throwable throwable) {
        hideProgress();
        exceptionHandler.showError(throwable, v->updateProfilePresenter());
    }

    private void setUpViewEnablingSetter(boolean isEnable) {
        if (isEnable) {
            ButterKnife.apply(errorView, ViewActionSetter.ENABLE);
            ButterKnife.apply(zipErrorText, ViewActionSetter.VISIBLE);
        } else {
            ButterKnife.apply(errorView, ViewActionSetter.DISABLE);
            ButterKnife.apply(zipErrorText, ViewActionSetter.GONE);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateModel(OnResumeProfileModelEvent event) {
        EventBus.getDefault().removeStickyEvent(OnResumeProfileModelEvent.class);
        phone.setText(event.getNumber());
        updateProfilePresenter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseResult(OnStopProfileModelEvent event) {
        if (container != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.profile_mobile_layout) void clickUpdatePhone() {
        Intent intent = new Intent(getActivity(), VerifyPhoneNumberActivity.class);
        if (phone != null)
            intent.putExtra(VerifyPhoneNumberActivity.PHONE_NUMBER, phone.getText().toString());
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.profile_mail) void clickUpdateMail() {
        getActivity().startActivity(new Intent(getActivity(), VerifyEmailActivity.class));
    }

}
