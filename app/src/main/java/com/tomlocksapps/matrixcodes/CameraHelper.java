package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.tomlocksapps.matrixcodes.model.CameraModelHelper;
import com.tomlocksapps.matrixcodes.model.GlobalPosition;
import com.tomlocksapps.matrixcodes.model.QRCode;
import com.tomlocksapps.matrixcodes.state.UserStateController;
import com.tomlocksapps.matrixcodes.utils.ImageUtils;
import com.tomlocksapps.matrixcodes.utils.Log;
import com.tomlocksapps.matrixcodes.view.DistanceDialogFragment;
import com.tomlocksapps.matrixcodes.view.DrawView;
import com.tomlocksapps.matrixcodes.view.UserPositionView;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 2014-10-12.
 */
public class CameraHelper {

    private final static String BUNLDE_PREVIEW_LOCK = "bundleFocusLock";



    private CameraPreview cameraPreview;
    private Camera camera;
    private Activity activity;

    private FrameLayout preview;
    private DrawView drawView;
    private UserPositionView userPositionView;
    private Button buttonStart;
    private long startTime;
    private boolean previewActive;
    private Button buttonStartAgain;

    private Camera.Parameters cameraParameters;

    private boolean previewLock;

    private boolean cameraInitialized;

    private int focusCount;
    private static final int FOCUS_COUNT_MAX = 5;

    private OnImagePreviewListener onImagePreviewListener;
    private UserStateController userStateController;
    private DistanceDialogFragment dialogFragment;
    private DistanceDialogFragment.OnDistanceDialogListener onDistanceDialogListener = new DistanceDialogFragment.OnDistanceDialogListener() {
        @Override
        public void onUserClick(int previewId) {
            setPreviewSize(getParameters().getSupportedPreviewSizes().get(previewId));
            previewLock = false;
            focusCount = 0;
        }

        @Override
        public void onUserCancel() {
            dialogFragment.dismiss();;
            previewLock = false;
            focusCount = 0;
        }
    };

    public OnImagePreviewListener getOnImagePreviewListener() {
        return onImagePreviewListener;
    }

    private Point globalPosition;

