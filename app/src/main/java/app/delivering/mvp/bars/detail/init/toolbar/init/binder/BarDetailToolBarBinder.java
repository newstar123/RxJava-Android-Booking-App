package app.delivering.mvp.bars.detail.init.toolbar.init.binder;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.R;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.bar.detail.about.activity.AboutBarViewActivity;
import app.delivering.component.bar.detail.adapter.pager.ViewPagerWithBlocking;
import app.delivering.component.bar.detail.photo.adapter.BarsDetailPhotosAdapter;
import app.delivering.component.bar.detail.transformers.ZoomOutPageTransformer;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.viewpager.events.OnSelectPagerPositionEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OpenPhotosGridEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewModel;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import app.delivering.mvp.bars.detail.init.toolbar.init.presenter.BarDetailToolBarPresenter;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;
import app.delivering.mvp.bars.list.init.enums.BarType;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailToolBarBinder extends BaseBinder {
    private BarDetailToolBarPresenter toolBarPresenter;
    private BarsDetailPhotosAdapter pagerAdapter;
    private AboutBarViewModel result;
    @BindView(R.id.bar_detail_name) TextView name;
    @BindView(R.id.bar_detail_type) TextView type;
    @BindView(R.id.bar_detail_route) TextView distance;
    @BindView(R.id.bar_detail_discount) TextView discount;
    @BindView(R.id.bar_detail_type_indicator) ImageView typeIndicator;
    @BindView(R.id.bar_detail_photos_transition) ImageView startedPhoto;
    @BindView(R.id.bar_detail_photos_view_pager) ViewPagerWithBlocking barScreensViewPager;
    @BindView(R.id.bar_detail_photos_spring_indicator) PageIndicatorView springIndicator;
    @BindView(R.id.bar_detail_expand_photos_title) TextView expandPhotosTitle;

    public BarDetailToolBarBinder(BarDetailActivity activity) {
        super(activity);
        toolBarPresenter = new BarDetailToolBarPresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        InitialVenueDetailModel initialModel = ((BarDetailActivity)getActivity()).getInitialModel();
        distance.setText(initialModel.getDistance());
        name.setText(initialModel.getNameValue());
        discount.setText(initialModel.getDiscount());
        int typeColor = Color.parseColor(BarType.toType(initialModel.getType()).getColor());
        type.setText(initialModel.getType());
        typeIndicator.setColorFilter(typeColor);
        if (!TextUtils.isEmpty(initialModel.getImage()))
            Picasso.with(getActivity()).load(initialModel.getImage()).centerCrop().fit().into(startedPhoto);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        showBarInformation(detailModel);
        toolBarPresenter.process(detailModel.getBarModel())
               .subscribeOn(AndroidSchedulers.mainThread())
               .subscribe(this::show, e->{}, ()->{});
    }

    private void show(AboutBarViewModel result) {
        pagerAdapter = new BarsDetailPhotosAdapter(getActivity(), result);
        this.result = result;
        barScreensViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        barScreensViewPager.setAdapter(pagerAdapter);
        barScreensViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                EventBus.getDefault().post(new OnStopPlayerEvent());
                tryUpdateTitle(position, result.getUrls());
                barScreensViewPager.setCurrentItem(position, true);
            }
        });
        initSpringIndicator(result.getUrls());
        barScreensViewPager.setPagingEnabled(true);
        tryUpdateTitle(0, result.getUrls());
    }

    private void tryUpdateTitle(int position, ArrayList<AboutBarViewTypeModel> typeModels) {
        if (expandPhotosTitle != null && typeModels != null)
            updateTitle(position, typeModels);
    }

    private void updateTitle(int position, ArrayList<AboutBarViewTypeModel> urls) {
        if (urls.get(position).isVideoUrl())
            expandPhotosTitle.setText(R.string.word_video);
        else {
            String title = String.format(getString(R.string.page_from_size), position + 1, urls.size());
            expandPhotosTitle.setText(title);
        }
    }

    private void showBarInformation(BarDetailModel model) {
        if (distance != null && TextUtils.isEmpty(distance.getText()))
            distance.setText(String.valueOf(model.getRouting()));
        name.setText(model.getBarModel().getName());
        discount.setText(model.getDiscountText());
        int typeColor = Color.parseColor(model.getBarModel().getType().getColor());
        type.setText(model.getBarModel().getType().getName().toString());
        typeIndicator.setColorFilter(typeColor);
    }

    private void initSpringIndicator(ArrayList<AboutBarViewTypeModel> urls) {
        springIndicator.setViewPager(barScreensViewPager);
        if (urls == null || urls.isEmpty() || urls.size() <= 1)
            springIndicator.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openAboutBarPhotoViewer(OpenPhotosGridEvent openPhotosGridEvent) {
        Intent intent = new Intent(getActivity(), AboutBarViewActivity.class);
        intent.putExtra(AboutBarViewActivity.ABOUT_BAR_URLS_MODEL, result.getVideoUrl());
        intent.putExtra(AboutBarViewActivity.ABOUT_BAR_URLS_LIST, result.getUrls());
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.animation_slide_to_top, R.anim.animation_stay);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void destroyFragment(OnSelectPagerPositionEvent event) {
        EventBus.getDefault().removeStickyEvent(OnSelectPagerPositionEvent.class);
        if (barScreensViewPager != null)
            barScreensViewPager.setCurrentItem(event.getPosition(), true);
    }
}
