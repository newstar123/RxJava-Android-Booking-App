package app.gateway.bars.image.get;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.core.bars.image.get.gateway.ImageBitmapGateway;
import rx.Observable;
import rx.Subscriber;

public class GetImageBitmapGateway implements ImageBitmapGateway {
    private final static String VENDOR_IMAGE_NAME = "share_vendor_picture";
    private Context context;
    private Target target;

    public GetImageBitmapGateway(Context context) {

        this.context = context;
    }

    @Override public Observable<ImageBitmapResponse> get(ImageBitmapRequest request) {
        return Observable.create(subscriber -> loadImage(subscriber, request));
    }

    private void loadImage(Subscriber<? super ImageBitmapResponse> subscriber, ImageBitmapRequest request) {
        ImageBitmapResponse responce = new ImageBitmapResponse();
        responce.setPath(request.getPath());
        target = new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                responce.setBitmap(bitmap);
                responce.setName(VENDOR_IMAGE_NAME);
                subscriber.onNext(responce);
                subscriber.onCompleted();
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
                subscriber.onNext(responce);
                subscriber.onCompleted();
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(context).load(request.getUrl()).into(target);
    }

}
