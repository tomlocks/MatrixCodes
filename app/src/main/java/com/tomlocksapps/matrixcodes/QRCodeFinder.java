package com.tomlocksapps.matrixcodes;

import android.util.Log;

import com.tomlocksapps.matrixcodes.model.FinderPattern;
import com.tomlocksapps.matrixcodes.model.QRCode;
import com.tomlocksapps.matrixcodes.utils.CountourAreaComparator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Tomasz on 2015-03-13.
 */
public class QRCodeFinder {

    private static final  int CHILD_COUNT = 5;

    private QRCodeFinder() {
    }

    public static QRCode findFinderPattern(Mat image, boolean debug) {

        Mat imageCanny = Mat.zeros(image.size(), image.type());

        Imgproc.Canny(image, imageCanny, 100, 300);


        List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
        List<MatOfPoint> contoursFinderPattern = new LinkedList<MatOfPoint>();

        Mat hierarchy = new Mat(100, 100, CvType.CV_32SC4);

        Imgproc.findContours(imageCanny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        int[] hier = new int[(int) hierarchy.total() * hierarchy.channels()];

        hierarchy.get(0, 0, hier);


        for (int i = 0; contours.size() > i; i++) {

            int parent = i;
            int child = 0;

            while (hier[parent * 4 + 2] != -1) {
                parent = hier[parent * 4 + 2];
                child = child + 1;
            }

            if (child >= CHILD_COUNT) {
                double areaK = Imgproc.contourArea(contours.get(parent));
                double areaI = Imgproc.contourArea(contours.get(i));

                if (4.5 < areaI / areaK && areaI / areaK < 7.5) {
                    contoursFinderPattern.add(contours.get(i));
                    if (debug)
                        Imgproc.drawContours(image, contours, i, Scalar.all(255), 5);
                }
            }
        }

        Log.d("area", "contoursFinderPattern size " + contoursFinderPattern.size());

        Collections.sort(contoursFinderPattern, new CountourAreaComparator());


        // find the 3 areas with the lowest stdDev
        if (contoursFinderPattern.size() > 3) {
            int position = 0;
            double minStdDev = 1000000;

            for (int i = 0; contoursFinderPattern.size() - 2 > i; i++) {
                double stdDev = 0, mean = 0, area = 0;


                for (int j = i; i + 3 > j; j++) {
                    mean += Imgproc.contourArea(contoursFinderPattern.get(i));
                }

                mean = mean / 3;

                for (int j = i; i + 2 > j; j++) {
                    stdDev += Math.pow((Imgproc.contourArea(contoursFinderPattern.get(j)) - mean), 2);
                }

                stdDev = Math.sqrt(stdDev / 3);

                if (minStdDev > stdDev) {
                    position = i;
                    minStdDev = stdDev;
                }

                contoursFinderPattern = contoursFinderPattern.subList(position, position + 3);

            }
        }

        Log.d("area", "contoursFinderPattern size sublist " + contoursFinderPattern.size());

        // compute mass centers
        List<Point> mc = new ArrayList<Point>();

        for (int i = 0; contoursFinderPattern.size() > i; i++) {

            Moments mu = Imgproc.moments(contoursFinderPattern.get(i));
            mc.add(new Point(mu.get_m10() / mu.get_m00(), mu.get_m01() / mu.get_m00()));

        }



        if (contoursFinderPattern.size() == 3) {

            int rightTopIndex = 0;
            int leftBottomIndex = 0;
            int leftTopIndex = 0;


            Log.d("areaRect", "areaRect angle: ----------- ");

            double maxLength = 0;
            int maxIndex = 0;

            for (int i = 0; mc.size() > i; i++) {
                double length = Math.sqrt(Math.pow((mc.get(i).x - mc.get((i + 1) % 3).x), 2) + Math.pow((mc.get(i).y - mc.get((i + 1) % 3).y), 2));
                if (length > maxLength) {
                    maxLength = length;
                    maxIndex = i;
                }
            }

            Point center = new Point((mc.get(maxIndex).x + mc.get((maxIndex + 1) % 3).x) / 2, (mc.get(maxIndex).y + mc.get((maxIndex + 1) % 3).y) / 2);

            leftTopIndex = (maxIndex + 2) % 3;

            Log.d("TopLeft", "Center:  " + center.toString() + " -- leftTop: " + mc.get(leftTopIndex).toString());


            if (mc.get((maxIndex + 2) % 3).y > center.y) {
                Log.d("TopLeft", "TopLeft :  TOP");
                if ((mc.get(maxIndex).x > mc.get((maxIndex + 1) % 3).x)) {
                    leftBottomIndex = maxIndex;
                    rightTopIndex = (maxIndex + 1) % 3;
                } else {
                    leftBottomIndex = (maxIndex + 1) % 3;
                    rightTopIndex = maxIndex;
                }
            } else if (mc.get((maxIndex + 2) % 3).y < center.y) {
                Log.d("TopLeft", "TopLeft :  BOTTOM");
                if ((mc.get(maxIndex).x < mc.get((maxIndex + 1) % 3).x)) {
                    leftBottomIndex = maxIndex;
                    rightTopIndex = (maxIndex + 1) % 3;
                } else {
                    leftBottomIndex = (maxIndex + 1) % 3;
                    rightTopIndex = maxIndex;
                }
            } else
                return null;


            if(debug )
              Core.line(image, mc.get(maxIndex), mc.get((maxIndex + 1) % 3), new Scalar(100, 100, 100), 5);


            FinderPattern topLeft = new FinderPattern(contoursFinderPattern.get(leftTopIndex), FinderPattern.SquarePosition.TOP_LEFT, center, mc.get(leftTopIndex));
            FinderPattern topRight = new FinderPattern(contoursFinderPattern.get(rightTopIndex), FinderPattern.SquarePosition.TOP_RIGHT, center, mc.get(rightTopIndex));
            FinderPattern bottomLeft = new FinderPattern(contoursFinderPattern.get(leftBottomIndex), FinderPattern.SquarePosition.BOTTOM_LEFT, center, mc.get(leftBottomIndex));


            topLeft.findAdditionalPoints(topRight.getCenter());
            topRight.findAdditionalPoints(topLeft.getCenter());
            bottomLeft.findAdditionalPoints(topLeft.getCenter());

            if (debug) {
                Core.circle(image, topLeft.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
                Core.circle(image, topLeft.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
                Core.circle(image, topLeft.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
                Core.circle(image, topLeft.getTopRight(), 5, new Scalar(0, 255, 0), 7);

                Core.circle(image, topRight.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
                Core.circle(image, topRight.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
                Core.circle(image, topRight.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
                Core.circle(image, topRight.getTopRight(), 5, new Scalar(0, 255, 0), 7);

                Core.circle(image, bottomLeft.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
                Core.circle(image, bottomLeft.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
                Core.circle(image, bottomLeft.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
                Core.circle(image, bottomLeft.getTopRight(), 5, new Scalar(0, 255, 0), 7);

            }


            QRCode qrCode = new QRCode(topLeft, topRight, bottomLeft);

            return qrCode;

        }

        return null;
    }


}
