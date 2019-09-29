package app.delivering.component.bar.detail.action;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import app.R;

public class CustomBottomActionButton extends FrameLayout {
    private BarDetailActionButtonState actionState;

    public CustomBottomActionButton(Context context) {
        super(context);
    }

    public CustomBottomActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarDetailActionButtonState barDetailActionState() {
        return actionState;
    }

    public void setActionState(BarDetailActionButtonState actionState) {
        this.actionState = actionState;
        ImageView icon = findViewById(R.id.bar_detail_action_button_icon);
        icon.setBackground(null);
        icon.setBackgroundResource(android.R.color.transparent);
        TextView uberText = findViewById(R.id.bar_detail_action_button_uber_text);
        uberText.setVisibility(GONE);
        TextView titleText = findViewById(R.id.bar_detail_action_button_title_text);
        titleText.setVisibility(VISIBLE);
        titleText.setTextColor(getResources().getColor(android.R.color.white));
        switch (actionState) {
            case CLOSED:
                setBackgroundResource(R.drawable.shape_5b606f_corner_5);
                titleText.setTextColor(getResources().getColor(R.color.color_50_ffffff));
                titleText.setText(getResources().getString(R.string.venue_closed));
                break;
            case OPEN_TAB:
                setBackgroundResource(R.drawable.shape_00a9e3_51d767_corner_5);
                icon.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dip20);
                icon.getLayoutParams().width = getResources().getDimensionPixelOffset(R.dimen.dip20);
                icon.setBackgroundResource(R.drawable.inset_open_tab);
                titleText.setText(getResources().getString(R.string.open_tab));
                break;
            case VIEW_TAB:
                setBackgroundResource(R.drawable.shape_00a9e3_51d767_corner_5);
                icon.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dip20);
                icon.getLayoutParams().width = getResources().getDimensionPixelOffset(R.dimen.dip20);
                icon.setBackgroundResource(R.drawable.inset_icon_view);
                titleText.setText(getResources().getString(R.string.view_tab));
                break;
            default:
                setBackgroundResource(R.drawable.shape_123e4c_corner_5);
                icon.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                icon.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
                icon.setBackgroundResource(R.drawable.inset_uber_white_full);
                uberText.setVisibility(VISIBLE);
                titleText.setVisibility(GONE);
        }
    }

    public String getUberEstimation() {
        TextView uberText = findViewById(R.id.bar_detail_action_button_uber_text);
        return uberText.getText().toString();
    }

    public void setUberEstimation(String uberEstimation) {
        TextView uberText = findViewById(R.id.bar_detail_action_button_uber_text);
        uberText.setText(uberEstimation);
    }
}
