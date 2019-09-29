package app.gateway.bars.list.get;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import rx.Observable;

public interface GetBarsGateway {
    Observable<List<BarModel>> get(double lat, double lng, int miles);
}
