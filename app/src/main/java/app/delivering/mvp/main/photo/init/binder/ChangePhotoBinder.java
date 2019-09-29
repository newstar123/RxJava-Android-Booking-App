package app.delivering.mvp.main.photo.init.binder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.main.photo.PhotoChooseDialogFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdateAfterAddingPhotoEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.photo.facebook.events.OnRestoreFBPhotoEvent;
import app.delivering.mvp.main.photo.init.events.ChooseDialogResultEvent;
import app.delivering.mvp.main.photo.init.events.CropResultEvent;
import app.delivering.mvp.main.photo.init.presenter.ChangePhotoPresenter;
import app.gateway.permissions.camera.CheckCameraPermissionGateway;
import app.gateway.permissions.storage.CheckExternalStoragePermissionGateway;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ChangePhotoBinder extends BaseBinder {
    private static final int REQUEST_IMAGE_CAPTURE = 211;
    private static final int REQUEST_GALLERY_CAPTURE = 212;
    @BindView(R.id.drawer_layout) View container;
    @BindView(R.id.photo) ImageView actionButton;
    @BindView(R.id.drawer_header_photo) ImageView photo;
    @BindView(R.id.update_photo_progress) MaterialProgressBar photoProgress;
    private ChangePhotoPresenter changePhotoPresenter;
    private InitExceptionHandler exceptionHandler;
    private String imagePath;
    private boolean sendUpdateToTab;

    public ChangePhotoBinder(BaseActivity activity) {
        super(activity);
        changePhotoPresenter = new ChangePhotoPresenter(getActivity());
        exceptionHandler = new InitExceptionHandler(getActivity());
    }

    @OnClick(R.id.photo) void click() {
        PhotoChooseDialogFragment photoChooseDialogFragment = new PhotoChooseDialogFragment();
        photoChooseDialogFragment.setUpOpeningOption(false);
        photoChooseDialogFragment.show(getActivity().getSupportFragmentManager(), "PhotoChooseDialogFragment");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseTypeEvent(ChooseDialogResultEvent event) {

        sendUpdateToTab = event.getIsOpeningStateFromWarning();

        switch (event.getResult()) {
            case PhotoChooseDialogFragment.TAKE_PHOTO:
                new CheckCameraPermissionGateway(getActivity()).check()
                        .subscribe(this::getPhoto, e->onError(e, event));
                break;
            case PhotoChooseDialogFragment.CHOOSE_GALLERY:
                new CheckExternalStoragePermissionGateway(getActivity()).check()
                        .subscribe(this::getImage, e->onError(e, event));
                break;
            case PhotoChooseDialogFragment.FACEBOOK_IMPORT:
                EventBus.getDefault().post(new OnRestoreFBPhotoEvent());
                break;
            default:
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.value_space_value),
                                getString(R.string.something_progress),
                                getString(R.string.please_try_again)), Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_OK) && requestCode == REQUEST_IMAGE_CAPTURE) {
            cropImage("file://" + imagePath);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            checkCropResult(resultCode, CropImage.getActivityResult(data));
        } else if (Activity.RESULT_OK == resultCode && requestCode == REQUEST_GALLERY_CAPTURE && data.getData() != null)
            cropImage(data.getData().toString());
    }

    private void getImage(Boolean isOk) {
        if (!isOk) return;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY_CAPTURE);
    }

    private void getPhoto(Boolean isOk) {
        if (!isOk) return;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.qorum.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void cropImage(String path) {
        try {
            File cropOutput = createImageFile();
            CropImage.activity(Uri.parse(path))
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setOutputCompressQuality(100)
                    .setOutputUri(Uri.parse("file://" + cropOutput.getPath()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAutoZoomEnabled(true)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMinCropResultSize(getActivity().getResources().getDimensionPixelOffset(R.dimen.dip100),
                            getActivity().getResources().getDimensionPixelOffset(R.dimen.dip100))
                    .start(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpeg", storageDir);
        imagePath = image.getPath();
        return image;
    }

    private void checkCropResult(int resultCode, CropImage.ActivityResult result) {
        if (resultCode == Activity.RESULT_OK)
            showCroppedAvatar(result);
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            Toast.makeText(getActivity(), result.getError().getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showCroppedAvatar(CropImage.ActivityResult result) {
        imagePath = result.getUri().getPath();
        actionButton.setEnabled(false);
        photoProgress.setVisibility(View.VISIBLE);
        CropResultEvent resultEvent = new CropResultEvent(imagePath);
        changePhotoPresenter.process(resultEvent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, e-> onError(e, new ChooseDialogResultEvent(0)), ()->actionButton.setEnabled(true));
    }

    private void onError(Throwable throwable, ChooseDialogResultEvent event) {
        actionButton.setEnabled(true);
        photoProgress.setVisibility(View.GONE);
        exceptionHandler.showError(throwable, v -> onChooseTypeEvent(event));
    }

    private void show(boolean response) {
        actionButton.setEnabled(true);
        photoProgress.setVisibility(View.GONE);
        Picasso.with(getActivity())
                .load(new File(imagePath))
                .resize(photo.getMeasuredWidth(), photo.getMeasuredHeight())
                .centerCrop()
                .into(photo);

        if (sendUpdateToTab)
            Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
            .subscribe(l->{
                EventBus.getDefault().postSticky(new UpdateAfterAddingPhotoEvent());
                getActivity().finish();
            }, e->{}, ()->{});

    }

}
