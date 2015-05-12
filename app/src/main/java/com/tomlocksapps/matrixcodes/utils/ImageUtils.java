package com.tomlocksapps.matrixcodes.utils;

import android.hardware.Camera;
import android.util.Log;

import com.tomlocksapps.matrixcodes.model.PointIndex;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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




    public static int[] getMinMaxLengthBetweenContourAndPointIndex(MatOfPoint contour, Point p) {

        int[] indexes = new int[4];

        Point[] cnt = contour.toArray();
        double maxLength = ImageUtils.calculateDistance(p, cnt[0]);
        int maxLengthIndex = 0;
        double minLength = ImageUtils.calculateDistance(p, cnt[0]);
        int minLengthIndex = 0;
        for(int i=1; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength) {
                maxLengthIndex = i;
                maxLength = length;
            } else if(length < minLength) {
                minLengthIndex = i;
                minLength = length;
            }
        }

        indexes[0] = minLengthIndex;
        indexes[1] = maxLengthIndex;

        return indexes;
    }

    public static PointIndex[] getMinMaxLengthBetweenContourAndPoint(MatOfPoint contour, Point p) {

        PointIndex[] points = new PointIndex[2]; // 0 - closest , 1 - furthest

        Point[] cnt = contour.toArray();
        double maxLength = ImageUtils.calculateDistance(p, cnt[0]);
        int maxLengthIndex = 0;
        double minLength = ImageUtils.calculateDistance(p, cnt[0]);
        int minLengthIndex = 0;
        for(int i=1; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength) {
                maxLengthIndex = i;
                maxLength = length;
            } else if(length < minLength) {
                minLengthIndex = i;
                minLength = length;
            }
        }



        points[0] = new PointIndex(cnt[minLengthIndex], minLengthIndex);
        points[1] = new PointIndex(cnt[maxLengthIndex], maxLengthIndex);

        return points;
    }

    public static PointIndex getMaximumLengthBetweenContourAndPoint(MatOfPoint contour, Point p) {


        Point[] cnt = contour.toArray();
        double maxLength = 0;
        int maxLengthIndex = -1;
        for(int i=0; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength) {
                maxLengthIndex = i;
                maxLength = length;
            }
        }

        PointIndex point = new PointIndex(cnt[maxLengthIndex], maxLengthIndex);

        return point;
    }

    public static int getMaximumLengthBetweenContourAndPointIndex(MatOfPoint contour, Point p) {

        Point[] cnt = contour.toArray();
        double maxLength = 0;
        int maxLengthIndex = -1;
        for(int i=0; cnt.length > i ; i++) {
            double length = ImageUtils.calculateDistance(p, cnt[i]);
            if(length > maxLength) {
                maxLengthIndex = i;
                maxLength = length;
            }
        }

        return maxLengthIndex;
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

        p.x = p.x + Math.sin(Math.toRadians(contour.angle))*1/2*contour.size.height;

        p.y = p.y + Math.sin(Math.toRadians(contour.angle))*1/2*contour.size.height;
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

    public static  List<MatOfPoint> findSubArrays(MatOfPoint contour, int from, int to) {
        Point[] contourPoints = contour.toArray();

        List<Point> subList1 = new ArrayList<Point>(contourPoints.length/2);
        List<Point> subList2 = new ArrayList<Point>(contourPoints.length/2);

        int offset = 0; //contourPoints.length/10;

        for(int i=0; i < contourPoints.length; i++) {
            if(from + offset < i && i < to - offset) {
                subList1.add(contourPoints[i]);
            }
            else if(from - offset > i || i > to + offset) {
                subList2.add(contourPoints[i]);
            }

//            subList1.add(contourPoints[i]);
        }

        List<MatOfPoint> matOfPointList = new ArrayList<MatOfPoint>(2);

        MatOfPoint matOfPoint1 = new MatOfPoint();
        matOfPoint1.fromList(subList1);

        MatOfPoint matOfPoint2 = new MatOfPoint();
        matOfPoint2.fromList(subList2);

        matOfPointList.add(matOfPoint1);
        matOfPointList.add(matOfPoint2);


        return matOfPointList;

        //return subList1.toArray(new Point[subList1.size()]);

    }

    public static MatOfPoint findEdges2(MatOfPoint contour, int from, int to) {
        Point[] contourPoints = contour.toArray();
        List<Point> points = new ArrayList<Point>(4);

        List<Point> subList1 = new ArrayList<Point>(contourPoints.length/2);
        List<Point> subList2 = new ArrayList<Point>(contourPoints.length/2);

        int offset = contourPoints.length/10;

        for(int i=0; i < contourPoints.length; i++) {
            if(from - offset < i && i < to + offset) {
                subList1.add(contourPoints[i]);
            } else if(from + offset > i && i > to - offset) {
                subList2.add(contourPoints[i]);
            }
        }

        MatOfPoint matOfPoint = new MatOfPoint();

        matOfPoint.fromList(subList1);

        return matOfPoint;

        //return subList1.toArray(new Point[subList1.size()]);

    }

    public static Point[] findEdges(MatOfPoint contour) {
        Point[] contourPoints = contour.toArray();
        List<Point> points = new ArrayList<Point>(4);

        Rect rect = Imgproc.boundingRect(contour);

        for(int i=0; i < contourPoints.length; i++) {
            Point p = contourPoints[i];
            if(rect.x == p.x) {
                points.add(p);
            }

                if(rect.y == p.y){
                points.add(p);
            }

            if(rect.x + rect.width-1 == p.x) {
                points.add(p);
            }
            if(rect.y + rect.height-1 == p.y) {
                points.add(p);
            }
        }



        Log.d("Matrix", "points: " + points.toString());

        return points.toArray(new Point[points.size()]);
    }

    public static Point[] getThreeEdges(MatOfPoint contour, Point center, Point previous) {
        Point[] points = new Point[3]; // 0 - max, 2 - min,, 1

        Point[] contourPoints = contour.toArray();

        Point[] minMaxPoints = getMinMaxLengthBetweenContourAndPoint(contour, center);

        double maxSum = 0;
        int maxSumIndex = 0;

        for(int i = 0; i < contourPoints.length ; i++) {
            double lenght1 =  ImageUtils.calculateDistance(previous, contourPoints[i]);
            double lenght2 =  ImageUtils.calculateDistance(minMaxPoints[0], contourPoints[i]);
            double lenght3 =  ImageUtils.calculateDistance(minMaxPoints[1], contourPoints[i]);

            double sum = lenght1 + lenght2 + lenght3;
            if(sum > maxSum) {
                maxSum = sum;
                maxSumIndex = i;
            }
        }

        points[0] = minMaxPoints[1];
        points[1] = contourPoints[maxSumIndex];
        points[2] = minMaxPoints[0];

        return points;
    }


    public static PointIndex findFurthestPointBetweenPoints(MatOfPoint contour, PointIndex p1, PointIndex p2) {

        Point[] contourPoints = contour.toArray();



        double maxSum = 0;
        int maxSumIndex = 0;

        for(int i = 0; i < contourPoints.length ; i++) {
            double lenght1 =  ImageUtils.calculateDistance(p1, contourPoints[i]);
            double lenght2 =  ImageUtils.calculateDistance(p2, contourPoints[i]);

            double sum = lenght1 + lenght2;
            if(sum > maxSum) {
                maxSum = sum;
                maxSumIndex = i;
            }
        }

        PointIndex p = new PointIndex(contourPoints[maxSumIndex], maxSumIndex);

        return p;
    }



//    public static Point[] getFourEdges(MatOfPoint contour, Point center) {
//        Point[] points = new Point[4];
//
//        Point maxPoint = ImageUtils.getMaximumLengthBetweenContourAndPoint(contour, center);
//        Point minPoint = ImageUtils.getMaximumLengthBetweenContourAndPoint(contour, center);
//
//
//
//        return points;
//    }


    public static Point calculateFourthPoint(Point P1, Point P2, Point P3, Point P4)
    {
        Point fourthPoint = new Point();
        double L, M;



        L=      P1.y*(P4.x-P3.x)*(P2.x-P1.x)
                -P3.y*(P4.x-P3.x)*(P2.x-P1.x)
                +P2.x*P3.x*P4.y
                -P2.x*P3.x*P3.y
                -P1.x*P3.x*P4.y
                +P1.x*P3.x*P3.y
                -P1.x*P4.x*P2.y
                +P1.x*P4.x*P1.y
                +P1.x*P3.x*P2.y
                -P1.x*P3.x*P1.y;
        M=P2.x*P4.y-P2.x*P3.y-P1.x*P4.y+P1.x*P3.y-P4.x*P2.y+P4.x*P1.y+P3.x*P2.y-P3.x*P1.y;
        fourthPoint.x=L/M;
        fourthPoint.y=(((P2.y-P1.y)*(fourthPoint.x-P1.x))/(P2.x-P1.x))+P1.y;

        if(Math.abs(M) < 0.5)
            return null;

        return fourthPoint;

    }


    public static Point rotatePoint(Point p ,int degrees) {
        //x' = xcos(alpha) - ysin(alpha)
        //y' = xsin(alpha) + ycos(alpha)

        int x = (int)(p.x*Math.cos(Math.toRadians(degrees)) - p.y*Math.sin(Math.toRadians(degrees)));
        int y = (int)(p.x*Math.sin(Math.toRadians(degrees)) + p.y*Math.cos(Math.toRadians(degrees)));

        return new Point(x,y);

    }

    }
