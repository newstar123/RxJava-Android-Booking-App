package app.delivering.mvp.ride.order.address.enter.binder;


import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class OnAddressEnterBinder extends BaseBinder{
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;

    public OnAddressEnterBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        orderRideAddressEditText.setOnEditorActionListener(this::onEditorActionListener);
        orderPickUpAddress.setOnEditorActionListener(this::onEditorActionListener);
    }

    private boolean onEditorActionListener(TextView textView, int actionId, KeyEvent keyEvent) {
        return wasEnterPressed(actionId, keyEvent);
    }

    private boolean wasEnterPressed(int actionId, KeyEvent keyEvent) {
        return (actionId == EditorInfo.IME_ACTION_DONE)
                || ((keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                && (keyEvent.getAction() == KeyEvent.ACTION_DOWN));
    }
}
