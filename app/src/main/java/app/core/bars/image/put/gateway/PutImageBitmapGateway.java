package app.core.bars.image.put.gateway;

import app.core.bars.image.get.entity.ImageBitmapResponse;
import rx.Observable;

public interface PutImageBitmapGateway {
   Observable<ImageBitmapResponse> put(ImageBitmapResponse request);
}
