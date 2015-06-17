package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.tomlocksapps.matrixcodes.model.CameraModelHelper;
import com.tomlocksapps.matrixcodes.model.GlobalPosition;
import com.tomlocksapps.matrixcodes.model.QRCode;
import com.tomlocksapps.matrixcodes.utils.FileUtils;
import com.tomlocksapps.matrixcodes.utils.ImageUtils;
import com.tomlocksapps.matrixcodes.utils.Log;
import com.tomlocksapps.matrixcodes.view.DrawView;
import com.tomlocksapps.matrixcodes.view.UserPositionView;

import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 2014-10-12.
 */
public class CameraHelper {

    private CameraPreview cameraPreview;
    private Camera camera;
    private Activity activity;

    private FrameLayout preview;
    private DrawView drawView;
    private UserPositionView userPositionView;
    private Button buttonStart;
    private long startTime;
    private boolean previewActive;

    private Camera.Parameters cameraParameters;

    private boolean focusLock;

    private boolean cameraInitialized;

    private OnImagePreviewListener onImagePreviewListener;

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

        this.buttonStart = (Button) activity.findViewById(R.id.buttonStart);

        ((Button)activity.findViewById(R.id.buttonSeparator)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("AppResults: ; ------------- Zmieniono odleglosc -----------------------------", Log.LogType.OTHER, this);
                Log.d("CameraModel: ; ------------- Zmieniono odleglosc -----------------------------", Log.LogType.OTHER, this);
            }
        });

//        this.cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//
//        camera.setParameters(cameraParameters);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                previewActive = true;
            }
        });

