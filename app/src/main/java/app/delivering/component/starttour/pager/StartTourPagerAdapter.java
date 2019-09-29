package app.delivering.component.starttour.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.starttour.fragment.StartTourFragment;

public class StartTourPagerAdapter extends FragmentStatePagerAdapter {
    public static final String START_TOUR_VIEW_ID = "app.delivering.component.starttour.START_TOUR_VIEW_ID";
    public static final String START_TOUR_TITLE = "app.delivering.component.starttour.START_TOUR_TITLE";
    public static final String START_TOUR_MESSAGE = "app.delivering.component.starttour.START_TOUR_MESSAGE";
    private List<Fragment> fragments;

    public StartTourPagerAdapter(BaseActivity activity) {
        super(activity.getSupportFragmentManager());
        int[] viewIds = new int[] {
                R.drawable.city_background_first,
                R.drawable.city_background_second,
                R.drawable.city_background_third,
                R.drawable.city_background_fourth
        };
        final String[] titleList = activity.getResources().getStringArray(R.array.start_tour_titles);
        final String[] messageList = activity.getResources().getStringArray(R.array.start_tour_messages);
        createFragmentsList(viewIds, titleList, messageList);
        notifyDataSetChanged();
    }

    private void createFragmentsList(int[] viewIds, String[] titleList, String[] messageList) {
        fragments = new ArrayList<>();
        for (int i = 0; i < titleList.length; i++){
            Bundle bundle = new Bundle();
            bundle.putInt(START_TOUR_VIEW_ID, viewIds[i]);
            bundle.putString(START_TOUR_TITLE, titleList[i]);
            bundle.putString(START_TOUR_MESSAGE, messageList[i]);
            StartTourFragment fragment = new StartTourFragment();
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
