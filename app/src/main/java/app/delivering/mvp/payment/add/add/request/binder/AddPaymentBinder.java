package app.delivering.mvp.payment.add.add.request.binder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.core.payment.add.entity.AddPaymentModel;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.payment.add.add.request.model.AddPaymentBinderRequest;
import app.delivering.mvp.payment.add.add.request.events.AddPaymentEvent;
import app.delivering.mvp.payment.add.add.request.events.OnAddPaymentStartEvent;
import app.delivering.mvp.payment.add.add.request.presenter.AddPaymentPresenter;
import app.delivering.mvp.payment.add.add.validate.events.ValidateAddPaymentEvent;
import app.delivering.mvp.payment.add.profile.verification.events.VerifyCardByPhotoEvent;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class AddPaymentBinder extends BaseBinder {
    @BindView(R.id.add_card_bottom_layout) RelativeLayout addCardLayout;

    @BindView(R.id.payment_add_number_input) EditText numberEditText;
    @BindView(R.id.payment_add_cvv_input) EditText cvvEditText;
    @BindView(R.id.payment_add_zip_input) EditText zipEditText;
    @BindView(R.id.payment_add_expired_date_input) EditText expiredEditText;
    @BindViews({R.id.payment_add_number_input,
            R.id.payment_add_cvv_input,
            R.id.payment_add_zip_input,
            R.id.payment_add_expired_date_input}) List<View> screenElements;
    private ReplaySubject<GetPaymentCardsModel> replaySubject;
    private final AddPaymentPresenter addPaymentPresenter;
    private final InitExceptionHandler initExceptionHandler;
    private Subscription binderSubscribe;

    public AddPaymentBinder(BaseActivity activity) {
        super(activity);
        addPaymentPresenter = new AddPaymentPresenter(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        replaySubject = ReplaySubject.create();
    }

    @OnClick(R.id.add_card_bottom_layout)
    public void setAddCardLayout() {
        EventBus.getDefault().post(new ValidateAddPaymentEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(AddPaymentEvent event) {
        closeSoftKeyboard();
        progressState();
        addPayment();
    }

    private void addPayment() {
        AddPaymentBinderRequest addPaymentBinderRequest = new AddPaymentBinderRequest();
        addPaymentBinderRequest.setExpiredMonthYear(getString(expiredEditText));
        AddPaymentModel addPaymentModel = new AddPaymentModel();
        addPaymentModel.setCvc(getUnspacedString(cvvEditText));
        addPaymentModel.setNumber(getUnspacedString(numberEditText));
        addPaymentModel.setZipCode(getUnspacedString(zipEditText));
        addPaymentBinderRequest.setAddPaymentModel(addPaymentModel);
        binderSubscribe = addPaymentPresenter.process(addPaymentBinderRequest)
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentStart(OnAddPaymentStartEvent event) {
        if (hasToRestore(binderSubscribe))
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void progressState() {
        showProgress();
        ButterKnife.apply(screenElements, ViewActionSetter.DISABLE);
        initExceptionHandler.dismiss();
    }

    private void show(GetPaymentCardsModel cardsModel) {
        if (cardsModel != null && cardsModel.getCards().size() == 1)
            EventBus.getDefault().post(new VerifyCardByPhotoEvent());
        else
            stopWithResult();
    }

    private void stopWithResult() {
        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> EventBus.getDefault().post(new ValidateAddPaymentEvent()));
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onAddPaymentStart(new OnAddPaymentStartEvent(/*menu*/));
        ButterKnife.apply(screenElements, ViewActionSetter.ENABLE);
    }
}
