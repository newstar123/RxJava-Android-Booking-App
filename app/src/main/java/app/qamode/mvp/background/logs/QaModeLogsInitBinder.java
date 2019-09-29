package app.qamode.mvp.background.logs;

import android.Manifest;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.File;
import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import app.qamode.mvp.validator.EditValidator;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class QaModeLogsInitBinder extends BaseBinder {
    private static final String TXT = ".txt";
    @BindView(R.id.log_activation_switcher) SwitchCompat switcher;
    @BindView(R.id.qa_mode_logs_expand_view) TextView expandHint;
    @BindView(R.id.qa_mode_logs_expand_container) LinearLayout settingsContainer;
    @BindView(R.id.qa_mode_logs_path_layout) TextInputLayout pathLayout;
    @BindView(R.id.qa_mode_logs_path_input) EditText path;
    @BindView(R.id.qa_mode_logs_file_layout) TextInputLayout fileLayout;
    @BindView(R.id.qa_mode_logs_file_input) EditText file;
    @BindView(R.id.qa_mode_logs_reset) Button restore;
    @BindView(R.id.qa_mode_logs_save) Button save;
    private final RxPermissions rxPermissions;
    private String initialPathValue;
    private String initialFileValue;

    public QaModeLogsInitBinder(BaseActivity activity) {
        super(activity);
        rxPermissions = new RxPermissions(activity);
    }

    @Override public void afterViewsBounded() {
        fillingFields();
        switcher.setChecked(QaModeCache.getQaModeLogs().get(BaseCacheType.BOOLEAN));
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> updateState(isChecked));
        setViewsVisibility(switcher.isChecked());
        new EditValidator(getActivity()).createSimpleValidator(path, pathLayout)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> checkValidation(), e->{}, ()->{});
        new EditValidator(getActivity()).createSimpleValidator(file, fileLayout)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> checkValidation(), e->{}, ()->{});
        checkRestoreState();
    }

    private void fillingFields() {
        initialPathValue = QaModeCache.getQaModeLogsPath().get(BaseCacheType.STRING);
        initialFileValue = QaModeCache.getQaModeLogsFile().get(BaseCacheType.STRING);
        path.setText(initialPathValue);
        file.setText(initialFileValue);
    }

    private void updateState(boolean isChecked) {
        if (isChecked)
            if (!rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) || !rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE))
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(isOk-> switcher.setChecked(false));
        QaModeCache.getQaModeLogs().save(BaseCacheType.BOOLEAN, isChecked);
        setViewsVisibility(isChecked);
    }

    private void setViewsVisibility(boolean checked) {
        expandHint.setVisibility(checked ? View.GONE : View.VISIBLE);
        settingsContainer.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(pathLayout.getError()) && TextUtils.isEmpty(fileLayout.getError())){ //validation results
            if (initialPathValue.equals(path.getText().toString()) && initialFileValue.equals(file.getText().toString()))//find changes
                save.setVisibility(View.GONE);
            else
                save.setVisibility(View.VISIBLE);
        } else  save.setVisibility(View.GONE);
    }

    @OnClick(R.id.qa_mode_logs_clear) void onClearButton(){
        showProgress();
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .map(aLong -> {
                    String path = QaModeCache.getQaModeLogsPath().get(BaseCacheType.STRING);
                    String fileName = QaModeCache.getQaModeLogsFile().get(BaseCacheType.STRING);
                    File file = new File(path + fileName);
                    return file.delete();
                })
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isDeleted -> Toast.makeText(getActivity(), isDeleted ?
                        getString(R.string.qa_mode_logs_clear_success) :
                        getString(R.string.qa_mode_logs_clear_error),
                        Toast.LENGTH_SHORT).show(), e->hideProgress(),
                        this::hideProgress);
    }

    @OnClick(R.id.qa_mode_logs_save) void onSaveButton(){
        showProgress();
        Observable.timer(700, TimeUnit.MILLISECONDS)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isDeleted -> {
                    updateValues(path.getText().toString(), file.getText().toString());
                    save.setVisibility(View.GONE);
                    }, e->hideProgress(), this::hideProgress);
    }

    @OnClick(R.id.qa_mode_logs_reset) void onRestoreButton(){
        showProgress();
        Observable.timer(700, TimeUnit.MILLISECONDS)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isDeleted -> {
                    updateValues(getString(R.string.qa_mode_logs_def_path), getString(R.string.qa_mode_logs_def_file_name));
                    path.setText(initialPathValue);
                    file.setText(initialFileValue);
                    restore.setVisibility(View.GONE);
                }, e->hideProgress(), this::hideProgress);

    }

    private void updateValues(String path, String fileName) {
        if (!fileName.contains(TXT))
            fileName = fileName + TXT;
        initialPathValue = path;
        initialFileValue = fileName;
        QaModeCache.getQaModeLogsPath().save(BaseCacheType.STRING, path);
        QaModeCache.getQaModeLogsFile().save(BaseCacheType.STRING, fileName);
        checkRestoreState();
        if (!LogToFileHandler.addLog("New Logs")) {
            Toast.makeText(getActivity(), getString(R.string.qa_mode_logs_declined), Toast.LENGTH_LONG).show();
            pathLayout.setError(getString(R.string.qa_mode_logs_declined));
            pathLayout.setErrorEnabled(true);
            pathLayout.requestFocus();
        }
    }

    private void checkRestoreState() {
        if (initialPathValue.equals(getString(R.string.qa_mode_logs_def_path)) &&
                initialFileValue.equals(getString(R.string.qa_mode_logs_def_file_name)))
            restore.setVisibility(View.GONE);
        else
            restore.setVisibility(View.VISIBLE);
    }
}
