package app.delivering.mvp.bars.map.user.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

import com.google.android.gms.maps.model.LatLng;

import app.R;
import app.core.location.get.GetRxLocationInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.map.user.model.UserMarkerModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class UserOnMapPresenter extends BasePresenter<String, Observable<UserMarkerModel>> {
    private GetRxLocationInteractor locationInteractor;

    public UserOnMapPresenter(BaseActivity activity) {
        super(activity);
        locationInteractor = new GetRxLocationInteractor(getActivity());
    }

    @Override public Observable<UserMarkerModel> process(String s) {
        return locationInteractor.process()
                .concatMap(this::convertToLatLng)
                .concatMap(this::createMarkerBackground)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<LatLng> convertToLatLng(Location location) {
        return Observable.create(subscriber -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            subscriber.onNext(latLng);
            subscriber.onCompleted();
        });
    }

    private Observable<UserMarkerModel> createMarkerBackground(LatLng latLng) {
        return Observable.create(subscriber -> {
            UserMarkerModel markerModel = new UserMarkerModel();
            markerModel.setPosition(latLng);
            markerModel.setMarker(getBitmapFromVectorDrawable(getActivity(), R.drawable.inset_person_marker));
            markerModel.setTitle(getActivity().getString(R.string.current_location));
            subscriber.onNext(markerModel);
            subscriber.onCompleted();
        });
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
