package app.delivering.mvp.bars.detail.init.friends.init.binder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.friends.entity.WhoseHereModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.bar.detail.friends.adapter.FriendsPhotoListAdapter;
import app.delivering.component.settings.SettingsFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.signup.events.SignUpFromBarDetailEvent;
import app.delivering.mvp.bars.detail.init.friends.init.presenter.BarDetailCheckinFriendsPresenter;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.coach.login.init.events.HideFBLoginCoachMarkEvent;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.settings.init.events.DestroySettingsEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailCheckInFriendsBinder extends BaseBinder {
    @BindView(R.id.check_in_friends_recycler) RecyclerView recyclerPhoto;
    @BindView(R.id.bar_detail_friends_list) LinearLayout container;
    @BindView(R.id.see_who_is_here_authorization_button) Button authorization;
    @BindView(R.id.see_who_is_here_fb_visibility_button) Button visibility;
    @BindView(R.id.see_who_is_here) TextView seeText;
    private FriendsPhotoListAdapter adapter;
    private BarDetailCheckinFriendsPresenter presenter;

    public BarDetailCheckInFriendsBinder(BaseActivity activity) {
        super(activity);
        adapter = new FriendsPhotoListAdapter();
        presenter = new BarDetailCheckinFriendsPresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        recyclerPhoto.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerPhoto.setAdapter(adapter);
        visibility.setText(formatHtmlToSpanned(R.string.visibility_facebook));
        authorization.setText(formatHtmlToSpanned(R.string.connect_facebook));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        loadSection(detailModel.getBarModel().getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(DestroySettingsEvent destroySettingsEvent) {
        EventBus.getDefault().removeStickyEvent(destroySettingsEvent);
        Long id = ((BarDetailActivity)getActivity()).getInitialModel().getBarId();
        loadSection(id);
    }

    private void loadSection(long id) {
        presenter.process(id)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private void show(WhoseHereModel whoseHereModel) {
        adapter.setModels(whoseHereModel.getPeople());

        if (whoseHereModel.getPeople().isEmpty()) {
            container.setVisibility(View.GONE);
            seeText.setVisibility(View.GONE);
        } else
            seeText.setVisibility(View.VISIBLE);

        if (whoseHereModel.isAuthorized()) {
            authorization.setVisibility(View.GONE);
            ButterKnife.apply(visibility, whoseHereModel.isFBVisible() ? ViewActionSetter.GONE : ViewActionSetter.VISIBLE);
        } else {
            authorization.setVisibility(View.VISIBLE);
            ButterKnife.apply(visibility, ViewActionSetter.GONE);
        }
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnClick(R.id.see_who_is_here_authorization_button) void login() {
        EventBus.getDefault().post(new HideFBLoginCoachMarkEvent());
        EventBus.getDefault().post(new SignUpFromBarDetailEvent(false));
    }

    @OnClick(R.id.see_who_is_here_fb_visibility_button) void openFBSettings() {
        SettingsFragment settingsFragment = new SettingsFragment();
        getActivity().start(settingsFragment);
    }
}
