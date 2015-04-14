package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.tomlocksapps.matrixcodes.model.FinderPattern;
import com.tomlocksapps.matrixcodes.model.QRCode;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {


    private FrameLayout preview;
    private CameraHelper cameraHelper;
    private ImageView imageViewPreview;
    private boolean focusSuccess = false;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {

            if (((MatrixCodesApplication) getApplication()).isOpenCVLoaded()) {
                // TODO add code for finding, recognizing and calculating distance.

                Toast.makeText(getApplicationContext(), "picture taken", Toast.LENGTH_SHORT).show();

            }

            camera.startPreview();
        }
    };


    private  Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (((MatrixCodesApplication) getApplication()).isOpenCVLoaded()) {



                Camera.Size size = camera.getParameters().getPreviewSize();

                long start = System.currentTimeMillis();

                Mat mYuv = new Mat(size.height + size.height / 2, size.width, CvType.CV_8UC1);
                mYuv.put(0, 0, data);
                Mat mRgba = new Mat();

                Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);

                Mat bgrMat = Highgui.imdecode(mRgba, Highgui.IMREAD_GRAYSCALE);

                Log.d("Mat", "bgrMat : " + bgrMat.type());


                Bitmap map = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(mRgba, map);

                //      imageViewPreview.setImageBitmap(map);

                FinderPattern finderPattern = QRCodeFinder.findFinderPattern(mRgba);

                if (finderPattern != null) {

                    Mat finderPatternMat = finderPattern.getMat();


                    MathOpertions mathOpertions = new MathOpertions(finderPattern.getLeftBottom(), finderPattern.getLeftTop(), finderPattern.getRightTop(), 0, 0);

                    double angle = mathOpertions.DegreeMath();

                    double distanceParam = mathOpertions.DistanceMath();
//                        Toast.makeText(getApplicationContext(), "leftTop: " + finderPattern.getLeftTop() + "; rightTop: " + finderPattern.getRightTop() + "; bottomLeft: " + finderPattern.getLeftBottom(), Toast.LENGTH_LONG).show();

                    Log.d("FinderPattern: ", "angle: " + angle + " direction: " + mathOpertions.getDirection() + "distance " + distanceParam);

                    Log.d("Calculation Time", "time : " + (System.currentTimeMillis() - start));



                    Bitmap bmp = Bitmap.createBitmap(finderPatternMat.cols(), finderPatternMat.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(finderPatternMat, bmp);

                    imageViewPreview.setImageBitmap(bmp);
          //          imageViewPreview.setRotation(90);

//                    QRCode qrCode = new QRCode(finderPattern, mRgba);


//                    Mat qrMat = qrCode.getQrCodeMat();
//
//                    Bitmap bmp = Bitmap.createBitmap(qrMat.cols(), qrMat.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(qrMat, bmp);
//
//                    imageViewPreview.setImageBitmap(bmp);




//                    if(camera.getParameters().getMaxNumFocusAreas() > 0  && !focusSuccess && false) {
//                        List<Camera.Area>  focusAreas = new ArrayList<Camera.Area>(1);
//                        int left = (int) (finderPattern.getLeftTop().x - size.width/2) * 2000/size.width;
//                        int top = (int) (finderPattern.getLeftTop().y - size.height/2) * 2000/size.height;
//                        Point bottomRight = new Point(finderPattern.getRightTop().x, finderPattern.getLeftBottom().y );
//                        int right = (int) (bottomRight.x - size.width/2) * 2000/size.width;
//                        int bottom = (int) (bottomRight.y - size.height/2) * 2000/size.height;
//                        Rect rect = new Rect(left,top, right, bottom);
//                        Camera.Area area = new Camera.Area(rect,1000);
//                        focusAreas.add(area);
//
//                        Log.d("focus", "focus: " + rect.toString());
//
//                        camera.cancelAutoFocus();
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
//                                focusSuccess = success;
//
//                                if (success)
//                                    camera.takePicture(null, null, pictureCallback);
//
//                                Camera.Parameters parameters = camera.getParameters();
//                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//                                camera.setParameters(parameters);
//                            }
//                        });
//                    }




                } else {
                    //           Toast.makeText(getApplicationContext(), " Not Found " , Toast.LENGTH_LONG).show();

                }


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



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("testTEST", "OpenCV loaded successfully");

                    ((MatrixCodesApplication) getApplication()).setOpenCVLoaded(true);

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!CameraHelper.checkCameraHardware((Context) this))
            finish();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        preview = (FrameLayout) findViewById(R.id.camera_preview);

        cameraHelper = new CameraHelper(this, preview, pictureCallback, previewCallback);

        imageViewPreview = (ImageView) findViewById(R.id.imageViewPreview);

        // TODO - check the length of supported picture sizes!
       // cameraHelper.setPictureSize(cameraHelper.getParameters().getSupportedPictureSizes().get(8));
        cameraHelper.setPreviewSize(cameraHelper.getParameters().getSupportedPreviewSizes().get(1));
    }



    @Override
    protected void onResume() {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mLoaderCallback);

        cameraHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // get rid of the "method called after release exception"

        cameraHelper.onPause();
        
    }


}
