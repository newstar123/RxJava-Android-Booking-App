package app.qamode.mvp.environment.uber;

import android.view.View;
import android.widget.ToggleButton;

import app.CustomApplication;
import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class UberApiUsageTypeBinder extends BaseBinder {
    @BindView(R.id.uber_api_type_toggle_button) ToggleButton controllButton;
    @BindView(R.id.uber_api_type_progress) MaterialProgressBar progressBar;

    public UberApiUsageTypeBinder(BaseActivity activity) {
        super(activity);
    }

    @Override
    public void afterViewsBounded() {
        controllButton.setChecked(QaModeCache.getQaModeUberApiState().get(BaseCacheType.BOOLEAN));
        controllButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            progressBar.setVisibility(View.VISIBLE);
            tryToChangeUberApiType(isChecked);
        });
    }

    private void tryToChangeUberApiType(boolean isChecked) {
        progressBar.setVisibility(View.GONE);
        QaModeCache.getQaModeUberApiState().save(BaseCacheType.BOOLEAN, isChecked);
        CustomApplication.get().initUberSession();
    }
}
