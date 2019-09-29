package app.delivering.mvp.bars.detail.init.tablist.list.inside.binder;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.bar.detail.fragment.feature.adapter.BarDetailFeaturesAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailInsideTipsModel;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.enums.FeatureType;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.model.FeaturesModel;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.presenter.BarDetailFeaturePresenter;
import app.delivering.mvp.bars.detail.init.tablist.list.inside.model.BarDetailInsideTipsHeightEvent;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailInsideTipsListBinder extends BaseBinder {
    private BarDetailFeaturePresenter featurePresenter;
    private final BarDetailFeaturesAdapter adapter;
    @BindView(R.id.bar_detail_inside_tips) RecyclerView recyclerView;
    @BindView(R.id.bar_detail_inside_tips_drink) LinearLayout drinkContainer;
    @BindView(R.id.bar_detail_inside_tips_crowd) LinearLayout crowdContainer;
    @BindView(R.id.bar_detail_inside_tips_list) RelativeLayout container;
    @BindView(R.id.bar_detail_inside_tips_drink_text) TextView drinkText;
    @BindView(R.id.bar_detail_inside_tips_crowd_text) TextView crowdText;
    private int drinkHeight;
    private int crowdHeight;

    public BarDetailInsideTipsListBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
        adapter = new BarDetailFeaturesAdapter();
        featurePresenter = new BarDetailFeaturePresenter(getActivity());
        drinkHeight = 0;
        crowdHeight = 0;
    }

    @Override public void afterViewsBounded() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadList(BarDetailInsideTipsModel listModel) {
        featurePresenter.process(listModel.getList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initList, err -> { }, () -> {

                });
    }

    private void initList(List<FeaturesModel> featuresModels) {
        ArrayList<FeaturesModel> models = new ArrayList<>();
        ArrayList<FeaturesModel> footers = new ArrayList<>();
        for (FeaturesModel model : featuresModels) {
            if (model.getType() == FeatureType.whatToDrink || model.getType() == FeatureType.crowd)
                footers.add(model);
            else
                models.add(model);
        }
        if (!footers.isEmpty())
            showFooter(models, footers);
        adapter.setModels(models);
    }

    private void showFooter(ArrayList<FeaturesModel> models, ArrayList<FeaturesModel> footers) {
        addFooterText(footers);
        sendVisibleTipsHeight(models);
    }

    private void addFooterText(ArrayList<FeaturesModel> footers) {
        for (FeaturesModel model : footers) {
            if (model.getType() == FeatureType.whatToDrink)
                drinkText.setText(getCapitalizedText(model.getTextValue()));
            if (model.getType() == FeatureType.crowd)
                crowdText.setText(getCapitalizedText(model.getTextValue()));
        }
    }

    private String getCapitalizedText(String value) {
        return TextUtils.isEmpty(value) ? "" : value.substring(0,1).toUpperCase() + value.substring(1);
    }

    private void sendVisibleTipsHeight(ArrayList<FeaturesModel> models) {
        getFullHeight()
        .subscribe(isDraw -> {
            BarDetailInsideTipsHeightEvent event = new BarDetailInsideTipsHeightEvent();
            event.setListSize(models.size());
            event.setFooterHight(getVisibleHeight());
            EventBus.getDefault().post(event);
        }, e->{}, ()->{});
    }

    private int getVisibleHeight() {
        int footerHeight = 0;
        if (TextUtils.isEmpty(drinkText.getText()))
            drinkContainer.setVisibility(View.GONE);
        else
            footerHeight = footerHeight + drinkHeight;
        if (TextUtils.isEmpty(crowdText.getText()))
            crowdContainer.setVisibility(View.GONE);
        else
            footerHeight = footerHeight + crowdHeight;
        return footerHeight;
    }

    private Observable<Boolean> getFullHeight() {
        return Observable.zip(getItemHeight(drinkText), getItemHeight(crowdText), (drink, crowd) -> {
            drinkHeight = drink;
            crowdHeight = crowd;
            return true;
        });
    }

    private Observable<Integer> getItemHeight(TextView text) {
        return Observable.create(subscriber -> {
            ViewTreeObserver obs = text.getViewTreeObserver();
            obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw () {
                    int height = text.getLineCount()*text.getLineHeight() + getActivity().getResources().getDimensionPixelSize(R.dimen.dip44);
                    text.getViewTreeObserver().removeOnPreDrawListener(this);
                    subscriber.onNext(height);
                    subscriber.onCompleted();
                    return true;
                }
            });
        });
    }
}
