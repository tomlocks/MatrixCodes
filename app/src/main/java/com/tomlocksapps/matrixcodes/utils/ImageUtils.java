package com.tomlocksapps.matrixcodes.utils;

import android.hardware.Camera;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

/**
 * Created by Tomasz on 2015-03-25.
 */
public class ImageUtils {

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

    public static Point pointMultiplyBy(Point p , double valuePercentX, double valuePercentY) {
        return new Point(p.x * valuePercentX, p.y * valuePercentY);
    }

    public static double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.x - p2.x),2) + Math.pow((p1.y - p2.y),2));
    }

    public static int getMaximumLengthBetweenContourAndPointIndex(MatOfPoint contour, Point p, boolean xAxis, boolean yAxis) {

        Point[] cnt = contour.toArray();
        double maxLength = 0;
        int maxLengthIndex = -1;
        for(int i=0; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength && (xAxis ? p.x < cnt[i].x :  p.x > cnt[i].x) && (yAxis ? p.y < cnt[i].y :  p.y > cnt[i].y)) {
                maxLengthIndex = i;
                maxLength = length;
            }
        }

        return maxLengthIndex;
    }

    public static int getMaximumLengthBetweenContourAndPointIndex(RotatedRect contour, Point p, boolean xAxis, boolean yAxis) {

        Point[] cnt = new Point[4];
        contour.points(cnt);
        double maxLength = 0;
        int maxLengthIndex = -1;
        for(int i=0; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength && (xAxis ? p.x < cnt[i].x :  p.x > cnt[i].x) && (yAxis ? p.y < cnt[i].y :  p.y > cnt[i].y)) {
                maxLengthIndex = i;
                maxLength = length;
            }
        }

        return maxLengthIndex;
    }

    public static Point rotatePoint(Point point1, double RotationDeg) {
        Point point1_temp = new Point();

        point1_temp.x = point1.x*Math.cos(RotationDeg) - point1.y*Math.sin(RotationDeg);
        point1_temp.y = point1.x*Math.sin(RotationDeg) + point1.y*Math.cos(RotationDeg);

        return point1_temp;
    }


    }
