package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tomlocksapps.matrixcodes.view.DrawView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


public class MainActivity extends Activity implements CameraHelper.OnImagePreviewListener {


    private FrameLayout preview;
    private CameraHelper cameraHelper;
    private ImageView imageViewPreview;
    private DrawView drawView;


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

        com.tomlocksapps.matrixcodes.utils.Log.d("onCreate", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        if (!CameraHelper.checkCameraHardware((Context) this))
            finish();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        preview = (FrameLayout) findViewById(R.id.camera_preview);
        drawView = (DrawView) findViewById(R.id.draw_view);

        cameraHelper = new CameraHelper(this, preview,drawView);

        cameraHelper.setOnImagePreviewListener(this);

        imageViewPreview = (ImageView) findViewById(R.id.imageViewPreview);

        // TODO - check the length of supported picture sizes!
        //     cameraHelper.setPictureSize(cameraHelper.getParameters().getSupportedPictureSizes().get(cameraHelper.getParameters().getSupportedPictureSizes().size()-3));
        Camera.Size pictureSize = cameraHelper.getParameters().getSupportedPictureSizes().get(cameraHelper.getParameters().getSupportedPictureSizes().size() - 2);

        Camera.Size previewSize = cameraHelper.getParameters().getSupportedPreviewSizes().get(cameraHelper.getParameters().getSupportedPreviewSizes().size() - 4);
//        Camera.Size previewSize = cameraHelper.getParameters().getSupportedPreviewSizes().get(0);

        Log.d("size", "pictureSize: " + pictureSize.height + " | " + pictureSize.width + " previewSize: " + previewSize.height + " | " + previewSize.width);


        cameraHelper.setPictureSize(pictureSize);
        cameraHelper.setPreviewSize(previewSize); // 3
    }


    @Override
    protected void onResume() {
        super.onResume();

        com.tomlocksapps.matrixcodes.utils.Log.d("onResume", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mLoaderCallback);

        cameraHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        com.tomlocksapps.matrixcodes.utils.Log.d("onPause", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

        // get rid of the "method called after release exception"

        cameraHelper.onPause();

    }


    @Override
    public void onImagePreview(Bitmap bmp) {
        imageViewPreview.setImageBitmap(bmp);
    }
}
