package app.delivering.component.main.photo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import app.R;
import app.delivering.mvp.main.photo.init.events.ChooseDialogResultEvent;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoChooseDialogFragment extends DialogFragment {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_GALLERY = 2;
    public static final int FACEBOOK_IMPORT = 3;
    private boolean isOpenedFromWarning;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.fragment_choose_photo_dialog, null);
        ButterKnife.bind(this, view);
        builder.setView(view);
        return builder.create();
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog().getWindow()).setGravity(Gravity.BOTTOM);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setUpOpeningOption(boolean isOpenedFromWarning) {
        this.isOpenedFromWarning = isOpenedFromWarning;
    }

    @OnClick(R.id.text_view_take_photo)
    public void onTakePhotoClick(View view) {
        success(TAKE_PHOTO);
    }

    @OnClick(R.id.text_view_choose_gallery)
    public void onChooseGalleryClick(View view) {
        success(CHOOSE_GALLERY);
    }

    @OnClick(R.id.import_from_facebook)
    public void onFacebookImportClick(View view) {
        success(FACEBOOK_IMPORT);
    }

    @OnClick(R.id.text_view_cancel)
    public void onCancelClick(View view) {
        this.dismissAllowingStateLoss();
        isOpenedFromWarning = false;
    }

    private void success(int result) {
        ChooseDialogResultEvent chooseDialogResultEvent = new ChooseDialogResultEvent(result);
        chooseDialogResultEvent.setIsOpeningStateFromWarning(isOpenedFromWarning);
        EventBus.getDefault().post(chooseDialogResultEvent);
        this.dismissAllowingStateLoss();
    }
}
