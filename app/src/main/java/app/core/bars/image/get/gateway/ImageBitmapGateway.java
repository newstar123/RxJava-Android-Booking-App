package app.core.bars.image.get.gateway;

import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.bars.image.get.entity.ImageBitmapResponse;
import rx.Observable;

public interface ImageBitmapGateway {
   Observable<ImageBitmapResponse> get(ImageBitmapRequest request);
}
