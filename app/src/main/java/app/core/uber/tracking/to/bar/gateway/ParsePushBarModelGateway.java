package app.core.uber.tracking.to.bar.gateway;


import org.json.JSONObject;

import app.core.bars.list.get.entity.BarModel;
import rx.Observable;

public interface ParsePushBarModelGateway {
    Observable<BarModel> parse(JSONObject pushMessage);
}
