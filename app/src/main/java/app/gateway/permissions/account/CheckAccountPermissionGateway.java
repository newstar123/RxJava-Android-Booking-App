package app.gateway.permissions.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import app.delivering.component.BaseActivity;
import app.delivering.component.main.account.InitialAccountReasonActivity;
import app.delivering.mvp.main.account.InitialAccountReasonBinder;
import app.gateway.permissions.PermissionGatewayImpl;

public class CheckAccountPermissionGateway extends PermissionGatewayImpl {

    public CheckAccountPermissionGateway(BaseActivity activity) {
        super(activity, Manifest.permission.GET_ACCOUNTS);
    }

    @Override
    public void onAccessGranted(Context context) {
    }

    @Override
    public void onAccessDenied(Context context) {
        context.startActivity(new Intent(context, InitialAccountReasonActivity.class));
    }

    @Override
    public void onDoNotAskAgainActivated(Context context) {
        Intent intent = new Intent(context, InitialAccountReasonActivity.class);
        intent.putExtra(InitialAccountReasonBinder.provideToAppSettings, true);
        context.startActivity(intent);
    }
}
