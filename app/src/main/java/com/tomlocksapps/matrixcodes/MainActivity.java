package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.tomlocksapps.matrixcodes.model.FinderPattern;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class MainActivity extends Activity {


    private FrameLayout preview;
    private CameraHelper cameraHelper;
    private ImageView imageViewPreview;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {

            if (((MatrixCodesApplication) getApplication()).isOpenCVLoaded()) {
                // TODO add code for finding, recognizing and calculating distance.

                int format = camera.getParameters().getPictureFormat();

                Camera.Size size = camera.getParameters().getPictureSize();



                if(format == ImageFormat.JPEG) {
                    Mat m = new Mat( 1 , data.length , CvType.CV_8UC1);
                    m.put(0,0,data);
                    Log.d("Mat", "rows: " + m.rows() + " ;cols: " + m.cols());
                    Mat bgrMat = new Mat();
                    bgrMat = Highgui.imdecode(m, Highgui.IMREAD_GRAYSCALE);
                    Log.d("bgrMat", "rows: " + bgrMat.rows() + " ;cols: " + bgrMat.cols() + "; type: " + bgrMat.type());

                    long start = System.currentTimeMillis();

                    FinderPattern finderPattern = QRCodeFinder.findFinderPattern(bgrMat);

                    Log.d("start: ",   System.currentTimeMillis()- start + "");

                    if(finderPattern != null) {

                    Mat finderPatternMat = finderPattern.getMat();

                        Bitmap bmp = Bitmap.createBitmap(finderPatternMat.cols(), finderPatternMat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(finderPatternMat, bmp);


                       imageViewPreview.setImageBitmap(bmp);

          //              Log.d("FinderPattern: ", "angle: " + angle + " leftTop: " + finderPattern.getLeftTop() + "; rightTop: " + finderPattern.getRightTop() + "; bottomLeft: " + finderPattern.getLeftBottom());

                    } else {
                        Toast.makeText(getApplicationContext(), " Not Found " , Toast.LENGTH_LONG).show();

                    }

                }



            }

            camera.startPreview();
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

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        cameraHelper = new CameraHelper(this, preview, pictureCallback);

        imageViewPreview = (ImageView) findViewById(R.id.imageViewPreview);

        // TODO - check the length of supported picture sizes!
        cameraHelper.setPictureSize(cameraHelper.getParameters().getSupportedPictureSizes().get(8));

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

        cameraHelper.onPause();
    }


}
