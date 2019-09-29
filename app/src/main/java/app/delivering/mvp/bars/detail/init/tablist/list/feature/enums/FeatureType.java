package app.delivering.mvp.bars.detail.init.tablist.list.feature.enums;

import android.text.TextUtils;

import app.R;

public enum FeatureType {
    fullBar(R.string.feature_fullbar, R.drawable.inset_cocktail, false),
    beerAndWineOnly(R.string.feature_beerandwineonly, R.drawable.inset_cocktail, false),
    mixology(R.string.feature_mixology, R.drawable.inset_cocktail, false),
    microbrewery(R.string.feature_microbrewery, R.drawable.inset_cocktail, false),
    draftBeer(R.string.feature_draftbeer, R.drawable.inset_cocktail, false),
    craftBeers(R.string.feature_craftbeers, R.drawable.inset_cocktail, false),
    vastWineList(R.string.feature_vastwinelist, R.drawable.inset_cocktail, false),
    extensiveSelection(R.string.feature_extensiveselection, R.drawable.inset_cocktail, true),
    servesFood(R.string.feature_servesfood, R.drawable.inset_cocktail, false),
    brunch(R.string.feature_brunch, R.drawable.inset_cocktail, false),
    rooftop(R.string.feature_rooftop, R.drawable.inset_cocktail, false),
    poolTables(R.string.feature_pooltables, R.drawable.inset_cocktail, false),
    karaoke(R.string.feature_karaoke, R.drawable.inset_cocktail, false),
    bigScreenTVs(R.string.feature_bigscreentvs, R.drawable.inset_cocktail, true),
    showsSports(R.string.feature_showssports, R.drawable.inset_cocktail, false),
    fireplace(R.string.feature_fireplace, R.drawable.inset_cocktail, false),
    liveDJ(R.string.feature_livedj, R.drawable.inset_cocktail, false),
    liveMusic(R.string.feature_livemusic, R.drawable.inset_cocktail, false),
    outdoorSeating(R.string.feature_outdoorseating, R.drawable.inset_cocktail, false),
    smokingArea(R.string.feature_smokingarea, R.drawable.inset_cocktail, false),
    impressiveViews(R.string.feature_impressiveviews, R.drawable.inset_cocktail, false),
    sunsets(R.string.feature_sunsets, R.drawable.inset_cocktail, false),
    oceanfront(R.string.feature_oceanfront, R.drawable.inset_cocktail, false),
    singlesScene(R.string.feature_singlesscene, R.drawable.inset_cocktail, false),
    gayScene(R.string.feature_gayscene, R.drawable.inset_cocktail, false),
    lesbianScene(R.string.feature_lesbianscene, R.drawable.inset_cocktail, false),
    maxOccupancy(R.string.feature_maxoccupancy, R.drawable.inset_cocktail, true),
    wifi(R.string.feature_wifi, R.drawable.inset_cocktail, false),
    avgDrinkPrice(R.string.tip_avgdrinkprice, R.drawable.inset_cocktail, true),
    avgGuestAge(R.string.tip_avgguestage, R.drawable.inset_cocktail, true),
    avgWaitTime(R.string.tip_avgwaittime, R.drawable.inset_cocktail, true),
    dressCodeCasual(R.string.tip_dresscodecasual, R.drawable.inset_cocktail, false),
    dressCodeSmartCasual(R.string.tip_dresscodesmartcasual, R.drawable.inset_cocktail, false),
    dressCodeTrendy(R.string.tip_dresscodetrendy, R.drawable.inset_cocktail, false),
    dressCodeUpscale(R.string.tip_dresscodeupscale, R.drawable.inset_cocktail, false),
    cashOnly(R.string.tip_cashonly, R.drawable.inset_cocktail, false),
    energyLevelSubdued(R.string.tip_energylevelsubdued, R.drawable.inset_cocktail, false),
    energyLevelLively(R.string.tip_energylevellively, R.drawable.inset_cocktail, false),
    energyLevelRowdy(R.string.tip_energylevelrowdy, R.drawable.inset_cocktail, false),
    music(R.string.tip_music, R.drawable.inset_cocktail, true),
    whatToDrink(R.string.tip_whattodrink, R.drawable.inset_cocktail, false),
    crowd(R.string.tip_crowd, R.drawable.inset_cocktail, false),
    freestyle(R.string.tip_freestyle, R.drawable.inset_cocktail, true),
    EMPTY_FEATURE(R.string.an_error_has_happened, R.drawable.inset_cocktail, false);

    private int descriptionId;
    private int iconId;
    private boolean isTwoColorsName;

    FeatureType(int descriptionId, int iconId, boolean isTwoStateName) {
        this.descriptionId = descriptionId;
        this.iconId = iconId;
        this.isTwoColorsName = isTwoStateName;
    }

    public static FeatureType toType(String name) {
        if (TextUtils.isEmpty(name))
            return EMPTY_FEATURE;
        for (FeatureType type : FeatureType.values())
            if (type.name().equalsIgnoreCase(name))
                return type;
        return EMPTY_FEATURE;
    }

    public int getIconId() {
        return iconId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public boolean isTwoColorsName() {
        return isTwoColorsName;
    }
}
