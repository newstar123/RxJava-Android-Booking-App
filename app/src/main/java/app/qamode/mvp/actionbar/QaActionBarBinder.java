package app.qamode.mvp.actionbar;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.transformers.DepthPageTransformer;
import app.delivering.mvp.BaseBinder;
import app.qamode.mvp.QaModeTopicsPagerAdapter;
import butterknife.BindView;

public class QaActionBarBinder extends BaseBinder {
    @BindView(R.id.qa_mode_toolbar) Toolbar toolBar;
    @BindView(R.id.qa_mode_tab_layout) TabLayout tabLayout;
    @BindView(R.id.qa_mode_root_view_pager) ViewPager viewPager;
    private QaModeTopicsPagerAdapter adapter;

    public QaActionBarBinder(BaseActivity activity) {
        super(activity);
        adapter = new QaModeTopicsPagerAdapter(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        getActivity().getSupportActionBar().setTitle(getString(R.string.qa_mode_menu_button));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
