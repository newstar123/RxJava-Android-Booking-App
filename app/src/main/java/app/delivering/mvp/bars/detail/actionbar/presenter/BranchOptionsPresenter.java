package app.delivering.mvp.bars.detail.actionbar.presenter;

import android.util.Pair;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import rx.Observable;

import static io.branch.referral.util.BranchContentSchema.COMMERCE_RESTAURANT;

public class BranchOptionsPresenter extends
        BasePresenter<BarDetailModel, Observable<Pair<BranchUniversalObject, LinkProperties>>> {

    private static final String QORUM_LINK_1 = "https://qorum.com/";
    private static final String QORUM_LINK_2 = "https://qorum.com";
    private static final String DESKTOP_URL = "$desktop_url";
    private static final String IOS_URL = "$ios_url";
    private static final String IPAD_URL = "$ipad_url";
    private static final String ANDROID_URL = "$android_url";
    private static final String VENUE_ID = "venue_id";
    private static final String VENUE = "venue/";

    public BranchOptionsPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override
    public Observable<Pair<BranchUniversalObject, LinkProperties>> process(BarDetailModel barDetailModel) {
        BranchUniversalObject branchUniversalObject = setUpBranchUniversalObject(barDetailModel);
        LinkProperties linkProperties = setUpLinkProperties(barDetailModel);
        return Observable.just(new Pair<>(branchUniversalObject, linkProperties));
    }

    private LinkProperties setUpLinkProperties(BarDetailModel barDetailModel) {
        return new LinkProperties()
                .addControlParameter(DESKTOP_URL, QORUM_LINK_2)
                .addControlParameter(IOS_URL, QORUM_LINK_2)
                .addControlParameter(IPAD_URL, QORUM_LINK_2)
                .addControlParameter(ANDROID_URL, QORUM_LINK_2)
                .addControlParameter(VENUE_ID, String.valueOf(barDetailModel.getBarModel().getId()));
    }

    private BranchUniversalObject setUpBranchUniversalObject(BarDetailModel barDetailModel) {
        ContentMetadata contentMetadata = new ContentMetadata();
        contentMetadata.addressCity = barDetailModel.getBarModel().getCity();
        contentMetadata.addressPostalCode = String.valueOf(barDetailModel.getBarModel().getZip());
        contentMetadata.latitude = barDetailModel.getBarModel().getLatitude();
        contentMetadata.longitude = barDetailModel.getBarModel().getLongitude();
        contentMetadata.setContentSchema(COMMERCE_RESTAURANT);
        return new BranchUniversalObject()
                .setCanonicalIdentifier(VENUE + barDetailModel.getBarModel().getId())
                .setCanonicalUrl(QORUM_LINK_1)
                .setTitle(barDetailModel.getBarModel().getName())
                .setContentDescription(String.valueOf(barDetailModel.getBarModel().getType()))
                .setContentImageUrl(barDetailModel.getBarModel().getBackgroundImageUrl())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(contentMetadata);
    }
}
