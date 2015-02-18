package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Map;


public class MainActivity extends Activity {


    private FrameLayout preview;
    private CameraHelper cameraHelper;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {



                    Camera.Size size = camera.getParameters().getPictureSize();

                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                 //   BitmapFactory.

            // test test

                    int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];



                 Mat mat = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC3);

                mat.size();

            //Mat mat;

            org.opencv.android.Utils.bitmapToMat(bmp, mat);

            //         Mat mat = Mat.zeros(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC3);

               //     mat.put(0,0, pixels);

                    bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

                    String result = decode(pixels, size, null);

                    if (result != null && !result.equals("")) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }




            camera.startPreview();
        }
    };

    public static int YUV_NV21_TO_RGB(int[] argb, byte[] yuv, Camera.Size size) {

        final int width = size.width;
        final int height = size.height;

        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) yuv[ci * width + cj]));
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
        return a;
    }

    public static String decode(int[] pixels, Camera.Size size, Map<DecodeHintType, Object> hints) {


        LuminanceSource source = new RGBLuminanceSource(size.width, size.height, pixels);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader barcodeReader = new MultiFormatReader();
        Result result = null;



        String finalResult = "";
        try {
            if (hints != null && !hints.isEmpty())
                result = barcodeReader.decode(bitmap, hints);
            else
                result = barcodeReader.decode(bitmap);
            // setting results.
            finalResult = String.valueOf(result.getText());
        } catch (Exception e) {
            e.printStackTrace();
            //throw new BarcodeEngine().new BarcodeEngineException(e.getMessage());
        }


        return finalResult;
        //	 return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!CameraHelper.checkCameraHardware((Context) this))
            finish();

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        cameraHelper = new CameraHelper(this, preview, pictureCallback);

        cameraHelper.setPictureSize(cameraHelper.getParameters().getSupportedPictureSizes().get(5));

    }


    // will go to ZXing class

    //  public static int my_YUV_TO_

    @Override
    protected void onResume() {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);

        cameraHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        cameraHelper.onPause();
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("testTEST", "OpenCV loaded successfully");
                    //    mOpenCvCameraView.enableView();



                    Mat mat;

                    mat = Mat.eye(3,3,0);

                    mat = Mat.eye(3,3,1);

                    Toast.makeText(getApplicationContext(), String.valueOf(mat.rows()), Toast.LENGTH_LONG).show();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


}
