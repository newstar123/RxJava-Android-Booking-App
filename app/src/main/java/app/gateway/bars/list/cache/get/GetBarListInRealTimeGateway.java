package app.gateway.bars.list.cache.get;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import rx.Observable;

public interface GetBarListInRealTimeGateway {
    Observable<List<BarModel>> get();
}
