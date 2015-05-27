package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.qrcode.encoder.QRCode;
import com.tomlocksapps.matrixcodes.model.QRCodeContent;
import com.tomlocksapps.matrixcodes.utils.Log;
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
                    Log.d("OpenCV loaded successfully", Log.LogType.OPENCV, this);

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

//        Log.d("size", "pictureSize: " + pictureSize.height + " | " + pictureSize.width + " previewSize: " + previewSize.height + " | " + previewSize.width);

        Log.d("MODEL: "+ Build.MODEL, Log.LogType.CAMERA, this);

        Log.d("pictureSize: " + pictureSize.height + " | " + pictureSize.width + " previewSize: " + previewSize.height + " | " + previewSize.width, Log.LogType.CAMERA, this);

        cameraHelper.setPictureSize(pictureSize);
        cameraHelper.setPreviewSize(previewSize);

        imageViewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                i.putExtra(MapActivity.BUNDLE_GLOBAL_X, 100);
                i.putExtra(MapActivity.BUNDLE_GLOBAL_Y, 150);
                startActivity(i);
            }
        });


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
