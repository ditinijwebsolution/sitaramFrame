package com.om.filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FilePickerController extends AppCompatActivity {
    private Activity mActivity;
    private static Context mContext;

    private File mSelectedFile;
    private Uri mSelectedFileUri;

    //FilePickerController Options
    String[] allowMimeTypes = {};
    boolean allowMultipleSelection = false;
    boolean allowTakePhoto = false;
    boolean allowTakeVideo = false;
    boolean allowFileManager = false;
    boolean allowDirectCapture = false;
    boolean allowFrontFacingCamera = false;
    boolean allowCroppingImage = false;
    boolean allowFixAspactRatio = false;
    int scaleQuality = 100;
    int videoMaxDuration = 4;
    boolean highQualityVideo = false;

    private FilePickerListener mFilePickerListener;
    private BottomSheetDialog mDialogFilePickerOptions;
    LinearLayout mLayoutTakePhoto, mLayoutTakeVideo, mLayoutFileManager;

    public interface FilePickerListener {
        void onSuccess(ArrayList<Uri> mSelectedFileList);
    }

    public FilePickerController() {
    }

    public FilePickerController(Activity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public void setAllowMultipleSelection(boolean allowMultiple) {
        this.allowMultipleSelection = allowMultiple;
    }

    public void setAllowMimeTypes(String[] mimeTypes) {
        this.allowMimeTypes = mimeTypes;
    }

    public void setAllowTakePhoto(boolean allowTakePhoto) {
        this.allowTakePhoto = allowTakePhoto;
    }

    public void setAllowTakeVideo(boolean allowTakeVideo) {
        this.allowTakeVideo = allowTakeVideo;
    }

    public void setAllowFileManager(boolean allowFileManager) {
        this.allowFileManager = allowFileManager;
    }

    public void setFilePickerListener(FilePickerListener filePickerListener) {
        this.mFilePickerListener = filePickerListener;
    }

    public void setAllowDirectCapture(boolean allowDirectCapture) {
        this.allowDirectCapture = allowDirectCapture;
    }

    public void setAllowCroppingImage(boolean allowCroppingImage) {
        this.allowCroppingImage = allowCroppingImage;
    }

    public void setAllowFixAspactRatio(boolean allowFixAspactRatio) {
        this.allowFixAspactRatio = allowFixAspactRatio;
    }

    public void setScaleQuality(int scaleQuality) {
        this.scaleQuality = scaleQuality;
    }

    public void setAllowFrontFacingCamera(boolean allowFrontFacingCamera) {
        this.allowFrontFacingCamera = allowFrontFacingCamera;
    }

    public void setVideoMaxDuration(int videoMaxDuration) {
        this.videoMaxDuration = videoMaxDuration;
    }

    public void setHighQualityVideo(boolean highQualityVideo) {
        this.highQualityVideo = highQualityVideo;
    }

    public void showFilePicker() {
        showDialogFilePickerOptions();
    }

    public void showCaptureImageFromCamera() {
        captureImageFromCamera();
    }

    public void showCaptureImageFromFileSelection(){
        openFileSelectionIntent();
    }

    public void showCaptureVideoFromCamera() {
        captureVideoFromCamera();
    }

    private void showDialogFilePickerOptions() {
        try {
            View sheetView = mActivity.getLayoutInflater().inflate(R.layout.dialog_file_picker_layout, null);
            setupDialogFields(sheetView);

            mDialogFilePickerOptions = new BottomSheetDialog(mActivity);
            mDialogFilePickerOptions.setContentView(sheetView);
            mDialogFilePickerOptions.show();

            FrameLayout bottomSheet = (FrameLayout) mDialogFilePickerOptions.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideDialogFilePickerOptions() {
        if (mDialogFilePickerOptions != null && mDialogFilePickerOptions.isShowing()) {
            mDialogFilePickerOptions.dismiss();
        }
    }

    private void setupDialogFields(View mView) {
        try {
            Button mBtnCancel = (Button) mView.findViewById(R.id.mBtnCancel);
            mLayoutTakePhoto = (LinearLayout) mView.findViewById(R.id.layout_btn_take_photo);
            mLayoutTakeVideo = (LinearLayout) mView.findViewById(R.id.layout_btn_take_video);
            mLayoutFileManager = (LinearLayout) mView.findViewById(R.id.layout_btn_choose_existing);

            if (allowTakePhoto) {
                mLayoutTakePhoto.setVisibility(View.VISIBLE);
                mLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideDialogFilePickerOptions();

                        captureImageFromCamera();
                    }
                });
            }

            if (allowTakeVideo) {
                mLayoutTakeVideo.setVisibility(View.VISIBLE);
                mLayoutTakeVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideDialogFilePickerOptions();

                        captureVideoFromCamera();
                    }
                });
            }

            if (allowFileManager) {
                mLayoutFileManager.setVisibility(View.VISIBLE);
                mLayoutFileManager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideDialogFilePickerOptions();

                        openFileSelectionIntent();
                    }
                });
            }

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialogFilePickerOptions();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findFrontFacingCamera(boolean frontFacing) {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (frontFacing && info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            } else if (frontFacing == false && info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }

        return cameraId;
    }

    private void captureImageFromCamera() {
        try {
            if (isPermissionGranted(mActivity, Manifest.permission.CAMERA) == false) {
                requestPermission(Manifest.permission.CAMERA, Constants.REQUEST_CODE_IMAGE_CAMERA);
            } else {
                mSelectedFile = null;
                mSelectedFileUri = null;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                    mSelectedFile = createImageFile();
                    mSelectedFileUri = getImageFileUri(mContext, mSelectedFile);

                    if (mSelectedFileUri != null) {
                        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", findFrontFacingCamera(allowFrontFacingCamera));
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedFileUri);
                        mActivity.startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureVideoFromCamera() {
        try {
            if (isPermissionGranted(mActivity, Manifest.permission.CAMERA) == false) {
                requestPermission(Manifest.permission.CAMERA, Constants.REQUEST_CODE_VIDEO_CAMERA);
            } else {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if (takeVideoIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoMaxDuration);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, highQualityVideo ? 1 : 0);
                    takeVideoIntent.putExtra("android.intent.extras.CAMERA_FACING", findFrontFacingCamera(allowFrontFacingCamera));

                    mActivity.startActivityForResult(takeVideoIntent, Constants.REQUEST_TAKE_VIDEO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFileSelectionIntent() {
        try {
            if (isPermissionGranted(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == false) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, allowMimeTypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelection);

                mActivity.startActivityForResult(intent, Constants.REQUEST_FILE_SELECTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_IMAGE_CAMERA) {
            if (isPermissionGranted(mActivity, Manifest.permission.CAMERA)) {
                captureImageFromCamera();
            }
        } else if (requestCode == Constants.REQUEST_CODE_VIDEO_CAMERA) {
            if (isPermissionGranted(mActivity, Manifest.permission.CAMERA)) {
                captureVideoFromCamera();
            }
        } else if (requestCode == Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (isPermissionGranted(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openFileSelectionIntent();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_TAKE_PHOTO
                    || requestCode == Constants.REQUEST_TAKE_VIDEO
                    || requestCode == Constants.REQUEST_FILE_SELECTION
                    /*|| requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE*/) {

                ArrayList<Uri> mSelectedFileList = new ArrayList<>();

                if (requestCode == Constants.REQUEST_TAKE_VIDEO) {
                    if (resultData != null && resultData.getData() != null) {
                        Uri videoUri = resultData.getData();
                        mSelectedFileList.add(videoUri);
                    }
                } else if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
                    if (allowCroppingImage) {
                        openCropImageIntent(mActivity, mContext, mSelectedFileUri);
                    } else {
                        applyImageCompression(mSelectedFileList);
                    }
                } /*else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    //CropImage.ActivityResult cropResultData = CropImage.getActivityResult(resultData);
                    //mSelectedFileList.add(cropResultData.getUri());
                   // mSelectedFileUri = cropResultData.getUri();
                    mSelectedFileUri = CropImage.getPickImageResultUriContent(mActivity,resultData);
                    applyImageCompression(mSelectedFileList);
                }*/ else if (requestCode == Constants.REQUEST_FILE_SELECTION) {
                    if (resultData != null) {
                        if (resultData.getClipData() != null) {
                            for (int i = 0; i < resultData.getClipData().getItemCount(); i++) {
                                Uri fileUri = resultData.getClipData().getItemAt(i).getUri();
                                mSelectedFileList.add(fileUri);
                            }
                        } else if (resultData.getData() != null) {
                            Uri fileUri = resultData.getData();

                            if (isImageFileType(fileUri)
                                    && allowCroppingImage) {
                                openCropImageIntent(mActivity, mContext, fileUri);
                            } else {
                                mSelectedFileList.add(fileUri);
                            }
                        }
                    }
                }

                if (mSelectedFileList != null
                        && mSelectedFileList.size() > 0) {
                    if (mFilePickerListener != null) {
                        mFilePickerListener.onSuccess(mSelectedFileList);
                    }
                }
            }
        }
    }

    private void applyImageCompression(ArrayList<Uri> mSelectedFileList) {
        try {
            ImageCompression mCompressor = new ImageCompression(mContext);
            mSelectedFile = mCompressor.compressToFile(mSelectedFile, scaleQuality);
            mSelectedFileUri = getImageFileUri(mContext, mSelectedFile);

            if (mSelectedFile != null && mSelectedFileUri != null) {
                mSelectedFileList.add(mSelectedFileUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMimeType(Uri fileUri) {
        ContentResolver cR = mContext.getContentResolver();
        return cR.getType(fileUri);
    }

    private boolean isImageFileType(Uri fileUri) {
        String mimeType = getMimeType(fileUri);

        if (Arrays.asList(Constants.IMAGE_MIME_TYPES).contains(mimeType)) {
            return true;
        }

        return false;
    }

    private Uri getImageFileUri(Context mContext, File fileObj) {
        Uri result = null;

        try {
            if (fileObj != null) {
                result = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", fileObj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private File getOutputMediaFile(Context context) throws IOException {
        File mediaStorageDir = new File(context.getFilesDir(), "images");

        if (mediaStorageDir.exists() == false) {
            if (mediaStorageDir.mkdirs() == false) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mediaFileName = "IMG_" + timeStamp + ".jpg";

        return new File(mediaStorageDir.getPath() + File.separator + mediaFileName);
    }

    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private boolean isPermissionGranted(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
        }
    }

    private final ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if(result.isSuccessful()){
            ArrayList<Uri> mSelectedFileList = new ArrayList<>();

            mSelectedFileUri = result.getUriContent();
            applyImageCompression(mSelectedFileList);

            if (mSelectedFileList != null
                    && mSelectedFileList.size() > 0) {
                if (mFilePickerListener != null) {
                    mFilePickerListener.onSuccess(mSelectedFileList);
                }
            }
        }
    });

    private void openCropImageIntent(Activity mActivity, Context mContext, Uri fileUri) {
        try {
            if (fileUri == null) {
                return;
            }

            File outputFile = createImageFile();
            Uri outputUri = getImageFileUri(mContext, outputFile);
            CropImageOptions options = new CropImageOptions();
            options.guidelines = CropImageView.Guidelines.ON;
            options.aspectRatioX = 1;
            options.aspectRatioY = 1;
            options.fixAspectRatio = true;
            options.customOutputUri = outputUri;

            cropImage.launch(new CropImageContractOptions(fileUri,options));
           /* CropImage.activity(fileUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setFixAspectRatio(allowFixAspactRatio)
                    .setOutputUri(outputUri)
                    .start(mActivity);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}