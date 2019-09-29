package app.gateway.facebook.friends.graph;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import app.core.facebook.friends.entity.FacebookFriendListModel;
import app.core.facebook.friends.entity.FacebookFriendModel;
import app.core.facebook.friends.gateway.FacebookFriendListGateway;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class FacebookGraphFriendsGateway implements FacebookFriendListGateway {
    private Gson gson;

    public FacebookGraphFriendsGateway() {
        gson = new Gson();
    }

    @Override public Observable<FacebookFriendListModel> get() {
        return Observable.create((Observable.OnSubscribe<FacebookFriendListModel>) subscriber -> {
            GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                    (objects, response) -> onFriendListLoaded(objects, subscriber));
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }).subscribeOn(Schedulers.io());
    }

    private void onFriendListLoaded(JSONArray objects, Subscriber<? super FacebookFriendListModel> subscriber) {
        ArrayList<FacebookFriendModel> friendsIds = parseFriendIds(objects);
        FacebookFriendListModel listModel = new FacebookFriendListModel();
        listModel.setFriendModels(friendsIds);
        subscriber.onNext(listModel);
        subscriber.onCompleted();
    }

    private ArrayList<FacebookFriendModel> parseFriendIds(JSONArray objects) {
        if (objects == null)
            return new ArrayList<>();
        ArrayList<FacebookFriendModel> friendsIds = new ArrayList<>();
        for (int i = 0; i < objects.length(); i++) {
            try {
                friendsIds.add(gson.fromJson(objects.get(i).toString(), FacebookFriendModel.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friendsIds;
    }
}
