package app.delivering.mvp.main.init.binder;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.uber.sdk.android.core.auth.AuthenticationError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import app.BuildConfig;
import app.R;
import app.core.init.token.entity.CancelFacebookLoginException;
import app.core.init.token.entity.NoAccountException;
import app.core.login.facebook.entity.Younger21Exception;
import app.core.permission.bluetooth.entity.BluetoothPermissionException;
import app.core.permission.camera.entity.CameraPermissionException;
import app.core.permission.entity.CallPhonePermissionException;
import app.core.permission.entity.GetAccountPermissionException;
import app.core.permission.entity.LocationSettingsException;
import app.core.permission.entity.NetworkSettingsException;
import app.core.permission.storage.entitty.ExternalStoragePermissionException;
import app.core.uber.auth.entity.UberAuthException;
import app.core.verify.interactor.mail.entity.EmailAlreadyInUseException;
import app.delivering.component.BaseActivity;
import app.delivering.component.main.account.InitialAccountReasonActivity;
import app.delivering.component.main.location.InitialLocationReasonActivity;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.dialog.base.binder.BaseDialogBinder;
import app.delivering.mvp.dialog.base.events.ShowNotificationDialogEvent;
import app.delivering.mvp.main.init.model.ResponseErrModel;
import app.delivering.mvp.main.location.reason.model.InitialLocationReasonError;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDeparturePointException;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDestinationPointException;
import app.qamode.log.LogToFileHandler;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class InitExceptionHandler {
    private static final String SERVER_ERRORS_KEY = "errors";
    private static final String SERVER_ERRORS_DETAIL_KEY = "detail";
    private static final String SERVER_ERRORS_STATUS = "status";
    private static final String SERVER_ERRORS_META = "meta";
    private static final String SERVER_ERRORS_MESSAGE = "message";
    private static final String SERVER_ERRORS_TITLE = "title";
    private static final String SERVER_ERRORS_CODE = "code";

    private static final String UNPROCESSABLE_ERROR =  "HTTP 422 Unprocessable Entity";
    private static final int UnprocessableErrorCode = 422;

    private BaseActivity activity;
    private View.OnClickListener onClickListener;
    private RxDialogHandler dialogHandler;


    public InitExceptionHandler(BaseActivity activity) {
        dialogHandler = new RxDialogHandler(activity);
        this.activity = activity;
    }

    public void showError(Throwable e, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        View progress = activity.findViewById(R.id.progress);
        if (!(e instanceof NoAccountException) && progress != null)
            progress.setVisibility(View.GONE);
        if (e instanceof GetAccountPermissionException)
            activity.startActivity(new Intent(activity, InitialAccountReasonActivity.class));
        else if (e instanceof InitialLocationReasonError)
            activity.startActivity(new Intent(activity, InitialLocationReasonActivity.class));
        else if (e instanceof Younger21Exception)
            showDialog(activity.getString(R.string.error_age_not_valid), onClickListener);
        else if (e instanceof CallPhonePermissionException)
            showMessageDialog(R.string.permission_call_phone_reason, R.string.enable);
        else if (e instanceof ExternalStoragePermissionException)
            showMessageDialog(R.string.error_storage_rationale, R.string.enable);
        else if (e instanceof CameraPermissionException)
            showMessageDialog(R.string.error_camera_rationale, R.string.enable);
        else if (e instanceof BluetoothPermissionException)
            showMessageDialog(R.string.error_bluetooth_rationale, R.string.enable);
        else if (e instanceof EmptyDeparturePointException)
            showMessageDialog(R.string.no_departure_position, R.string.ok);
        else if (e instanceof EmptyDestinationPointException)
            showMessageDialog(R.string.no_destination_position, R.string.ok);
        else if (e instanceof UberAuthException && ((UberAuthException) e).getError() == AuthenticationError.CANCELLED)
            activity.finish();
        else if (e instanceof NoAccountException) {
        } else if (e instanceof NetworkSettingsException){
        } else if (e instanceof CancelFacebookLoginException) {
        } else if (e instanceof HttpException) {
            if (((HttpException) e).code() == UnprocessableErrorCode && e.getMessage().equals(UNPROCESSABLE_ERROR))
                showRouteErrorDialog();
            else
                showServerErrorDialog((HttpException) e, onClickListener);
        } else if (!(e instanceof LocationSettingsException) && e != null)
            showMessageDialog(BuildConfig.DEBUG ? e.getClass().getSimpleName() + ", message-" + e.getMessage() : R.string.request_error, R.string.retry);
    }

    private void showRouteErrorDialog() {
        dialogHandler.showOneButtonWithoutTitle(R.string.route_error, R.string.ok)
                .subscribe(onSuccess -> dialogHandler.dismissDialog(), err -> dialogHandler.dismissDialog(), () -> {});
    }

    public void showErrorWithDuration(Throwable e, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (e instanceof NetworkSettingsException){
        } if (e instanceof EmailAlreadyInUseException) {
            showMessageDialog(R.string.error_email_in_use, R.string.ok);
        }else if (e instanceof CancelFacebookLoginException) {
        } else if (!(e instanceof LocationSettingsException))
            showMessageDialog(BuildConfig.DEBUG ? e.getClass().getSimpleName() + ", message-" + e.getMessage() : R.string.request_error, R.string.retry);
    }

    private void showMessageDialog(Object bodyId, int actionId) {
        String body = (bodyId instanceof String) ? (String) bodyId : activity.getString((int)bodyId);
        dialogHandler.showOneButtonWithoutTitle(body, actionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(isOk -> onClickListener.onClick(new View(activity)), this::onError);
    }

    private void onError(Throwable throwable) {
        throwable.getCause();
        throwable.printStackTrace();
    }

    public void dismiss() {
        if (dialogHandler != null)
            dialogHandler.dismissDialog();
    }

    public ResponseErrModel parseResponseError(HttpException exception) {
        ResponseErrModel responseErrModel = new ResponseErrModel();
        try {
            JSONObject error = new JSONObject(exception.response().errorBody().string());
            JSONArray errorArray = new JSONArray(error.getString(SERVER_ERRORS_KEY));
            JSONObject jsonStatusVal = new JSONObject(errorArray.getString(0));
            JSONObject jsonMetaVal = new JSONObject(jsonStatusVal.getString(SERVER_ERRORS_META));

            responseErrModel.setCode(Integer.parseInt(jsonStatusVal.getString(SERVER_ERRORS_CODE)));
            responseErrModel.setMetaMessage(jsonMetaVal.getString(SERVER_ERRORS_MESSAGE));
            responseErrModel.setStatus(jsonStatusVal.getString(SERVER_ERRORS_STATUS));
            responseErrModel.setTitle(jsonStatusVal.getString(SERVER_ERRORS_TITLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseErrModel;
    }

    private void showServerErrorDialog(HttpException exception, View.OnClickListener onClickListener) {
        try {
            JSONObject error = new JSONObject(exception.response().errorBody().string());
            JSONArray errorArray = new JSONArray(error.getString(SERVER_ERRORS_KEY));
            JSONObject errorObj = new JSONObject(errorArray.get(0).toString());
            showDialog(errorObj.getString(SERVER_ERRORS_DETAIL_KEY), onClickListener);
        } catch (Exception e) {
            showDialog(activity.getString(R.string.default_http_error_message), onClickListener);
            e.printStackTrace();
        }
    }

    private void showDialog(String message, @Nullable View.OnClickListener listener) {
        LogToFileHandler.addLog("QorumNotifier - serverError: ".concat(message));

        HashMap map = new HashMap();
        map.put(QorumNotifier.MESSAGE, message);
        new BaseDialogBinder(activity).onShowDialogEvent(new ShowNotificationDialogEvent(NotificationType.DEF, map), listener);
    }
}
