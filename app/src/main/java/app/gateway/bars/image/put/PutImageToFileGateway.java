package app.gateway.bars.image.put;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

import app.CustomApplication;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.core.bars.image.put.gateway.PutImageBitmapGateway;
import rx.Observable;

public class PutImageToFileGateway implements PutImageBitmapGateway {

    @Override public Observable<ImageBitmapResponse> put(ImageBitmapResponse request) {
        return tryToBitmapSave(request);
    }

    private Observable<ImageBitmapResponse> tryToBitmapSave(ImageBitmapResponse bitmap) {
        return Observable.create(subscriber -> {
            try {
                File storageDir = CustomApplication.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(bitmap.getName(), ".jpeg", storageDir);
                FileOutputStream out = new FileOutputStream(image.getPath());
                bitmap.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                bitmap.setPath(image.getPath());
                bitmap.setFile(image);
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
