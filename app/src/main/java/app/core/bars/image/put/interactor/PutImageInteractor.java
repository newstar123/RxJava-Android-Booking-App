package app.core.bars.image.put.interactor;

import app.core.BaseInteractor;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.core.permission.storage.interactor.ExternalStoragePermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.bars.image.put.PutImageToFileGateway;
import rx.Observable;

public class PutImageInteractor implements BaseInteractor<ImageBitmapResponse,Observable<ImageBitmapResponse>> {
    private PutImageToFileGateway putImageToFileGateway;
    private ExternalStoragePermissionInteractor permissionInteractor;

    public PutImageInteractor(BaseActivity activity){
        permissionInteractor = new ExternalStoragePermissionInteractor(activity);
        putImageToFileGateway = new PutImageToFileGateway();
    }

    @Override public Observable<ImageBitmapResponse> process(ImageBitmapResponse request) {
        return permissionInteractor.process()
                .concatMap(isOk -> putImageToFileGateway.put(request));
    }
}
