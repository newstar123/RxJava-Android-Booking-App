package app.delivering.mvp.profile.edit.actionbar.clicks.binder;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class ViewActionSetter {
    public static final ButterKnife.Action<View> ENABLE = (view, index) -> view.setEnabled(true);
    public static final ButterKnife.Action<View> DISABLE = (view, index) -> view.setEnabled(false);
    public static final ButterKnife.Action<View> VISIBLE = (view, index) -> view.setVisibility(View.VISIBLE);
    public static final ButterKnife.Action<View> INVISIBLE = (view, index) -> view.setVisibility(View.INVISIBLE);
    public static final ButterKnife.Action<View> GONE = (view, index) -> view.setVisibility(View.GONE);
}
