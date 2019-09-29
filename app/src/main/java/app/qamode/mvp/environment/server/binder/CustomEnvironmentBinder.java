package app.qamode.mvp.environment.server.binder;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.CustomApplication;
import app.R;
import app.core.init.token.entity.NoAccountException;
import app.delivering.component.BaseActivity;
import app.delivering.component.main.MainActivity;
import app.delivering.component.payment.list.PaymentsActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.drawer.logout.presenter.LogOutPresenter;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.mvp.environment.server.event.ResetEnvironmentEvent;
import app.qamode.mvp.environment.server.model.UpdateEnvironmentModel;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class CustomEnvironmentBinder extends BaseBinder {
    private static final int DEFAULT_POSITION = 0;
    @BindView(R.id.environment_switcher) SwitchCompat switcher;
    @BindView(R.id.qa_mode_environment_expand_view) TextView expandHint;
    @BindView(R.id.qa_mode_environment_container) LinearLayout settingsContainer;
    @BindView(R.id.qa_mode_server_host) Spinner hosts;
    @BindView(R.id.qa_mode_connection_type) Spinner connectionTypes;
    @BindView(R.id.qa_mode_server_host_root_urn) Spinner hostRootUrn;
    @BindView(R.id.qa_mode_apply_custom_environment) Button applyButton;
    @BindView(R.id.environment_progress) MaterialProgressBar progressBar;
    private final InitExceptionHandler initExceptionHandler;
    private LogOutPresenter presenter;
    private UpdateEnvironmentModel updateModel;
    private int selectedHostPosition;
    private int selectedTypePosition;
    private int selectedUrnPosition;
    private int selectionHostCounter;
    private int selectionTypeCounter;
    private int selectionUrnCounter;

    public CustomEnvironmentBinder(BaseActivity activity) {
        super(activity);
        presenter = new LogOutPresenter(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
    }

    @Override
    public void afterViewsBounded() {
        fillingFields();
        boolean isStateOn = QaModeCache.getQaModeCustomEnvironment().get(BaseCacheType.BOOLEAN);
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> updateState(isChecked));
        switcher.setChecked(isStateOn);
    }

    private void fillingFields() {
        String[] hostList = getActivity().getResources().getStringArray(R.array.server_host_list);
        ArrayAdapter<String> hostsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, hostList);
        hostsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hosts.setAdapter(hostsAdapter);
        selectedHostPosition = QaModeCache.getQaModeServerHost().get(BaseCacheType.INT);
        hosts.setSelection(selectedHostPosition);
        hosts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionHostCounter++;
                if (selectionHostCounter > 1) {
                    selectedHostPosition = position;
                    checkForChanges(selectedHostPosition, selectedTypePosition, selectedUrnPosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] connectionTypeList = getActivity().getResources().getStringArray(R.array.connection_type);
        ArrayAdapter<String> connectionTypesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, connectionTypeList);
        connectionTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        connectionTypes.setAdapter(connectionTypesAdapter);
        selectedTypePosition = QaModeCache.getQaModeConnectionType().get(BaseCacheType.INT);
        connectionTypes.setSelection(selectedTypePosition);
        connectionTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionTypeCounter++;
                if (selectionTypeCounter > 1) {
                    selectedTypePosition = position;
                    checkForChanges(selectedHostPosition, selectedTypePosition, selectedUrnPosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] hostRootUrnList = getActivity().getResources().getStringArray(R.array.server_host_suffix);
        ArrayAdapter<String> hostSuffixesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, hostRootUrnList);
        hostSuffixesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostRootUrn.setAdapter(hostSuffixesAdapter);
        selectedUrnPosition = QaModeCache.getQaModeRootUrn().get(BaseCacheType.INT);
        hostRootUrn.setSelection(selectedUrnPosition);
        hostRootUrn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionUrnCounter++;
                if (selectionUrnCounter > 1) {
                    selectedUrnPosition = position;
                    checkForChanges(selectedHostPosition, selectedTypePosition, selectedUrnPosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(ResetEnvironmentEvent event) {
        updateState(false);
    }

    private void updateState(boolean isChecked) {
        if (!isChecked){
            if (checkForChanges(DEFAULT_POSITION, DEFAULT_POSITION, DEFAULT_POSITION))
                new RxDialogHandler(getActivity()).showTwoButtonsWithTitle(R.string.attention,
                        R.string.qa_mode_restore_environment_message, R.string.cancel, R.string.okay)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isOk -> {
                            if (isOk) applyChanges(new UpdateEnvironmentModel(DEFAULT_POSITION, DEFAULT_POSITION, DEFAULT_POSITION));},
                                e -> { }, () -> { });
        } else {
            QaModeCache.getQaModeCustomEnvironment().save(BaseCacheType.BOOLEAN, isChecked);
        }
        expandHint.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        settingsContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private boolean checkForChanges(int selectedHostPosition, int selectedTypePosition, int selectedUrnPosition) {
        int savedHostPosition = QaModeCache.getQaModeServerHost().get(BaseCacheType.INT);
        int savedTypePosition = QaModeCache.getQaModeConnectionType().get(BaseCacheType.INT);
        int savedUrnPosition = QaModeCache.getQaModeRootUrn().get(BaseCacheType.INT);
        boolean isChangesAvailable = selectedHostPosition != savedHostPosition || selectedTypePosition != savedTypePosition ||
                selectedUrnPosition != savedUrnPosition;
        applyButton.setVisibility(isChangesAvailable ? View.VISIBLE : View.GONE);
        return isChangesAvailable;
    }

    @OnClick(R.id.qa_mode_apply_custom_environment) void apply(){ showAlertDialog();}

    private void showAlertDialog() {
        new RxDialogHandler(getActivity()).showTwoButtonsWithTitle(R.string.attention,
                R.string.qa_mode_custom_environment_message, R.string.cancel, R.string.okay)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isOk -> {
                    if (isOk) applyChanges(new UpdateEnvironmentModel(selectedHostPosition, selectedTypePosition, selectedUrnPosition));},
                        e->{}, ()->{});
    }

    private void applyChanges(UpdateEnvironmentModel model) {
        this.updateModel = model;
        progressBar.setVisibility(View.VISIBLE);
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> saveChangesAndRestart(model), this::onError, ()->{});
    }

    private void saveChangesAndRestart(UpdateEnvironmentModel model) {
        QaModeCache.getQaModeCustomEnvironment().save(BaseCacheType.BOOLEAN, switcher.isChecked());
        updateQaCache(model);
        CustomApplication.get().setUpRestClient();
        CustomApplication.get().setUpAutoCheckInController();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }

    private void updateQaCache(UpdateEnvironmentModel model) {
        QaModeCache.getQaModeServerHost().save(BaseCacheType.INT, model.getHostPosition());
        QaModeCache.getQaModeConnectionType().save(BaseCacheType.INT, model.getTypePosition());
        QaModeCache.getQaModeRootUrn().save(BaseCacheType.INT, model.getUrnPosition());
    }

    private void onError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        if (throwable instanceof NoAccountException)
            saveChangesAndRestart(updateModel);
        else if (throwable instanceof HttpException && ((HttpException) throwable).code() == 402)
            showPaymentError();
        else
            initExceptionHandler.showError(throwable, v -> applyChanges(updateModel));
    }

    private void showPaymentError() {
        new RxDialogHandler(getActivity())
                .showTwoButtonsWithoutTitle(R.string.close_tab_payment_error, R.string.change_card, R.string.pay_with_cash)
                .subscribe(isOk -> {if (!isOk) showChangePaymentCard(); }, e -> {}, () -> {});
    }

    private void showChangePaymentCard() {
        getActivity().startActivity(new Intent(getActivity(), PaymentsActivity.class));
    }
}
