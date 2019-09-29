package app.gateway.profile.photo.facebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;

import app.core.facebook.profile.gateway.RestoreFBPhotoGateway;
import rx.Observable;

public class GetFacebookPhotoGateway implements RestoreFBPhotoGateway {
    private static final String FB_GRAPH_PREFIX = "https://graph.facebook.com/";
    private static final String FB_GRAPH_SUFIX = "/picture?type=large";

    @Override public Observable<Bitmap> get(long facebookId) {
        return Observable.just(FB_GRAPH_PREFIX + facebookId + FB_GRAPH_SUFIX)
                .concatMap(this::LoadPhoto);
    }

    private Observable<Bitmap> LoadPhoto(String spec) {

        return Observable.create(subscriber -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(spec).openConnection().getInputStream());
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
