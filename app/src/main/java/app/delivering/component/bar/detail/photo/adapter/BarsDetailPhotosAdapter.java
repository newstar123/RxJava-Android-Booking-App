package app.delivering.component.bar.detail.photo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.photo.BarDetailPhotoFragment;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewModel;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;

public class BarsDetailPhotosAdapter extends FragmentStatePagerAdapter {
    public static final String BAR_DETAIL_PHOTO_MODEL = "BAR_DETAIL_PHOTO_MODEL";
    public static final String BAR_DETAIL_VIDEO_URL = "BAR_DETAIL_VIDEO_URL";
    public static final String BAR_DETAIL_PROMOTION = "BAR_DETAIL_PROMOTION";
    public static final String BAR_DETAIL_PHOTO_POSITION = "BAR_DETAIL_PHOTO_POSITION";
    private final List<Fragment> fragments;
    private final List<AboutBarViewTypeModel> urls;

    public BarsDetailPhotosAdapter(BaseActivity activity, AboutBarViewModel result) {
        super(activity.getSupportFragmentManager());
        fragments = new ArrayList<>();
        urls = result.getUrls();
        for (int i=0; i< urls.size(); i++){
            BarDetailPhotoFragment photoFragment = new BarDetailPhotoFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BAR_DETAIL_PHOTO_MODEL, result.getUrls());
            bundle.putString(BAR_DETAIL_VIDEO_URL, result.getVideoUrl());
            bundle.putString(BAR_DETAIL_PROMOTION, result.getPromotion());
            bundle.putInt(BAR_DETAIL_PHOTO_POSITION, i);
            photoFragment.setArguments(bundle);
            fragments.add(photoFragment);
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return urls.get(position).getUrl();
    }
}
