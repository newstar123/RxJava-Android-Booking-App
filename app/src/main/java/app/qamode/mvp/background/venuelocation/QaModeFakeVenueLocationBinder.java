package app.qamode.mvp.background.venuelocation;

import android.Manifest;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.location.get.GetRxLocationInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.mvp.validator.EditValidator;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class QaModeFakeVenueLocationBinder extends BaseBinder {
    @BindView(R.id.fake_venue_location_switcher) SwitchCompat switcher;
    @BindView(R.id.fake_venue_location_expand_view) TextView expandHint;
    @BindView(R.id.additional_fake_venue_location_settings) LinearLayout settingsContainer;
    @BindView(R.id.fake_venue_location_to_current_position_msg) TextView currentMessage;
    @BindView(R.id.fake_venue_location_to_custom_position_msg) TextView customMessage;
    @BindView(R.id.fake_venue_location_custom_latitude_layout) TextInputLayout latitudeLayout;
    @BindView(R.id.fake_venue_location_custom_latitude_input) EditText latitude;
    @BindView(R.id.fake_venue_location_custom_longitude_layout) TextInputLayout longitudeLayout;
    @BindView(R.id.fake_venue_location_custom_longitude_input) EditText longitude;
    @BindView(R.id.fake_venue_location_to_current_position) RadioButton currentLocation;
    @BindView(R.id.fake_venue_location_to_custom_position) RadioButton customLocation;
    @BindView(R.id.fake_venue_location_progress) MaterialProgressBar progressBar;
    private final RxPermissions rxPermissions;
    private final GetRxLocationInteractor locationInteractor;

    public QaModeFakeVenueLocationBinder(BaseActivity activity) {
        super(activity);
        rxPermissions = new RxPermissions(activity);
        locationInteractor = new GetRxLocationInteractor(activity);
    }

    @Override public void afterViewsBounded() {
        fillingFields();
        switcher.setChecked(QaModeCache.getQaModeVenueLocation().get(BaseCacheType.BOOLEAN));
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> updateState(isChecked));
        setViewsVisibility(switcher.isChecked());
        new EditValidator(getActivity()).createSimpleValidator(latitude, latitudeLayout)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> checkValidation(), e->{}, ()->{});
        new EditValidator(getActivity()).createSimpleValidator(longitude, longitudeLayout)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> checkValidation(), e->{}, ()->{});
    }

    private void fillingFields() {
        latitude.setText(QaModeCache.getQaModeVenueLocationLatitude().get(BaseCacheType.STRING));
        longitude.setText(QaModeCache.getQaModeVenueLocationLongitude().get(BaseCacheType.STRING));
        if (FakeVenueLocationType.toType(QaModeCache.getQaModeVenueLocationType().get(BaseCacheType.STRING)) == FakeVenueLocationType.CUSTOM)
            customLocation.setChecked(true);
        else
            currentLocation.setChecked(true);
    }

    private void updateState(boolean isChecked) {
        if (isChecked)
            if (!rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION) || !rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(isOk-> switcher.setChecked(false));
        QaModeCache.getQaModeVenueLocation().save(BaseCacheType.BOOLEAN, isChecked);
        setViewsVisibility(isChecked);
    }

    private void setViewsVisibility(boolean checked) {
        expandHint.setVisibility(checked ? View.GONE : View.VISIBLE);
        settingsContainer.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(latitudeLayout.getError()) && TextUtils.isEmpty(longitudeLayout.getError())) //validation results
            save(latitude.getText().toString(), longitude.getText().toString());
    }

    @OnCheckedChanged(R.id.fake_venue_location_to_current_position) void onCurrentLocationSelected(boolean isChecked){
        if (isChecked){
            setAdditionalSettingsVisibility(isChecked);
            QaModeCache.getQaModeVenueLocationType().save(BaseCacheType.STRING, FakeVenueLocationType.CURRENT.name());
        }
    }

    private void save(String latitude, String longitude) {
        QaModeCache.getQaModeVenueLocationLatitude().save(BaseCacheType.STRING, latitude);
        QaModeCache.getQaModeVenueLocationLongitude().save(BaseCacheType.STRING, longitude);
    }

    @OnCheckedChanged(R.id.fake_venue_location_to_custom_position) void onCustomLocationSelected(boolean isChecked){
        if (isChecked){
            setAdditionalSettingsVisibility(!isChecked);
            QaModeCache.getQaModeVenueLocationType().save(BaseCacheType.STRING, FakeVenueLocationType.CUSTOM.name());
            progressBar.setVisibility(View.VISIBLE);
            locationInteractor.process()
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(location -> save(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())),
                            e->progressBar.setVisibility(View.GONE),
                            ()->progressBar.setVisibility(View.GONE));
        }
    }

    private void setAdditionalSettingsVisibility(boolean isChecked) {
        currentMessage.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        customMessage.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        latitudeLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        longitudeLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    }
}
