package app.qamode.mvp.exit.binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.mvp.environment.server.event.ResetEnvironmentEvent;
import app.qamode.mvp.exit.event.OnExitQaModeEvent;

public class ExitQaModeBinder extends BaseBinder {

    public ExitQaModeBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(OnExitQaModeEvent event) {
        if (QaModeCache.getQaModeCustomEnvironment().get(BaseCacheType.BOOLEAN)){
            restoreDefaultState();
        } else {
            exit();
        }
    }

    private void restoreDefaultState() {
        int savedHostPosition = QaModeCache.getQaModeServerHost().get(BaseCacheType.INT);
        int savedTypePosition = QaModeCache.getQaModeConnectionType().get(BaseCacheType.INT);
        int savedUrnPosition = QaModeCache.getQaModeRootUrn().get(BaseCacheType.INT);
        if (savedHostPosition != 0 || savedTypePosition != 0 || savedUrnPosition != 0) {
            EventBus.getDefault().post(new ResetEnvironmentEvent());
        } else {
            exit();
        }
    }

    private void exit() {
        QaModeCache.isQaModeActive().reset();
        getActivity().finishAfterTransition();
    }

}
