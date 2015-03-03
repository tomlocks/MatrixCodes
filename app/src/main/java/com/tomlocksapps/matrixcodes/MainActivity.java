package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


public class MainActivity extends Activity {


    private FrameLayout preview;
    private CameraHelper cameraHelper;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {

            if (((MatrixCodesApplication) getApplication()).isOpenCVLoaded()) {
                // TODO add code for finding, recognizing and calculating distance.
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

        // TODO - check the length of supported picture sizes!
        cameraHelper.setPictureSize(cameraHelper.getParameters().getSupportedPictureSizes().get(5));

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
