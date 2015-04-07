package com.tomlocksapps.matrixcodes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.tomlocksapps.matrixcodes.model.FinderPattern;
import com.tomlocksapps.matrixcodes.utils.ImageUtils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Tomasz on 2014-10-12.
 */
public class CameraHelper {

    private CameraPreview cameraPreview;
    private Camera camera;
    private Context context;

    private Camera.PictureCallback pictureCallback;

    private FrameLayout preview;

    private Camera.Parameters cameraParameters;

    private Camera.PreviewCallback previewCallback;

    CameraHelper(Context context, FrameLayout preview, final Camera.PictureCallback pictureCallback, final Camera.PreviewCallback previewCallback) {
        this.context = context;
        this.preview = preview;
        this.pictureCallback = pictureCallback;
        this.previewCallback = previewCallback;

        this.camera = getCameraInstance(); // available in onResume method

        this.cameraParameters = camera.getParameters(); // available in onResume method

        this.cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        camera.setParameters(cameraParameters);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success)
                            camera.takePicture(null, null, pictureCallback);
                    }
                });

            }
        });
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

        if (camera != null) {
            camera.release();
            camera = null;
        }


        // Create an instance of Camera
        camera = getCameraInstance();

        camera.setParameters(cameraParameters);

        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(context, camera, previewCallback);

        preview.addView(cameraPreview);
    }


    protected void onPause() {


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


}
