package com.tomlocksapps.matrixcodes.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tomasz on 2015-06-08.
 */
public class FileUtils {

//    public static boolean saveBmpToFile(byte[] data, String fileName, Camera.Parameters parameters) {
//        try {
//            Camera.Size size = parameters.getPreviewSize();
//            YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
//                    size.width, size.height, null);
//            File file = new File(Environment.getExternalStorageDirectory()
//                    .getPath() + "/out.jpg");
//            FileOutputStream filecon = new FileOutputStream(file);
//            image.
//                    new Rect(0, 0, image.getWidth(), image.getHeight()), 90,
//                    filecon);
//
//
//        } catch (FileNotFoundException e) {
//
//        }
//        return false;
//    }

    public static boolean saveBmpToFile(Bitmap bmp, String fileName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        ////you can create a new file name "test.jpg" in sdcard folder.
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//write the bytes in file
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fo.write(bytes.toByteArray());
            fo.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}