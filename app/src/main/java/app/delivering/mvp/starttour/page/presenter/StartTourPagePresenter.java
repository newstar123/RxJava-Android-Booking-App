package app.delivering.mvp.starttour.page.presenter;

import android.os.Bundle;

import app.delivering.component.starttour.fragment.StartTourFragment;
import app.delivering.component.starttour.pager.StartTourPagerAdapter;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.starttour.page.model.StartTourPageModel;
import rx.Observable;

public class StartTourPagePresenter extends BaseOutputPresenter<Observable<StartTourPageModel>>{
    private Bundle arguments;

    public StartTourPagePresenter(StartTourFragment fragment) {
        super(fragment.getBaseActivity());
        this.arguments = fragment.getArguments();
    }

    @Override public Observable<StartTourPageModel> process() {
        return Observable.create(subscriber -> {
            StartTourPageModel model = new StartTourPageModel();
            String title = arguments.getString(StartTourPagerAdapter.START_TOUR_TITLE, "");
            String message = arguments.getString(StartTourPagerAdapter.START_TOUR_MESSAGE, "");
            int id = arguments.getInt(StartTourPagerAdapter.START_TOUR_VIEW_ID, 0);
            model.setTitle(title);
            model.setMessage(message);
            model.setImageId(id);
            subscriber.onNext(model);
            subscriber.onCompleted();
        });
    }
}
