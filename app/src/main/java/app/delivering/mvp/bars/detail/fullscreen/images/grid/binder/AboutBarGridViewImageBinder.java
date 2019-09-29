package app.delivering.mvp.bars.detail.fullscreen.images.grid.binder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.about.activity.AboutBarViewActivity;
import app.delivering.component.bar.detail.about.grid.adapter.AboutBarGridViewAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.viewpager.events.OnSelectPagerPositionEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import butterknife.BindView;

public class AboutBarGridViewImageBinder extends BaseBinder {
    @BindView(R.id.bar_detail_photos_grid) RecyclerView recyclerPhoto;
    private AboutBarGridViewAdapter adapter;

    public AboutBarGridViewImageBinder(BaseActivity activity) {
        super(activity);
        adapter = new AboutBarGridViewAdapter();
    }

    @Override public void afterViewsBounded() {
        getActivity().getSupportActionBar().setTitle("");
        recyclerPhoto.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerPhoto.setAdapter(adapter);
        ArrayList<AboutBarViewTypeModel> urls = getActivity().getIntent().getParcelableArrayListExtra(AboutBarViewActivity.ABOUT_BAR_URLS_LIST);
        adapter.setModel(urls);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSelectItem(OnSelectPagerPositionEvent event) {
        getActivity().finish();
    }
}
