package app.delivering.mvp.feedback.init.binder.handler;


import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import app.delivering.mvp.main.init.binder.InitLocationImageHandler;
import rx.Observable;
import rx.Subscriber;

public class PicassoImageHandler {
    private ImageView imageView;

    public PicassoImageHandler(ImageView imageView) {
        this.imageView = imageView;
    }

   public Observable<Boolean> apply(RequestCreator requestCreator){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override public void call(Subscriber<? super Boolean> subscriber) {
                requestCreator.into(imageView, new Callback() {
                    @Override public void onSuccess() {
                        InitLocationImageHandler.animateImage(imageView);
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }

                    @Override public void onError() {
                        subscriber.onError(new RuntimeException());
                    }
                });
            }
        });

   }
}
