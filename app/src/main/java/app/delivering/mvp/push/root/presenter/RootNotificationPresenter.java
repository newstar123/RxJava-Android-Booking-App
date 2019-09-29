package app.delivering.mvp.push.root.presenter;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.delivering.component.push.status.PushNotificationStatus;
import app.delivering.mvp.BaseContextPresenter;
import app.delivering.mvp.push.root.model.RootPushNotificationResponse;
import rx.Observable;

public class RootNotificationPresenter extends BaseContextPresenter<RemoteMessage, Observable<RootPushNotificationResponse>> {
    private static final String MESSAGE_BODY_KEY = "message";
    private static final String MESSAGE_STATUS_KEY = "status";
    private static final String MSG_ROOT_KEY = "msg";

    public RootNotificationPresenter(Context context) {
        super(context);
    }

    @Override public Observable<RootPushNotificationResponse> process(RemoteMessage message) {
        return Observable.create(subscriber -> {
            JSONObject rootJson = null;
            try {
                Map<String, String> notificationFieldsMap = message.getData();
                String messageBody = notificationFieldsMap.get(MESSAGE_BODY_KEY).replace("\\", "");
                String m1 = messageBody.replace("\"{", "{");
                String m2 = m1.replace("}\"", "}");
                rootJson = new JSONObject(m2);
                JSONObject msgJson = getMessageJson(rootJson);
                String statusName = msgJson.getString(MESSAGE_STATUS_KEY);
                PushNotificationStatus status = PushNotificationStatus.toType(statusName);
                RootPushNotificationResponse response = new RootPushNotificationResponse();
                response.setJsonMessage(msgJson);
                final String serverMessage = getServerMessage(rootJson);
                response.setServerMessage(serverMessage);
                response.setStatus(status);
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }

    private JSONObject getMessageJson(JSONObject rootJson) {
        try {
            return new JSONObject(rootJson.getString(MSG_ROOT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootJson;
    }

    private String getServerMessage(JSONObject rootJson) {
        try {
            return rootJson.getString(MESSAGE_BODY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
