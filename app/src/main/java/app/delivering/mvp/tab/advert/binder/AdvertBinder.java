package app.delivering.mvp.tab.advert.binder;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.ride.delayed.discount.model.DiscountUpdatesModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.tab.advert.presenter.AdvertImagePresenter;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class AdvertBinder extends BaseBinder {
    @BindView(R.id.advert_tab_view)
    ImageView advertImage;
    @BindView(R.id.tab_information_container)
    RelativeLayout tabContent;
    private AdvertImagePresenter imagePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<DiscountUpdatesModel> replaySubject;

    public AdvertBinder(BaseActivity activity) {
        super(activity);
        imagePresenter = new AdvertImagePresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Override
    public void afterViewsBounded() {
        imagePresenter.process(0d)
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadAdvert(StartTabEvent event) {
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private void show(DiscountUpdatesModel response) {
        Picasso picasso = Picasso.with(getActivity());
        picasso.load(response.getAdvertResponse().getData().getAttributes().getMyTabAdvert())
                .error(getActivity().getDrawable(R.drawable.inset_broken_resource))
                .into(advertImage);
    }

    private void onError(Throwable throwable) {
        replaySubject = ReplaySubject.create();
        initExceptionHandler.showError(throwable, view -> loadAdvert(new StartTabEvent()));
    }
}
