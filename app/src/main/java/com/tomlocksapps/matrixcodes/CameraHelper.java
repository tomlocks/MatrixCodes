package com.tomlocksapps.matrixcodes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by Tomasz on 2014-10-12.
 */
public class CameraHelper {

    private CameraPreview cameraPreview;
    private Camera camera;
    private Context context;

    private Camera.PictureCallback pictureCallback;

    private FrameLayout preview;

    // supported Sizes
    private Camera.Size pictureSize;
    private Camera.Size previewSize;

    private int pictureFormat;
    private int previewFormat;


    CameraHelper(Context context, FrameLayout preview, final Camera.PictureCallback pictureCallback) {
        this.context = context;
        this.preview = preview;
        this.pictureCallback = pictureCallback;
        this.camera = getCameraInstance();

        Camera.Parameters params =  camera.getParameters();

        this.pictureSize = params.getPictureSize();
        this.pictureFormat = params.getPictureFormat();

        this.pictureSize = params.getPreviewSize();
        this.previewFormat = params.getPreviewFormat();

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null,null,pictureCallback);
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

        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(context, camera, pictureSize, pictureFormat);

        // List<Integer> formats =
        // camera.getParameters().getSupportedPreviewFormats();

        preview.addView(cameraPreview);
    }


    protected void onPause() {

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            preview.removeView(cameraPreview);
            camera = null;
        }

    }

    public Camera.Size getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(Camera.Size pictureSize) {
        this.pictureSize = pictureSize;
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }

    public int getPictureFormat() {
        return pictureFormat;
    }

    public void setPictureFormat(int pictureFormat) {
        this.pictureFormat = pictureFormat;
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }
}
