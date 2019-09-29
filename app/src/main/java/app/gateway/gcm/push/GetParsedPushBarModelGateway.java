package app.gateway.gcm.push;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import app.core.bars.list.get.entity.BarModel;
import app.core.uber.tracking.to.bar.gateway.ParsePushBarModelGateway;
import rx.Observable;

public class GetParsedPushBarModelGateway implements ParsePushBarModelGateway {
    private static final String PUSH_BAR = "venue";
    private static final String PUSH_BAR_ID = "id";
    private static final String PUSH_BAR_NAME = "name";
    private static final String PUSH_BAR_URL = "background_image_url";

    public GetParsedPushBarModelGateway(Context context) {

    }

    @Override public Observable<BarModel> parse(JSONObject pushMessage) {
        return Observable.create(subscriber -> {
            try {
                BarModel barModel = new BarModel();
                JSONObject barJson = pushMessage.getJSONObject(PUSH_BAR);
                long id = barJson.getLong(PUSH_BAR_ID);
                barModel.setId(id);
                String name = barJson.getString(PUSH_BAR_NAME);
                barModel.setName(name);
                String url = barJson.getString(PUSH_BAR_URL);
                barModel.setBackgroundImageUrl(url);
                subscriber.onNext(barModel);
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }
}
