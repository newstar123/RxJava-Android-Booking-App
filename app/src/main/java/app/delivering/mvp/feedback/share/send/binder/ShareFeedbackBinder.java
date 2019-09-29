package app.delivering.mvp.feedback.share.send.binder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.MalformedURLException;
import java.net.URL;

import app.R;
import app.core.bars.image.get.entity.ImageBitmapRequest;
import app.core.bars.list.get.entity.BarModel;
import app.core.feedback.init.entity.FeedbackOutputModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.feedback.send.model.ShareFeedbackResponse;
import app.delivering.mvp.feedback.share.send.model.SuccessFeedback;
import app.delivering.mvp.feedback.share.send.presenter.ShareFeedbackPresenter;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

import static android.os.Build.VERSION_CODES.M;

public class ShareFeedbackBinder extends BaseBinder {
    public static final int SHARE_REQUEST = 1122;
    @BindView(R.id.share_facebook) CheckBox facebook;
    @BindView(R.id.share_twitter) CheckBox twitter;
    @BindView(R.id.share_email) CheckBox email;
    @BindView(R.id.editTextFeedback) EditText comment;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<ShareFeedbackResponse> replaySubject;
    private BarModel bar;
    private ShareFeedbackPresenter presenter;

    public ShareFeedbackBinder(BaseActivity activity) {
        super(activity);
        presenter = new ShareFeedbackPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBarEvent(FeedbackOutputModel model) {
        bar = model.getBar();
        initPresenter();
    }

    private void initPresenter() {
        ImageBitmapRequest request = new ImageBitmapRequest();
        request.setUrl(bar.getBackgroundImageUrl());
        presenter.process(request)
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEvent(SuccessFeedback event) {
        if (event.getRatingType() == 1 && bar != null && socialsChecked()) {
            replaySubject.asObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(this::checkShareFeedback, this::onError);
        } else
            getActivity().finish();
    }

    private boolean socialsChecked() {
        return email.isChecked() || twitter.isChecked() || facebook.isChecked();
    }

    private void checkShareFeedback(ShareFeedbackResponse response) {
        if (email.isChecked()) shareWithMail(response);
        if (twitter.isChecked()) shareToTwitter(response);
        if (facebook.isChecked()) shareToFacebook(response);
    }

    private void shareWithMail(ShareFeedbackResponse response) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, getTitle(bar.getName()));
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getMessageBody(response)));
        intent.putExtra(Intent.EXTRA_STREAM, getFilePath(response));
        getActivity().startActivityForResult(Intent.createChooser(intent, "Send mail..."), SHARE_REQUEST);
    }

    private void shareToTwitter(ShareFeedbackResponse response) {
        URL url = null;
        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity());
        try {
            url = new URL(response.getProfileRefCode());
            builder.url(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String message = "Check out " + getTwitterLink() + " with Qorum.\nOptional(\"" + comment.getText().toString() + "\")";
        builder.text(message);
        builder.show();
    }

    private String getMessageBody(ShareFeedbackResponse response) {
        return "<!DOCTYPE html><html><body>Check out <a href=\"" + getTwitterLink() + "\">"
                + bar.getTwitterHandle() + "</a> with <a href=\"" + response.getProfileRefCode()
                + "\">Qorum</a>. Optional(\"" + comment.getText().toString() + "\")</body></html>";
    }

    private String getTwitterLink() {
        return "http://twitter.com/" + bar.getTwitterHandle();
    }

    private Uri getFilePath(ShareFeedbackResponse response) {
        if (Build.VERSION.SDK_INT < M)
            return Uri.parse("content://" + response.getPath());
        else
        return Uri.parse("file://" + response.getPath());
    }

    private void shareToFacebook(ShareFeedbackResponse response) {
        FacebookSdk.sdkInitialize(getActivity());
        if (ShareDialog.canShow(ShareLinkContent.class))
            showShareDialog(response);
        else
            getActivity().finish();
    }

    private void showShareDialog(ShareFeedbackResponse response) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(getTitle(bar.getName()))
                .setContentDescription(comment.getText().toString())
                .setContentUrl(Uri.parse(response.getProfileRefCode()))
                .build();
        ShareContent content = new ShareMediaContent.Builder()
                .addMedium(new SharePhoto.Builder()
                        .setBitmap(response.getShareImage())
                        .build())
                .build();
        ShareDialog shareDialog = new ShareDialog(getActivity());
        shareDialog.registerCallback(CallbackManager.Factory.create(),
                                     new FacebookCallback<Sharer.Result>() {
                                         @Override public void onSuccess(Sharer.Result result) {}
                                         @Override public void onCancel() {}
                                         @Override public void onError(FacebookException error) {}
                                     }, SHARE_REQUEST);
        shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
    }

    private String getTitle(String name) {
        return String.format(getString(R.string.check_out_bar_with_qorum), name);
    }

    private void onError(Throwable e) {
        replaySubject = ReplaySubject.create();
        initPresenter();
        initExceptionHandler.showError(e, view -> onSuccessEvent(new SuccessFeedback(1)));
    }

}
