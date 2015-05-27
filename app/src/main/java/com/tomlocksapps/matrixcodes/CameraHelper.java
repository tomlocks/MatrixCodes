package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.tomlocksapps.matrixcodes.model.CameraModel;
import com.tomlocksapps.matrixcodes.model.GlobalPosition;
import com.tomlocksapps.matrixcodes.model.QRCode;
import com.tomlocksapps.matrixcodes.model.QRCodeContent;
import com.tomlocksapps.matrixcodes.model.QRCodeContentParser;
import com.tomlocksapps.matrixcodes.utils.ImageUtils;
import com.tomlocksapps.matrixcodes.utils.Log;
import com.tomlocksapps.matrixcodes.view.DrawView;
import com.tomlocksapps.matrixcodes.view.UserPositionView;

import android.os.Build;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

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

    private CameraPreview cameraPreview;
    private Camera camera;
    private Activity activity;

    private FrameLayout preview;
    private DrawView drawView;

    private Camera.Parameters cameraParameters;

    private boolean focusLock;

    private boolean cameraInitialized;

    private OnImagePreviewListener onImagePreviewListener;

    public OnImagePreviewListener getOnImagePreviewListener() {
        return onImagePreviewListener;
    }

    CameraHelper(final Activity activity, FrameLayout preview, DrawView drawView) {

        Log.d("Constructor", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        this.activity = activity;
        this.preview = preview;
        this.drawView = drawView;

        this.camera = getCameraInstance(); // available in onResume method

        this.cameraParameters = camera.getParameters(); // available in onResume method

//        this.cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//
//        camera.setParameters(cameraParameters);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                camera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (success)
//                            camera.takePicture(null, null, pictureCallback);
//                    }
//                });

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startPreview();

                    }
                });

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

                    QRCode qrCode = QRCodeFinder.findFinderPattern(bgrMat, false);

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



            if (((MatrixCodesApplication) activity.getApplication()).isOpenCVLoaded()) {

                if (focusLock)
                    return;



                Camera.Size size = camera.getParameters().getPreviewSize();

                long start = System.currentTimeMillis();

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


                    boolean result = qrCode.snipCode(mRgba);
                    Mat m = mRgba;
                    int rows = m.rows();
                    int cols = m.cols();
                    Point p1 = new Point();

                    p1.x = size.height - qrCode.getTopLeftFP().getCenter().y;
                    p1.y = qrCode.getTopLeftFP().getCenter().x;

//                    drawView.setRatio(size);
//                //    drawView.setPoints(qrCode.getTopLeftFP()
//                // .getCenter(), qrCode.getTopRightFP().getCenter(), qrCode.getBottomLeftFP().getCenter());
//                    drawView.setPoints(p1,p1,p1);
//                    drawView.invalidate();

                    android.util.Log.d("focus", "qrCode snipCode: " + result);
                    if (result) {

                        Bitmap map2 = Bitmap.createBitmap(qrCode.getQrCodeMat().cols(), qrCode.getQrCodeMat().rows(), Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(qrCode.getQrCodeMat(), map2);


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

                            double dist = ImageUtils.calculateDistance(qrCode.getTopLeftFP().getTopLeft(), qrCode.getTopLeftFP().getBottomLeft());

                            Log.d("CameraModel: " + dist , Log.LogType.OTHER, this);

                            List<Integer> dividers = new ArrayList<Integer>();
                            dividers.add(2);
                            dividers.add(5);
                            dividers.add(8);
                            dividers.add(11);
                            dividers.add(12);



                            if(qrCode.parseCode(finalResult, dividers)) {
                                qrCode.computeUserPosition();
                                QRCode.UserPosition userPosition = qrCode.getUserPosition();



                                MathOpertions mathOpertions = new MathOpertions(qrCode.getBottomLeftFP().getCenter(), qrCode.getTopLeftFP().getCenter(), qrCode.getTopRightFP().getCenter(), qrCode.getQrCodeContent().getSize(), CameraModel.getFactor(Build.MODEL), userPosition);



                                if(userPosition == QRCode.UserPosition.DOWN) {
                                    Toast.makeText(activity, "Podnies telefon", Toast.LENGTH_SHORT).show();
                                    //drawView.startAnimation(new TranslateAnimation());
                                }   else if(userPosition == QRCode.UserPosition.UP) {
                                    Toast.makeText(activity, "Obniz telefon", Toast.LENGTH_SHORT).show();
                                    //drawView.startAnimation(new TranslateAnimation());
                                }

                                Log.d("UserPosition degree: " + mathOpertions.getCodeAngle() + " distance: " + mathOpertions.getDistanceToCode() + ", position:" + userPosition, Log.LogType.OTHER, this);
                                Point global = GlobalPosition.getGlobalPosition(qrCode.getQrCodeContent().getP(),qrCode.getQrCodeContent().getAngle(),mathOpertions.getDistanceToCode(),mathOpertions.getCodeAngle());
                                Log.d("UserPosition point: x: " + global.x + " y: " + global.y, Log.LogType.OTHER, this);


                                UserPositionView userPositionView = (UserPositionView) activity.findViewById(R.id.userPositionView);
                                userPositionView.setVisibility(View.VISIBLE);
                                userPositionView.setRotation(qrCode.getQrCodeContent().getAngle());
                                userPositionView.setUserPosition(mathOpertions.getDistanceToCode());
                                userPositionView.setUserAngle(mathOpertions.getCodeAngle());
                                userPositionView.setGlobalPosition(global);
                                userPositionView.invalidate();

                            }

                            //camera.cancelAutoFocus();

                            if(onImagePreviewListener!=null)
                                onImagePreviewListener.onImagePreview(map2);
//                            camera.stopPreview();
                        }
                        android.util.Log.d("finalResult", "finalResult: " + finalResult);

                        android.util.Log.d("focus", "focus: areas: " + camera.getParameters().getMaxNumFocusAreas() + " focusLock: " + focusLock);

                        focusLock = false;

//                    if(camera.getParameters().getMaxNumFocusAreas() > 0  && !focusLock) {
//
//                        Log.d("focus", "focus inside");
//
//                        focusLock = true;
//
//                        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>(1);
//
//                        int centerX = (int) (qrCode.getCenter().x  - size.width/2) * 2000/size.width;;
//                        int centerY =  (int) (qrCode.getCenter().y  - size.height/2) * 2000/size.height;
//
////                        int left = (int) (qrCode.getTopLeftFP().getTopLeft().x - size.width/2) * 2000/size.width;
////                        int top = (int) (qrCode.getTopLeftFP().getTopLeft().y - size.height/2) * 2000/size.height;
////
////                        Point bottomRight = qrCode.getFourthPoint();
////
////
////
////                        int right = (int) (bottomRight.x - size.width/2) * 2000/size.width;
////                        int bottom = (int) (bottomRight.y - size.height/2) * 2000/size.height;
//
//                        final int OFFSET = (int)((qrCode.getTopRightFP().getCenter().x - qrCode.getTopLeftFP().getCenter().x)/2)* 2000/size.width ;
//
//                        Log.d("focus", "focus getTopRightFP" + qrCode.getTopRightFP().getCenter());
//                        Log.d("focus", "focus qrCode.getTopLeftFP().getCenter()" + qrCode.getTopLeftFP().getCenter());
//                        Log.d("focus", "focus qrCode.getCenter()" + qrCode.getCenter());
//
//                        Log.d("focus", "focus centers: " +  centerX + " | " + centerY + " OFFSET: " + OFFSET);
//
//                        int left = centerX - OFFSET;
//                        int right = centerX + OFFSET;
//                        int top= centerY - OFFSET;
//                        int bottom = centerY + OFFSET;
//
//                        Rect rect = new Rect(left,top, right, bottom);
//                        Camera.Area area = new Camera.Area(rect,1000);
//                        focusAreas.add(area);
//
//                       Log.d("focus", "focus rect: " + rect.toString());
//
//              //          camera.cancelAutoFocus();
//
//
//                        Camera.Parameters parameters = camera.getParameters();
//                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                        parameters.setFocusAreas(focusAreas);
//
//
//                        camera.setParameters(parameters);
//                        camera.autoFocus(new Camera.AutoFocusCallback() {
//                            @Override
//                            public void onAutoFocus(boolean success, Camera camera) {
//
//                                Log.d("focus", "focus onAutoFocus success:" + success);
//
//
//
//                                if (success) {
//
//                                    camera.takePicture(null, null, pictureCallback);
//
////                                    Bitmap bmp = Bitmap.createBitmap(qrCode.getQrCodeMat().cols(), qrCode.getQrCodeMat().rows(), Bitmap.Config.ARGB_8888);
////                                    Utils.matToBitmap(qrCode.getQrCodeMat(), bmp);
////
////                                    imageViewPreview.setImageBitmap(bmp);
//
//
//                                } else
//                                        focusLock = false;
//
//
//
//
//
//
////                                Camera.Parameters parameters = camera.getParameters();
////                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
////                                camera.setParameters(parameters);
//                            }
//                        });
//
//
//                    }


                    }


                }

