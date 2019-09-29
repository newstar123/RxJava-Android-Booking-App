package app.qamode.mvp.background.checkout;

import android.support.v7.widget.SwitchCompat;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;

public class AutoCheckoutDistanceVisibilityBinder extends BaseBinder {
    @BindView(R.id.checkout_distance_switcher) SwitchCompat switcher;

    public AutoCheckoutDistanceVisibilityBinder(BaseActivity activity) {
        super(activity);
    }

    @Override
    public void afterViewsBounded() {
        switcher.setChecked(QaModeCache.getQaModeAutoCheckoutDistanceVisibility().get(BaseCacheType.BOOLEAN));
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> QaModeCache.getQaModeAutoCheckoutDistanceVisibility().save(BaseCacheType.BOOLEAN, isChecked));
    }
}
