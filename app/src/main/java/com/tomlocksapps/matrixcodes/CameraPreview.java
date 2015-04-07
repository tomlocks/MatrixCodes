package com.tomlocksapps.matrixcodes;

/**
 * Created by Tomasz on 2014-10-12.
 */


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera camera;

    private Camera.Size previewSize;
    private Integer previewFormat;
    private  Camera.PreviewCallback previewCallback;

    public CameraPreview(Context context, Camera camera, final Camera.PreviewCallback previewCallback) {
        super(context);
        this.camera = camera;

        Camera.Parameters params = camera.getParameters();

        this.previewSize = params.getPreviewSize();
        this.previewFormat = params.getPreviewFormat();
        this.previewCallback = previewCallback;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraPreview(Context context, Camera camera, Camera.Size previewSize, int previewFormat) {
        super(context);
        this.camera = camera;

        this.previewSize = previewSize;
        this.previewFormat = previewFormat;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public CameraPreview(Context context) {
        super(context);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            camera.setPreviewCallback(previewCallback);

            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(CameraPreview.this.getClass().getName(),
                    "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
          camera.setDisplayOrientation(90);

        // start preview with new settings
        try {


            camera.setPreviewCallback(previewCallback);
            camera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);

            camera.setParameters(parameters);
            camera.startPreview();

        } catch (Exception e) {
            Log.d(CameraPreview.this.getClass().getName(),
                    "Error starting camera preview: " + e.getMessage());
        }
    }


}