//                    Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(mRgba, bmp);
//
//                    imageViewPreview.setImageBitmap(bmp);


//                if (finderPattern != null) {
//
//                    Mat finderPatternMat = finderPattern2.getMat();
//
//
//                    MathOpertions mathOpertions = new MathOpertions(finderPattern2.getLeftBottom(), finderPattern2.getLeftTop(), finderPattern2.getRightTop(), 0, 0);
//
//                    double angle = mathOpertions.DegreeMath();
//
//                    double distanceParam = mathOpertions.DistanceMath();
////                        Toast.makeText(getApplicationContext(), "leftTop: " + finderPattern.getLeftTop() + "; rightTop: " + finderPattern.getRightTop() + "; bottomLeft: " + finderPattern.getLeftBottom(), Toast.LENGTH_LONG).show();
//
//                    Log.d("FinderPattern: ", "angle: " + angle + " direction: " + mathOpertions.getDirection() + "distance " + distanceParam);
//
//                    Log.d("Calculation Time", "time : " + (System.currentTimeMillis() - start));
//
//                    Bitmap bmp = Bitmap.createBitmap(finderPatternMat.cols(), finderPatternMat.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(finderPattern2.getMat(), bmp);
//
//                    imageViewPreview.setImageBitmap(bmp);
//
//
//
////                    Bitmap bmp = Bitmap.createBitmap(finderPatternMat.cols(), finderPatternMat.rows(), Bitmap.Config.ARGB_8888);
////                    Utils.matToBitmap(finderPattern.contours, bmp);
////
////                    imageViewPreview.setImageBitmap(bmp);
//
//
////                    QRCode qrCode = new QRCode(finderPattern, mRgba);
////
////
////                    Mat qrMat = qrCode.getQrCodeMat();
////
////                    Bitmap bmp = Bitmap.createBitmap(qrMat.cols(), qrMat.rows(), Bitmap.Config.ARGB_8888);
////                    Utils.matToBitmap(qrMat, bmp);
////
////                    imageViewPreview.setImageBitmap(bmp);
//
//
//
//
////                    if(camera.getParameters().getMaxNumFocusAreas() > 0  && !focusSuccess && false) {
////                        List<Camera.Area>  focusAreas = new ArrayList<Camera.Area>(1);
////                        int left = (int) (finderPattern.getLeftTop().x - size.width/2) * 2000/size.width;
////                        int top = (int) (finderPattern.getLeftTop().y - size.height/2) * 2000/size.height;
////                        Point bottomRight = new Point(finderPattern.getRightTop().x, finderPattern.getLeftBottom().y );
////                        int right = (int) (bottomRight.x - size.width/2) * 2000/size.width;
////                        int bottom = (int) (bottomRight.y - size.height/2) * 2000/size.height;
////                        Rect rect = new Rect(left,top, right, bottom);
////                        Camera.Area area = new Camera.Area(rect,1000);
////                        focusAreas.add(area);
////
////                        Log.d("focus", "focus: " + rect.toString());
////
////                        camera.cancelAutoFocus();
////
////
////                        Camera.Parameters parameters = camera.getParameters();
////                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
////                        parameters.setFocusAreas(focusAreas);
////
////
////                        camera.setParameters(parameters);
////                        camera.autoFocus(new Camera.AutoFocusCallback() {
////                            @Override
////                            public void onAutoFocus(boolean success, Camera camera) {
////                                focusSuccess = success;
////
////                                if (success)
////                                    camera.takePicture(null, null, pictureCallback);
////
////                                Camera.Parameters parameters = camera.getParameters();
////                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
////                                camera.setParameters(parameters);
////                            }
////                        });
////                    }
//
//
//
//
//                }


//            FinderPattern finderPattern = QRCodeFinder.findFinderPattern(bgrMat);
//
//            if(finderPattern != null) {
//                Log.d("Mat", "finderPattern : " + finderPattern.getLeftTop());
//
//            } else
//                Log.d("Mat", "finderPattern : null" );


//            Log.d("onPreviewFrame", "onPreviewFrame time: " + (System.currentTimeMillis() - start));


            }
        }
    };


}