    CameraHelper(final Activity activity, final FrameLayout preview, DrawView drawView) {

        Log.d("Constructor", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        this.activity = activity;
        this.preview = preview;
        this.drawView = drawView;

        this.camera = getCameraInstance(); // available in onResume method


        this.cameraParameters = camera.getParameters(); // available in onResume method

        this.userStateController = new UserStateController(activity);

        dialogFragment = new DistanceDialogFragment();
        dialogFragment.setOnDistanceDialogListener(onDistanceDialogListener);
        dialogFragment.setUserStateController(userStateController);

        buttonStartAgain = (Button) activity.findViewById(R.id.button_start_again);
        buttonStartAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPositionView.setVisibility(View.GONE);
                onImagePreviewListener.onImageVisiblityChange(View.GONE);
                previewLock = false;
                v.setVisibility(View.GONE);
            }
        });

        buttonStartAgain.setVisibility(View.GONE);

        this.buttonStart = (Button) activity.findViewById(R.id.buttonStart);

        ((Button) activity.findViewById(R.id.buttonSeparator)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("AppResults: ; ------------- Zmieniono odleglosc -----------------------------", Log.LogType.OTHER, this);
                Log.d("CameraModel: ; ------------- Zmieniono odleglosc -----------------------------", Log.LogType.OTHER, this);
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                previewActive = true;
            }
        });


        userPositionView = (UserPositionView) activity.findViewById(R.id.userPositionView);

        userPositionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalPosition != null) {
                    Intent i = new Intent(activity, MapActivity.class);
                    i.putExtra(MapActivity.BUNDLE_GLOBAL_X, globalPosition.x);
                    i.putExtra(MapActivity.BUNDLE_GLOBAL_Y, globalPosition.y);
                    activity.startActivity(i);
                }
            }
        });

        cameraInitialized = true;
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void onStart() {
        com.tomlocksapps.matrixcodes.utils.Log.d("onStart", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        previewLock = false;
    }

    public void onResume() {
        Log.d("onResume", Log.LogType.LIFECYCLE, this);

        if (!cameraInitialized) {
            if (camera != null) {
                camera.release();
                camera = null;
            }

            // Create an instance of Camera
            camera = getCameraInstance();
        }

        camera.setParameters(cameraParameters);

        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(activity, camera, previewCallback);

        preview.addView(cameraPreview);

        if(buttonStartAgain.getVisibility() != View.VISIBLE && !dialogFragment.isVisible())
            previewLock = false;

        cameraInitialized = false;
    }


    protected void onPause() {
        Log.d("onPause", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        if (camera != null) {
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.stopPreview();
            camera.release();
            preview.removeView(cameraPreview);
            camera = null;
        }
    }

    public Camera.Parameters getParameters() {
        return cameraParameters;
    }

    public void setPictureSize(Camera.Size pictureSize) {

        cameraParameters.setPictureSize(pictureSize.width, pictureSize.height);

        camera.setParameters(cameraParameters);
    }

    public void setPreviewSize(Camera.Size previewSize) {

        Log.d(""+ previewSize.width + "x" + previewSize.height, Log.LogType.CAMERA , this);

        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);

        camera.setParameters(cameraParameters);
    }


    public void setOnImagePreviewListener(OnImagePreviewListener onImagePreviewListener) {
        this.onImagePreviewListener = onImagePreviewListener;
    }

    public interface OnImagePreviewListener {
       void onImagePreview(Bitmap bmp);
       void onImageVisiblityChange(int state);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {

            if (((MatrixCodesApplication) activity.getApplication()).isOpenCVLoaded()) {

                int format = camera.getParameters().getPictureFormat();

                Camera.Size size = camera.getParameters().getPictureSize();

                if (format == ImageFormat.JPEG) {
                    Mat m = new Mat(1, data.length, CvType.CV_8UC1);
                    m.put(0, 0, data);
                    android.util.Log.d("Mat", "rows: " + m.rows() + " ;cols: " + m.cols());
                    Mat bgrMat;
                    bgrMat = Highgui.imdecode(m, Highgui.IMREAD_GRAYSCALE);
                    android.util.Log.d("bgrMat", "rows: " + bgrMat.rows() + " ;cols: " + bgrMat.cols() + "; type: " + bgrMat.type());

                    QRCode qrCode = QRCodeFinder.findFinderPattern(bgrMat, true);

                    Bitmap bmp = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(bgrMat, bmp);

                    if (onImagePreviewListener != null)
                        onImagePreviewListener.onImagePreview(bmp);
                    //  imageViewPreview.setImageBitmap(bmp);


                    android.util.Log.d("QRCode", "QRCode :" + qrCode);

                    if (qrCode != null) {


                        qrCode.snipCode(bgrMat);


                    }


                }

            }

            previewLock = false;
        }
    };


    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            android.util.Log.d("focus", "onPreviewFrame previewLock: " + previewLock);


            if (((MatrixCodesApplication) activity.getApplication()).isOpenCVLoaded()) { // previewActive

                if (previewLock)
                    return;

                if (focusCount > FOCUS_COUNT_MAX) {
                    previewLock = true;

                    Fragment fragment = activity.getFragmentManager().findFragmentByTag("distanceFragment");

                    if(fragment != null && fragment instanceof DialogFragment){
                        DialogFragment dialogFragment = (DialogFragment) fragment;
                        dialogFragment.dismiss();
                    }

                    dialogFragment.show(activity.getFragmentManager(), "distanceFragment");
                }

                long previewStartTime = System.currentTimeMillis();


                Camera.Size size = camera.getParameters().getPreviewSize();


                Mat mYuv = new Mat(size.height + size.height / 2, size.width, CvType.CV_8UC1);
                mYuv.put(0, 0, data);
                Mat mRgba = new Mat();

                Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);


                final QRCode qrCode = QRCodeFinder.findFinderPattern(mRgba, false);


                android.util.Log.d("focus", "QrCode : " + qrCode);
                if (qrCode == null) {



                    if (camera.getParameters().getMaxNumFocusAreas() > 0 && !previewLock) {

                        android.util.Log.d("focus", "focus inside");

                        previewLock = true;

                        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>(1);

                        int centerX = (int) 0;
                        ;
                        int centerY = (int) 0;

                        final int OFFSET = 100;


                        int left = centerX - OFFSET;
                        int right = centerX + OFFSET;
                        int top = centerY - OFFSET;
                        int bottom = centerY + OFFSET;

                        Rect rect = new Rect(left, top, right, bottom);
                        Camera.Area area = new Camera.Area(rect, 1000);
                        focusAreas.add(area);

                        android.util.Log.d("focus", "focus rect: " + rect.toString());


                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        parameters.setFocusAreas(focusAreas);


                        camera.setParameters(parameters);
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {

                                android.util.Log.d("focus", "focus onAutoFocus success:" + success);

                                previewLock = false;

                            }
                        });

                        focusCount++;
                    }

                } else {

                    boolean result = qrCode.snipCode(mRgba);

                    android.util.Log.d("focus", "qrCode snipCode: " + result);
                    if (result) {

                        Bitmap map2 = Bitmap.createBitmap(qrCode.getQrCodeMat().cols(), qrCode.getQrCodeMat().rows(), Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(qrCode.getQrCodeMat(), map2);

                        int[] intArray = new int[map2.getWidth() * map2.getHeight()];
                        map2.getPixels(intArray, 0, map2.getWidth(), 0, 0, map2.getWidth(), map2.getHeight());


                        LuminanceSource source = new RGBLuminanceSource(map2.getWidth(), map2.getHeight(), intArray);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        MultiFormatReader barcodeReader = new MultiFormatReader();
                        Result resultZxing = null;

                        String finalResult = "";
                        try {

                            //TODO set Hints
                            resultZxing = barcodeReader.decode(bitmap);

                            finalResult = String.valueOf(resultZxing.getText());


                        } catch (com.google.zxing.NotFoundException e) {
                            e.printStackTrace();

                        }

                        if (!finalResult.equals("")) {
                            Log.d("UserPosition: " + qrCode.getUserPosition(), Log.LogType.OTHER, this);

                            Double dist2w = ImageUtils.calculateDistance(qrCode.getTopLeftFP().getTopLeft(), qrCode.getTopRightFP().getTopRight());


                            Log.d("CameraModel: ;" + Build.MODEL + "; " + size.width + "x" + +size.height + ";" + dist2w, Log.LogType.OTHER, this);


                            previewActive = false;

                            List<Integer> dividers = new ArrayList<Integer>();
                            dividers.add(2);
                            dividers.add(5);
                            dividers.add(8);
                            dividers.add(11);
                            dividers.add(12);


                            if (qrCode.parseCode(finalResult, dividers) && CameraModelHelper.getFactor(Build.MODEL, size) != null) {
                                qrCode.computeUserPosition();
                                QRCode.UserPosition userPosition = qrCode.getUserPosition();


                                MathOpertions mathOpertions = new MathOpertions(qrCode.getBottomLeftFP().getBottomLeft(),
                                        qrCode.getTopLeftFP().getTopLeft(), qrCode.getTopRightFP().getTopRight(),
                                        qrCode.getQrCodeContent().getSize(), CameraModelHelper.getFactor(Build.MODEL, size), userPosition);




                                Log.d("UserPosition degree: " + mathOpertions.getCodeAngle() + " distance: " + mathOpertions.getDistanceToCode() + ", position:" + userPosition, Log.LogType.OTHER, this);
                                globalPosition = GlobalPosition.getGlobalPosition(qrCode.getQrCodeContent().getP(), qrCode.getQrCodeContent().getAngle(), mathOpertions.getDistanceToCode(), mathOpertions.getCodeAngle());
                                Log.d("UserPosition point: x: " + globalPosition.x + " y: " + globalPosition.y, Log.LogType.OTHER, this);


                                userPositionView.setVisibility(View.VISIBLE);
                                userPositionView.setRotation(qrCode.getQrCodeContent().getAngle());
                                userPositionView.setUserPosition(mathOpertions.getDistanceToCode());
                                userPositionView.setUserAngle(mathOpertions.getCodeAngle());
                                userPositionView.setGlobalPosition(globalPosition);
                                userPositionView.invalidate();


                                long calculationTime = System.currentTimeMillis() - startTime;
                                long algorithmTime = System.currentTimeMillis() - previewStartTime;

                                Log.d("AppResults: ;" + Build.MODEL + "; " + algorithmTime + ";" + calculationTime + "; " + size.height + "x" + size.width + ";" + mathOpertions.getDistanceToCode() + ";" + mathOpertions.getCodeAngle() + "; " + userPosition, Log.LogType.OTHER, this);

                            }

                            focusCount = 0;

                            previewLock = true;



                            buttonStartAgain.setVisibility(View.VISIBLE);

                            if (onImagePreviewListener != null)
                                onImagePreviewListener.onImagePreview(map2);

                            return;
                        }
                        android.util.Log.d("finalResult", "finalResult: " + finalResult);

                        android.util.Log.d("focus", "focus: areas: " + camera.getParameters().getMaxNumFocusAreas() + " previewLock: " + previewLock);

                        previewLock = false;




                    }


                }


            }
        }
    };

    public void onRestoreInstanceState(Bundle icicle) {
        Log.d("onRestoreInstanceState", Log.LogType.LIFECYCLE, this);

        if(icicle != null) {
            previewLock = icicle.getBoolean(BUNLDE_PREVIEW_LOCK);
        }
    }

    public void onSaveInstanceState(Bundle icicle) {
        Log.d("onSaveInstanceState", Log.LogType.LIFECYCLE, this);


        icicle.putBoolean(BUNLDE_PREVIEW_LOCK, previewLock);
    }

}
