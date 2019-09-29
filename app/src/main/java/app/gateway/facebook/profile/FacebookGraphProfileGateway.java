package app.gateway.facebook.profile;


import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.gson.Gson;

import app.core.login.facebook.entity.FacebookProfileResponse;
import app.core.login.facebook.entity.picture.Picture;
import app.core.login.facebook.entity.picture.PictureData;
import app.core.login.facebook.gateway.FacebookProfileGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FacebookGraphProfileGateway implements FacebookProfileGateway {
    private Gson gson;
    private Picture picture;
    private PictureData pictureData;
    private FacebookProfileResponse facebookProfileResponse;

    public FacebookGraphProfileGateway() {
        setUpAllParameters();
    }

    private void setUpAllParameters() {
        gson = new Gson();
        picture = new Picture();
        pictureData = new PictureData();
        facebookProfileResponse = new FacebookProfileResponse();

        pictureData.setPicture(picture);
        facebookProfileResponse.setPictureData(pictureData);
    }

    @Override
    public Observable<FacebookProfileResponse> get() {
        return Observable.create((Observable.OnSubscribe<FacebookProfileResponse>) subscriber -> {
            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
                if(response.getError() != null) {
                    subscriber.onError(response.getError().getException());
                    subscriber.onCompleted();
                    return;
                }
                facebookProfileResponse  = gson.fromJson(object.toString(), FacebookProfileResponse.class);
                subscriber.onNext(facebookProfileResponse);
                subscriber.onCompleted();
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "gender,age_range,birthday,picture");
            parameters.putBoolean("redirect", false);
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }).subscribeOn(Schedulers.io());
    }
}
