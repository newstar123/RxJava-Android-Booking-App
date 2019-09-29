package app.delivering.mvp.starttour.page.binder;

import android.widget.ImageView;
import android.widget.TextView;

import app.R;
import app.delivering.component.starttour.fragment.StartTourFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.starttour.page.model.StartTourPageModel;
import app.delivering.mvp.starttour.page.presenter.StartTourPagePresenter;
import butterknife.BindView;

public class PageBinder extends BaseBinder {
    @BindView(R.id.start_tour_fragment_title) TextView title;
    @BindView(R.id.start_tour_fragment_message) TextView message;
    @BindView(R.id.start_tour_fragment_view) ImageView image;
    private StartTourPagePresenter pagePresenter;

    public PageBinder(StartTourFragment fragment) {
        super(fragment.getBaseActivity());
        pagePresenter = new StartTourPagePresenter(fragment);
    }

    @Override public void afterViewsBounded() {
        pagePresenter.process().subscribe(this::showPage, e->{}, ()->{});
    }

    private void showPage(StartTourPageModel model) {
        title.setText(model.getTitle());
        message.setText(model.getMessage());
        image.setImageResource(model.getImageId());
    }
}
