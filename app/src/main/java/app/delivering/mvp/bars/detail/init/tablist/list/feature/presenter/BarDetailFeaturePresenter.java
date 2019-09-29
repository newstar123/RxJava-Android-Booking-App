package app.delivering.mvp.bars.detail.init.tablist.list.feature.presenter;

import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

import app.core.bars.metadata.GetServerMetadataInteractor;
import app.core.bars.metadata.entity.ServerFeatureModel;
import app.core.bars.metadata.entity.ServerMetadataModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.enums.FeatureType;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.model.FeaturesModel;
import rx.Observable;

public class BarDetailFeaturePresenter extends BasePresenter<List<String>, Observable<List<FeaturesModel>>>{
    private static final String FEATURE_SEPARATOR = "=";
    private final GetServerMetadataInteractor serverMetadataInteractor;

    public BarDetailFeaturePresenter(BaseActivity activity) {
        super(activity);
        serverMetadataInteractor = new GetServerMetadataInteractor(activity);
    }

    @Override public Observable<List<FeaturesModel>> process(List<String> strings) {
        return Observable.zip(Observable.just(strings), serverMetadataInteractor.process(), this::getParsedList);
    }

    private List<FeaturesModel> getParsedList(List<String> strings, ServerMetadataModel metadataModel) {
        ArrayList<FeaturesModel> modelList = new ArrayList<>();
        for (String feature : strings) {
            FeaturesModel featuresModel = new FeaturesModel();
            String[] separate = feature.split(FEATURE_SEPARATOR);
            featuresModel.setType(FeatureType.toType(separate[0]));
            featuresModel.setServerFeatureModel(findServerMetadata(metadataModel, separate[0]));
            if (featuresModel.getVenueFeatureModel() != null)
                parseModel(modelList, featuresModel, separate);
        }
        return modelList;
    }

    private void parseModel(ArrayList<FeaturesModel> modelList, FeaturesModel featuresModel, String[] separate) {
        if (separate.length == 1)
            featuresModel.setTextValue(getActivity().getString(featuresModel.getType().getDescriptionId()));
        if (separate.length == 2) {
            switch (featuresModel.getType()) {
                case crowd:
                    featuresModel.setTextValue(separate[1]);
                    break;
                case whatToDrink:
                    featuresModel.setTextValue(separate[1]);
                    break;
                default:
                    featuresModel.setCombinedText(getSpannedText(featuresModel, separate[1]));
            }
        }
        modelList.add(featuresModel);
    }

    private ServerFeatureModel findServerMetadata(ServerMetadataModel metadataModel, String featureName) {
        for (ServerFeatureModel insiderTip : metadataModel.getInsiderTips())
            if (insiderTip.getId().equalsIgnoreCase(featureName))
                return insiderTip;
        for (ServerFeatureModel feature : metadataModel.getFeatures())
            if (feature.getId().equalsIgnoreCase(featureName))
                return feature;
        return null;
    }

    private Spanned getSpannedText(FeaturesModel featuresModel, String string) {
        int formatId = featuresModel.getType().getDescriptionId();
        String text = String.format(getActivity().getString(formatId), string);
        return formatHtmlToSpanned(text);
    }

    protected Spanned formatHtmlToSpanned(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(string);
    }
}
