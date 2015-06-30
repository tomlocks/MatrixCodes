package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;


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

        Camera.Size pictureSize = cameraHelper.getParameters().getSupportedPictureSizes().get(cameraHelper.getParameters().getSupportedPictureSizes().size() - 2);

        Camera.Size previewSize = cameraHelper.getParameters().getSupportedPreviewSizes().get(0);

        Log.d("MODEL: "+ Build.MODEL, Log.LogType.CAMERA, this);

        Log.d("pictureSize: " + pictureSize.height + " | " + pictureSize.width + " previewSize: " + previewSize.height + " | " + previewSize.width, Log.LogType.CAMERA, this);

        for(Camera.Size size :cameraHelper.getParameters().getSupportedPreviewSizes()) {
            Log.d("supportedPreviewSize: " +  size.width + "|" + size.height, Log.LogType.CAMERA, this);
        }


        cameraHelper.setPictureSize(pictureSize);
        cameraHelper.setPreviewSize(previewSize);



    }

    @Override
    protected  void onStart() {
        super.onStart();

        com.tomlocksapps.matrixcodes.utils.Log.d("onStart", com.tomlocksapps.matrixcodes.utils.Log.LogType.LIFECYCLE, this);

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

        cameraHelper.onPause();

    }


    @Override
    public void onImagePreview(Bitmap bmp) {
        imageViewPreview.setVisibility(View.VISIBLE);
        imageViewPreview.setImageBitmap(bmp);
    }

    @Override
    public void onImageVisiblityChange(int state) {
        imageViewPreview.setVisibility(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_resolution_change) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            List<Camera.Size> previewSizes = cameraHelper.getParameters().getSupportedPreviewSizes();

            List<CharSequence> resolutionLabels = new ArrayList<CharSequence>(previewSizes.size());

                    for(Camera.Size size : previewSizes) {
                        String label = size.height+"x"+size.width;
                        resolutionLabels.add(label);
                    }

            CharSequence[] resolutionLabelsArray = resolutionLabels.toArray(new CharSequence[resolutionLabels.size()]);

            builder.setItems(resolutionLabelsArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Camera.Size previewSize = cameraHelper.getParameters().getSupportedPreviewSizes().get(which);
                    cameraHelper.setPreviewSize(previewSize);

                    Log.d("AppResults: ; ---------- New Resolution:  "+previewSize.width+ "x" + previewSize.height+" -----------------------", Log.LogType.OTHER, this);
                    Log.d("CameraModel: ; ---------- New Resolution:  "+previewSize.width+ "x" + previewSize.height+" -----------------------", Log.LogType.OTHER, this);
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRestoreInstanceState(Bundle icicle) {
        super.onRestoreInstanceState(icicle);

        cameraHelper.onRestoreInstanceState(icicle);
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);

        cameraHelper.onSaveInstanceState(icicle);
    }

}
