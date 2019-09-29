package app.delivering.mvp.coach.login.check.binder;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.R;
import app.core.coach.entity.BooleanResponse;
import app.core.coach.login.check.interactor.CheckLoginCoachMarkInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.coach.login.FacebookLoginCoachMarkFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.get.events.BarDetailResumeEvent;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CheckLoginCoachMarkBinder extends BaseBinder {
    private final CheckLoginCoachMarkInteractor coachMarkInteractor;
    @BindView(R.id.bar_detail_scroll_view) NestedScrollView scrollView;
    @BindView(R.id.bar_detail_app_bar) AppBarLayout toolBar;
    @BindView(R.id.bar_detail_content) LinearLayout content;
    @BindView(R.id.bar_detail_friends_list) LinearLayout friends;

    public CheckLoginCoachMarkBinder(BaseActivity activity) {
        super(activity);
        coachMarkInteractor = new CheckLoginCoachMarkInteractor(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowEvent(BarDetailResumeEvent event) {
        coachMarkInteractor.process()
                .concatMap(this::getScrollHeight)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::scrollOn, e-> {}, ()->{});
    }

    private Observable<Integer> getScrollHeight(BooleanResponse response) {
        return Observable.zip(getContentHeight(), getFriendsHeight(),
                       (scrollHeight, friendsHeight) ->
                               scrollHeight - (friendsHeight + getActivity().getResources().getDimensionPixelSize(R.dimen.dip120)));
    }

    private Observable<Integer> getFriendsHeight() {
        return Observable.create(subscriber -> {
            friends.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    friends.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    subscriber.onNext(friends.getHeight());
                    subscriber.onCompleted();
                }
            });
        });
    }

    private Observable<Integer> getContentHeight() {
        return Observable.create(subscriber -> {
            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    subscriber.onNext(content.getHeight());
                    subscriber.onCompleted();
                }
            });
        });
    }

    private void scrollOn(Integer height) {
        toolBar.setExpanded(false);
        scrollView.smoothScrollTo(0, height);
        getActivity().start(new FacebookLoginCoachMarkFragment());
    }

}