//        preview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                camera.autoFocus(new Camera.AutoFocusCallback() {
////                    @Override
////                    public void onAutoFocus(boolean success, Camera camera) {
////                        if (success)
////                            camera.takePicture(null, null, pictureCallback);
////                    }
////                });
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        camera.startPreview();
//
//                    }
//                });
//
//            }
//        });

        userPositionView = (UserPositionView) activity.findViewById(R.id.userPositionView);

        userPositionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalPosition!=null) {
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

    public void onResume() {
        Log.d("onResume", Log.LogType.LIFECYCLE, this);

        if(!cameraInitialized){
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


        focusLock = false;

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

        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);

        camera.setParameters(cameraParameters);
    }



    public void setOnImagePreviewListener(OnImagePreviewListener onImagePreviewListener) {
        this.onImagePreviewListener = onImagePreviewListener;
    }

    public interface OnImagePreviewListener {
        public void onImagePreview(Bitmap bmp);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {

            if (((MatrixCodesApplication) activity.getApplication()).isOpenCVLoaded()) {

                int format = camera.getParameters().getPictureFormat();

                Camera.Size size = camera.getParameters().getPictureSize();

                if(format == ImageFormat.JPEG) {
                    Mat m = new Mat(1, data.length, CvType.CV_8UC1);
                    m.put(0, 0, data);
                    android.util.Log.d("Mat", "rows: " + m.rows() + " ;cols: " + m.cols());
                    Mat bgrMat;
                    bgrMat = Highgui.imdecode(m, Highgui.IMREAD_GRAYSCALE);
                    android.util.Log.d("bgrMat", "rows: " + bgrMat.rows() + " ;cols: " + bgrMat.cols() + "; type: " + bgrMat.type());

                    QRCode qrCode = QRCodeFinder.findFinderPattern(bgrMat, true);

                    Bitmap bmp = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(bgrMat, bmp);

                    if(onImagePreviewListener!=null)
                        onImagePreviewListener.onImagePreview(bmp);
                    //  imageViewPreview.setImageBitmap(bmp);



                    android.util.Log.d("QRCode", "QRCode :" + qrCode);

                    if(qrCode!=null) {


                        qrCode.snipCode(bgrMat);

//                        Bitmap bmp = Bitmap.createBitmap(qrCode.getQrCodeMat().cols(), qrCode.getQrCodeMat().rows(), Bitmap.Config.ARGB_8888);
//                         Utils.matToBitmap(qrCode.getQrCodeMat(), bmp);
//
//                         imageViewPreview.setImageBitmap(bmp);

                    }


                }

//                Toast.makeText(activity, "picture taken", Toast.LENGTH_SHORT).show();

            }

//            camera.startPreview();

            focusLock = false;
        }
    };


    private  Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            android.util.Log.d("focus", "onPreviewFrame focusLock: " + focusLock);



            if (((MatrixCodesApplication) activity.getApplication()).isOpenCVLoaded()  ) { // previewActive

                if (focusLock)
                    return;

                long previewStartTime = System.currentTimeMillis();


                Camera.Size size = camera.getParameters().getPreviewSize();


                Mat mYuv = new Mat(size.height + size.height / 2, size.width, CvType.CV_8UC1);
                mYuv.put(0, 0, data);
                Mat mRgba = new Mat();

                Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);

//                Bitmap map = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
//
//                Utils.matToBitmap(mRgba, map);
//
//                      imageViewPreview.setImageBitmap(map);

                final QRCode qrCode = QRCodeFinder.findFinderPattern(mRgba, false);




                android.util.Log.d("focus", "QrCode : " + qrCode);
                if (qrCode == null) {





//                    QRCodeFinder.findFocus(mRgba);

                    if (camera.getParameters().getMaxNumFocusAreas() > 0 && !focusLock) {

                        android.util.Log.d("focus", "focus inside");

                        focusLock = true;

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

                        //          camera.cancelAutoFocus();


                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        parameters.setFocusAreas(focusAreas);


                        camera.setParameters(parameters);
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {

                                android.util.Log.d("focus", "focus onAutoFocus success:" + success);

                                focusLock = false;

                            }
                        });


                    }

                }else {

//                    Bitmap map = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.RGB_565);
////
//
//
//                    Utils.matToBitmap(mRgba, map);

//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                map = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
//                map.setHasAlpha(false);

//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                    map.compress(Bitmap.CompressFormat.PNG, 100, bytes);
//
////you can create a new file name "test.jpg" in sdcard folder.
//                    File f = new File(Environment.getExternalStorageDirectory()
//                            + File.separator + "test.jpg");
//                    try {
//                        f.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
////write the bytes in file
//                    FileOutputStream fo = null;
//                    try {
//                        fo = new FileOutputStream(f);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        fo.write(bytes.toByteArray());
//                        fo.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

// remember close de FileOutput


//                    if(onImagePreviewListener!=null)
////                                onImagePreviewListener.onImagePreview(map2);
//                        onImagePreviewListener.onImagePreview(map);

                    boolean result = qrCode.snipCode(mRgba);
//                    Mat m = mRgba;
//                    int rows = m.rows();
//                    int cols = m.cols();
//                    Point p1 = new Point();
//
//                    p1.x = size.height - qrCode.getTopLeftFP().getCenter().y;
//                    p1.y = qrCode.getTopLeftFP().getCenter().x;

//                    drawView.setRatio(size);
//                //    drawView.setPoints(qrCode.getTopLeftFP()
//                // .getCenter(), qrCode.getTopRightFP().getCenter(), qrCode.getBottomLeftFP().getCenter());
//                    drawView.setPoints(p1,p1,p1);
//                    drawView.invalidate();

                    android.util.Log.d("focus", "qrCode snipCode: " + result);
                    if (result) {

                        Bitmap map2 = Bitmap.createBitmap(qrCode.getQrCodeMat().cols(), qrCode.getQrCodeMat().rows(), Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(qrCode.getQrCodeMat(), map2);


//                        Bitmap map = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
//
//                         Utils.matToBitmap(mRgba, map);
//
//
//                        FileUtils.saveBmpToFile(map, "img.png");
//                        FileUtils.saveBmpToFile(map2, "snip.png");


                        int[] intArray = new int[map2.getWidth()*map2.getHeight()];
//copy pixel data from the Bitmap into the 'intArray' array
                        map2.getPixels(intArray, 0, map2.getWidth(), 0, 0, map2.getWidth(), map2.getHeight());


                        LuminanceSource source = new RGBLuminanceSource(map2.getWidth(), map2.getHeight(), intArray);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        MultiFormatReader barcodeReader = new MultiFormatReader();
                        Result resultZxing = null;

                        String finalResult = "";
                        try {

//                                Map<DecodeHintType, Object> hints = new


                            //TODO set Hints
                            resultZxing = barcodeReader.decode(bitmap);

                            finalResult = String.valueOf(resultZxing.getText());



                        } catch (com.google.zxing.NotFoundException e) {
                            e.printStackTrace();
                            //throw new BarcodeEngine().new BarcodeEngineException(e.getMessage());
                        }

                        if(!finalResult.equals("")) {
//                            Toast.makeText(activity.getApplicationContext(), finalResult, Toast.LENGTH_SHORT).show();
                            Log.d("UserPosition: "+qrCode.getUserPosition(), Log.LogType.OTHER, this);


                           // qrCode.getTopLeftFP().getTopLeft().y - qrCode.getTopLeftFP().getBottomLeft().y

//                            double dist = ImageUtils.calculateDistance(qrCode.getTopLeftFP().getTopLeft(), qrCode.getTopLeftFP().getBottomLeft());
                            Double dist2w = ImageUtils.calculateDistance( qrCode.getTopLeftFP().getTopLeft() , qrCode.getTopRightFP().getTopRight());


                            Log.d("CameraModel: ;" + Build.MODEL + "; " + size.width +  "x" + + size.height+ ";" + dist2w , Log.LogType.OTHER, this);


//                            Log.d("DistancePoints: " + dist2w, Log.LogType.OTHER, this);

                            previewActive = false;

                            List<Integer> dividers = new ArrayList<Integer>();
                            dividers.add(2);
                            dividers.add(5);
                            dividers.add(8);
                            dividers.add(11);
                            dividers.add(12);



                            if(qrCode.parseCode(finalResult, dividers) && CameraModelHelper.getFactor(Build.MODEL, size) != null) {
                                qrCode.computeUserPosition();
                                QRCode.UserPosition userPosition = qrCode.getUserPosition();



                                MathOpertions mathOpertions = new MathOpertions(qrCode.getBottomLeftFP().getBottomLeft(),
                                        qrCode.getTopLeftFP().getTopLeft(), qrCode.getTopRightFP().getTopRight(),
                                        qrCode.getQrCodeContent().getSize(), CameraModelHelper.getFactor(Build.MODEL, size), userPosition);



                                if(userPosition == QRCode.UserPosition.DOWN) {
                                    Toast.makeText(activity, "Podnies telefon", Toast.LENGTH_SHORT).show();
                                    //drawView.startAnimation(new TranslateAnimation());
                                }   else if(userPosition == QRCode.UserPosition.UP) {
                                    Toast.makeText(activity, "Obniz telefon", Toast.LENGTH_SHORT).show();
                                    //drawView.startAnimation(new TranslateAnimation());
                                }

                                Log.d("UserPosition degree: " + mathOpertions.getCodeAngle() + " distance: " + mathOpertions.getDistanceToCode() + ", position:" + userPosition, Log.LogType.OTHER, this);
                                globalPosition = GlobalPosition.getGlobalPosition(qrCode.getQrCodeContent().getP(),qrCode.getQrCodeContent().getAngle(),mathOpertions.getDistanceToCode(),mathOpertions.getCodeAngle());
                                Log.d("UserPosition point: x: " + globalPosition.x + " y: " + globalPosition.y, Log.LogType.OTHER, this);


                                userPositionView.setVisibility(View.VISIBLE);
                                userPositionView.setRotation(qrCode.getQrCodeContent().getAngle());
                                userPositionView.setUserPosition(mathOpertions.getDistanceToCode());
                                userPositionView.setUserAngle(mathOpertions.getCodeAngle());
                                userPositionView.setGlobalPosition(globalPosition);
                                userPositionView.invalidate();



                                long calculationTime = System.currentTimeMillis() - startTime;
                                long algorithmTime = System.currentTimeMillis() - previewStartTime;

                                Log.d("AppResults: ;" + Build.MODEL +"; " + algorithmTime +  ";"  + calculationTime +   "; " + size.height + "x" + size.width + ";" + mathOpertions.getDistanceToCode() + ";" + mathOpertions.getCodeAngle() + "; " + userPosition , Log.LogType.OTHER, this);

                            }

                            //camera.cancelAutoFocus();


                            if(onImagePreviewListener!=null)
                                onImagePreviewListener.onImagePreview(map2);
//                                onImagePreviewListener.onImagePreview(map);
//                            camera.stopPreview();
                        }
                        android.util.Log.d("finalResult", "finalResult: " + finalResult);

                        android.util.Log.d("focus", "focus: areas: " + camera.getParameters().getMaxNumFocusAreas() + " focusLock: " + focusLock);

                        focusLock = false;


                    }


                }


            }
        }
    };


}
