package app.core.facebook.profile.gateway;

import android.graphics.Bitmap;

import rx.Observable;

public interface RestoreFBPhotoGateway {
   Observable<Bitmap> get(long facebookId);
}
