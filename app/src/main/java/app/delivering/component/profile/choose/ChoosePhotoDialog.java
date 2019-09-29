package app.delivering.component.profile.choose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChoosePhotoDialog extends DialogFragment {
    private Unbinder bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_choose_photo, container, false);
        bind = ButterKnife.bind(this, v);
        return v;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @OnClick(R.id.button_ok) void buttonOk() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
    }

    @OnClick(R.id.button_cancel) void buttonCancel() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
    }

    @OnClick({R.id.button_ok, R.id.button_cancel}) void buttons() {dismiss();}

}
