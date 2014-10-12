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
    private Camera mCamera;

    private Camera.Size previewSize = null;
    private int previewFormat = -1;


    public CameraPreview(Context context, Camera camera) {
        super(context);

        this.mCamera = camera;

        if (previewSize == null)
            this.previewSize = mCamera.getParameters().getSupportedPreviewSizes().get(0);
        if (previewFormat < 0)
            this.previewFormat = 0;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraPreview(Context context, Camera camera, Camera.Size previewSize, int previewFormat) {
        super(context);
        this.mCamera = camera;

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
            //	mCamera.setPreviewCallback(previewCallback);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
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
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mCamera.setDisplayOrientation(90);

        // start preview with new settings
        try {
            //    mCamera.setPreviewCallback(previewCallback);
            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            // parameters.setPictureSize(list.get(0).width,list.get(0).height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(CameraPreview.this.getClass().getName(),
                    "Error starting camera preview: " + e.getMessage());
        }
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }
}
