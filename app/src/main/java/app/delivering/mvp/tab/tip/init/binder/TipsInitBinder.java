package app.delivering.mvp.tab.tip.init.binder;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import app.R;
import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.tip.entity.ExactGratuity;
import app.core.checkin.tip.entity.Gratuity;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.tab.tip.filter.DecimalInputFilter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.tab.content.model.TabContentModel;
import app.delivering.mvp.tab.content.model.TabContentRequest;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import app.delivering.mvp.tab.tip.init.presenter.TipsInitPresenter;
import app.delivering.mvp.tab.tip.init.validator.CustomTipsValidation;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class TipsInitBinder extends BaseBinder implements ScreenSizeInterface {
    private static final String DOLLAR_PREFIX = "$";
    private static final int MIN_PERCENT = 18;
    private static final int MIDDLE_PERCENT = 20;
    private static final int MAX_PERCENT = 25;
    public static int MAX_CUSTOM_TIPS_VAL = 5000;
    private static final String FORMAT_DOUBLE = "$%.2f";
    private int gratuity;
    private double exactGratuity;
    private double minTipsValue = 0.0;
    private boolean isIgnore;
    private String formattedConfirmText;
    private final InitExceptionHandler initExceptionHandler;
    private final TipsInitPresenter tipsInitPresenter;
    private final CustomTipsValidation customTipsValidation;

    @BindView(R.id.main_container) CoordinatorLayout rootLayout;
    @BindView(R.id.tip_variants_group) RadioGroup tipsContainer;
    @BindView(R.id.custom_tip_value) EditText customTipEditText;
    @BindView(R.id.tab_tip_confirm_button) Button confirmTip;
    @BindView(R.id.drink_discounts_text) TextView drinkDiscounts;
    @BindView(R.id.tab_line_progress) MaterialProgressBar progressBar;
    @BindViews({R.id.min_tip, R.id.middle_tip,
            R.id.max_tip, R.id.custom_tip_value}) List<View> radioButtons;
    @BindViews({R.id.tab_totals_container, R.id.tab_totals,
            R.id.tab_uber_button, R.id.tab_close}) List<View> views;

    @BindView(R.id.tab_totals_tip_value) TextView tipPercentValue;
    @BindView(R.id.tab_totals_tax_value) TextView taxValue;
    @BindView(R.id.tab_totals_qorum_discount) TextView offPercentTitle;
    @BindView(R.id.tab_totals_qorum_discount_value) TextView offPercentValue;
    @BindView(R.id.tab_totals_total_value) TextView totalValue;
    @BindView(R.id.tab_tips_container) LinearLayout tabSecondContainers;

    public TipsInitBinder(BaseActivity activity) {
        super(activity);
        tipsInitPresenter = new TipsInitPresenter(getActivity());
        customTipsValidation = tipsInitPresenter;
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override
    public void afterViewsBounded() {
        setProgress(progressBar);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Point size = this.getDisplaySize(getActivity());
            int rootHeight = size.y;
            int contentBottom = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getBottom();
            int keypadHeight = rootHeight - contentBottom;
            setViewsVisibility(keypadHeight >= rootHeight * 0.15);
        });
        checkClickingDoneButton();
    }

    private void checkClickingDoneButton() {
        customTipEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    || actionId == EditorInfo.IME_ACTION_DONE) {
                if (confirmTip.isEnabled())
                    confirm();
            }
            return true;
        });
    }

    private void setViewsVisibility(boolean isVisible) {
        ButterKnife.apply(confirmTip, isVisible ? ViewActionSetter.VISIBLE : ViewActionSetter.GONE);
        ButterKnife.apply(views, isVisible ? ViewActionSetter.GONE : ViewActionSetter.VISIBLE);
        drinkDiscounts.setPadding(0, 0, 0, isVisible ? 15 : 0 );
    }

    @OnClick(R.id.tab_tip_confirm_button) void confirm() {
        showProgress();
        setUpInput(false);
        ButterKnife.apply(radioButtons, ViewActionSetter.DISABLE);

        final Gratuity setUpGratuity = customTipsValidation.setUpGratuity(gratuity);
        final ExactGratuity setUpExactGratuity = customTipsValidation.setUpExactGratuity(exactGratuity);

        tipsInitPresenter.process(new ConfirmTipsRequest(setUpExactGratuity, setUpGratuity, exactGratuity > 0))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError, ()->{});
    }

    private void show(CheckInResponse response) {
        hideProgress();
        ButterKnife.apply(radioButtons, ViewActionSetter.ENABLE);
        EventBus.getDefault().post(response);
    }

    private void onError(Throwable throwable) {
        hideProgress();
        ButterKnife.apply(radioButtons, ViewActionSetter.ENABLE);
        initExceptionHandler.showError(throwable, v -> confirm());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(CheckInResponse checkInResponse) {
        if (checkInResponse.getCheckin().getTotals().getTotal() > 0)
            tabSecondContainers.setVisibility(View.VISIBLE);

        exactGratuity = checkInResponse.getCheckin().getExactGratuity() / 100;
        gratuity = checkInResponse.getCheckin().getGratuity();

        tipsInitPresenter.validate(new TabContentRequest(gratuity, exactGratuity, checkInResponse))
                .subscribe(val -> initContentFields(val), err -> {}, ()->{});

        minTipsValue = (double) checkInResponse.getCheckin().getTotals().getSubTotal() * 18 / 10000.;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String minCaseVal = String.format(getActivity().getString(R.string.validation_min_case), decimalFormat.format(minTipsValue));
        formattedConfirmText = String.format("%s ", getActivity().getString(R.string.min_case)).concat(minCaseVal);

        setUpTipViews(checkInResponse.getCheckin());
    }

    private void initContentFields(TabContentModel model) {
        tipPercentValue.setText(String.format("%s %s", getString(R.string.tip_with_dots), model.getTipPercentValue()));
        taxValue.setText(model.getTabTaxValue());
        offPercentTitle.setText(model.getOffPercentTitle());
        offPercentValue.setText(model.getOffPercentValue());
        totalValue.setText(model.getTotalValue());
    }

    private void setUpTipViews(GetCheckInsResponse checkInsResponse) {
        if (checkInsResponse != null) {
            tipsContainer.setVisibility(View.VISIBLE);
            double exactTip = checkInsResponse.getExactGratuity();
            if (exactTip == 0) {
                switch (checkInsResponse.getGratuity()) {
                    case MIN_PERCENT:
                        tipsContainer.check(R.id.min_tip);
                        break;
                    case MIDDLE_PERCENT:
                        tipsContainer.check(R.id.middle_tip);
                        break;
                    case MAX_PERCENT:
                        tipsContainer.check(R.id.max_tip);
                        break;
                }
            } else {
                String appliedTip = String.format(Locale.US, FORMAT_DOUBLE, Double.valueOf(exactTip / 100));
                customTipEditText.setActivated(true);
                customTipEditText.requestFocus();
                customTipEditText.setText(appliedTip);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void start(StartTabEvent event) {
        customTipEditText.setFilters(new InputFilter[]{new DecimalInputFilter(5, 2)});
        setUpInput(false);
    }

    @OnCheckedChanged({R.id.min_tip, R.id.middle_tip, R.id.max_tip})
    void onMinimalTip(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            customTipEditText.setBackground(getActivity().getDrawable(R.color.blueBackground));
            customTipEditText.setSelected(false);
            customTipEditText.setCursorVisible(false);
            switch (button.getId()) {
                case R.id.min_tip:
                    checkUpdateTipsTo(MIN_PERCENT);
                    break;
                case R.id.middle_tip:
                    checkUpdateTipsTo(MIDDLE_PERCENT);
                    break;
                case R.id.max_tip:
                    checkUpdateTipsTo(MAX_PERCENT);
                    break;
            }
        }
    }

    @OnClick(R.id.custom_tip_value)
    void onClickCustomTip() {
        activateCustomTip();
    }

    private void activateCustomTip() {
        gratuity = 0;
        customTipEditText.setCursorVisible(true);
        setUpCustomTip();
        setUpCustomTips(exactGratuity);
    }

    @OnFocusChange(R.id.custom_tip_value)
    void onFocusChangeCustomTip(View view, boolean hasFocus) {
        if (hasFocus) {
            activateCustomTip();
        } else {
            customTipEditText.setBackground(getActivity().getDrawable(R.color.blueBackground));
            setUpInput(false);
        }
    }

    private void setUpCustomTip() {
        tipsContainer.clearCheck();
        customTipEditText.setBackground(getActivity().getDrawable(R.drawable.blue_tip_square));
    }

    @OnTextChanged(value = R.id.custom_tip_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable editable) {
        String inputVal = editable.toString();
        customTipEditText.setSelection(inputVal.length() > 0 ? inputVal.length() : 0);

        if (isIgnore) {
            isIgnore = false;
            return;
        }

        if (!inputVal.isEmpty()) {
            Double val = 0.0;
            long roundedVal = 0;

            if (inputVal.contains(DOLLAR_PREFIX))
                inputVal = inputVal.substring(1);

            try {
                val = Double.parseDouble(inputVal);
                roundedVal = Math.round(val);
                if (roundedVal > MAX_CUSTOM_TIPS_VAL) {
                    inputVal = inputVal.subSequence(0, inputVal.length() - 1).toString();
                    val = Double.parseDouble(inputVal);
                    roundedVal = Math.round(val);
                    setUpCustomTipsText(inputVal);
                } else if (roundedVal * 10 > MAX_CUSTOM_TIPS_VAL)
                    setUpCustomTipsText(inputVal);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            setUpCustomTips(val);
            setUpCustomTipsText(customTipsValidation.formatInput(val, roundedVal, inputVal));
        }
    }

    private void setUpCustomTipsText(String value) {
        isIgnore = true;
        customTipEditText.setText(value);
    }

    private void setUpCustomTips(double val) {
        if (val >= minTipsValue) {
            exactGratuity = val;
            confirmButtonOptions(true);
            confirmTip.setText(getString(R.string.accept));
        } else {
            confirmButtonOptions(false);
            confirmTip.setText(formattedConfirmText);
        }
    }

    private void checkUpdateTipsTo(int percents) {
        customTipEditText.getText().clear();
        gratuity = percents;
        exactGratuity = 0;
        if (tipsContainer.getCheckedRadioButtonId() == -1 && !customTipEditText.isFocused())
            return;
        confirm();
    }

    private void setUpInput(boolean isActive) {
        InputMethodManager inputMethod = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethod == null) return;
        if (isActive)
            inputMethod.showSoftInput(customTipEditText, 0);
        else
            inputMethod.hideSoftInputFromWindow(customTipEditText.getWindowToken(), 0);
    }

    private void confirmButtonOptions(boolean isEnable) {
        if (isEnable)
            confirmTip.setBackground(getActivity().getDrawable(R.drawable.shape_00a9e3_51d767_corner_5));
        else
            confirmTip.setBackground(getActivity().getDrawable(R.drawable.shape_dadada_corner_5));
        confirmTip.setEnabled(isEnable);
    }
}
