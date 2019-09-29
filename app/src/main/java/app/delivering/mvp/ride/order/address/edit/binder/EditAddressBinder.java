package app.delivering.mvp.ride.order.address.edit.binder;


import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.adapter.AddressPredictionAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.address.edit.model.EditAddressResponse;
import app.delivering.mvp.ride.order.address.edit.presenter.EditAddressPresenter;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class EditAddressBinder extends BaseBinder {
    @BindView(R.id.order_ride_address) EditText orderRideFromEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_address_prediction) RecyclerView addressPredictionRecyclerView;
    @BindView(R.id.order_ride_information_section) View orderRideInformationSection;
    private final EditAddressPresenter editAddressPresenter;
    private ReplaySubject<EditAddressResponse> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private AddressPredictionAdapter addressPredictionAdapter;

    public EditAddressBinder(BaseActivity activity) {
        super(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(getActivity());
        editAddressPresenter = new EditAddressPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        initAddressList();
        initAddressPredicationStream(orderRideFromEditText);
        initAddressPredicationStream(orderPickUpAddress);
    }

    private void initAddressList() {
        addressPredictionAdapter = new AddressPredictionAdapter();
        addressPredictionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addressPredictionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addressPredictionRecyclerView.setHasFixedSize(false);
        addressPredictionRecyclerView.setAdapter(addressPredictionAdapter);
    }

    private void initAddressPredicationStream(EditText editText) {
        RxTextView.afterTextChangeEvents(editText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(CharSequence::toString)
                .filter(query -> !TextUtils.isEmpty(query))
                .filter(query -> isInEditAddressMode())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(query -> showProgress())
                .concatMap(editAddressPresenter::process)
                .compose(getActivity().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(replaySubject);
    }

    private Boolean isInEditAddressMode() {
        return orderRideInformationSection.getLayoutParams().height
                == (int) getActivity().getResources().getDimension(R.dimen.dip100);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(EditAddressResponse editAddressResponse) {
        hideProgress();
        addressPredictionAdapter.setModels(editAddressResponse.getResult());
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }
}
