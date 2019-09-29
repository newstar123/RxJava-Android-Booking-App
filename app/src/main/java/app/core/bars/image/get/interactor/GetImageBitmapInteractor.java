package app.core.bars.image.get.interactor;

import android.content.Context;

import app.core.BaseInteractor;
import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import app.gateway.bars.image.get.GetImageBitmapGateway;
import rx.Observable;

public class GetImageBitmapInteractor implements BaseInteractor<ImageBitmapRequest, Observable<ImageBitmapResponse>>{
    private GetImageBitmapGateway getBarImageGateway;

    public GetImageBitmapInteractor(Context context){
        getBarImageGateway = new GetImageBitmapGateway(context);
    }

    @Override public Observable<ImageBitmapResponse> process(ImageBitmapRequest barImageRequest) {
        return getBarImageGateway.get(barImageRequest);
    }
}
