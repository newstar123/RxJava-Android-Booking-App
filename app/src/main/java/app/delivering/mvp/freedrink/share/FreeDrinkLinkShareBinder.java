package app.delivering.mvp.freedrink.share;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import app.R;
import app.delivering.component.invite.FreeDrinksFragment;
import app.delivering.component.invite.receiver.FreeDrinkSenderReceiver;
import app.delivering.component.terms.TermsActivity;
import app.delivering.mvp.BaseBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import butterknife.BindView;
import butterknife.OnClick;

public class FreeDrinkLinkShareBinder extends BaseBinder {
    @BindView(R.id.free_drink_container) CoordinatorLayout container;
    private static final String SHARE_DRINKS = "Share your free drinks";
    private String codeUrl;

    public FreeDrinkLinkShareBinder(FreeDrinksFragment fragment) {
        super(fragment.getBaseActivity());
        codeUrl = fragment.getArguments().getString(FreeDrinksFragment.FREE_DRINKS_CODE_URL);
    }

    @OnClick(R.id.invite_with_facebook)
    void onFBLink() {
        if (TextUtils.isEmpty(codeUrl)) {
            showDialogMessage(R.string.error_no_photo);
        } else {
            sendAnalytics(MixpanelEvents.TYPE_FACEBOOK);
            FacebookSdk.sdkInitialize(getActivity());
            ShareDialog shareDialog = new ShareDialog(getActivity());
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(SHARE_DRINKS)
                        .setContentDescription("Follow the next address " + codeUrl)
                        .setContentUrl(Uri.parse(codeUrl))
                        .build();
                shareDialog.show(linkContent);
            }
        }
    }

    @OnClick(R.id.invite_with_sms)
    void onSMSLink() {
        if (TextUtils.isEmpty(codeUrl)) {
            showDialogMessage(R.string.error_no_photo);
        } else {
            sendAnalytics(MixpanelEvents.TYPE_SMS);
            Uri uri = Uri.parse("smsto:");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", SHARE_DRINKS + " " + codeUrl);
            getActivity().startActivity(intent);
        }
    }

    @OnClick(R.id.invite_with_other)
    void onMoreLink() {
        if (TextUtils.isEmpty(codeUrl)) {
            showDialogMessage(R.string.error_no_photo);
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, SHARE_DRINKS + " " + codeUrl);
            sendIntent.setType("text/plain");
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent receiver = new Intent(getActivity(), FreeDrinkSenderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                intent = Intent.createChooser(sendIntent, "Share via", pendingIntent.getIntentSender());
            } else {
                intent = Intent.createChooser(sendIntent, "Share via");
                sendAnalytics(MixpanelEvents.TYPE_OTHER);
            }
            getActivity().startActivity(intent);
        }
    }

    @OnClick(R.id.invite_with_twitter)
    void onTwitterLink() {
        if (TextUtils.isEmpty(codeUrl)) {
            showDialogMessage(R.string.error_no_photo);
        } else {
            sendAnalytics(MixpanelEvents.TYPE_TWITTER);
            URL url = null;
            TweetComposer.Builder builder = new TweetComposer.Builder(getActivity());
            try {
                url = new URL(codeUrl);
                builder.text(SHARE_DRINKS);
                builder.url(url);
            } catch (MalformedURLException e) {
                builder.text(SHARE_DRINKS + " - " + codeUrl);
                e.printStackTrace();
            }
            builder.show();
        }
    }

    @OnClick(R.id.invite_with_email)
    void onEmailLink() {
        if (TextUtils.isEmpty(codeUrl)) {
            showDialogMessage(R.string.error_no_share_url);
        } else {
            sendAnalytics(MixpanelEvents.TYPE_EMAIL);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_SUBJECT, SHARE_DRINKS);
            intent.putExtra(Intent.EXTRA_TEXT, codeUrl);
            getActivity().startActivity(Intent.createChooser(intent, "Send with..."));
        }
    }

    private void sendAnalytics(String type) {
        MixpanelSendGateway.send(MixpanelEvents.getInviteEvent(type)).subscribe(res -> {
        }, e -> {
        }, () -> {
        });
    }

    @OnClick(R.id.terms_conditions)
    void onTermsAndConditions() {
        getActivity().startActivity(new Intent(getActivity(), TermsActivity.class));
    }
}
