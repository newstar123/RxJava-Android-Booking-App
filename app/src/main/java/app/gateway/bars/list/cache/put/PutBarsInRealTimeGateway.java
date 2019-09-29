package app.gateway.bars.list.cache.put;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import rx.Observable;

public interface PutBarsInRealTimeGateway {
    Observable<List<BarModel>> put(List<BarModel> list);
}
