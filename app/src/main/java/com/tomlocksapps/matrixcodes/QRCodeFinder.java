package com.tomlocksapps.matrixcodes;

import android.util.Log;

import com.tomlocksapps.matrixcodes.model.FinderPattern;
import com.tomlocksapps.matrixcodes.utils.CountourAreaComparator;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


/**
 * Created by Tomasz on 2015-03-13.
 */
public class QRCodeFinder {


    private QRCodeFinder() {
    }



//    public static QRCodeFinder newInstance() {
//        QRCodeFinder qr = new QRCodeFinder();
//
//        return qr;
//    }

    public static FinderPattern findFinderPattern(Mat image) {


        Mat imageCanny = Mat.zeros(image.size(), image.type());

        Imgproc.Canny(image, imageCanny, 100, 300);

        List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
        List<MatOfPoint> contoursFinderPattern = new LinkedList<MatOfPoint>();

        Mat hierarchy = new Mat(100,100, CvType.CV_32SC4);

        Imgproc.findContours(imageCanny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contoursDrawing = Mat.zeros(image.size(), CvType.CV_8UC1);


        int[] hier = new int[(int) hierarchy.total()* hierarchy.channels() ] ;

         hierarchy.get(0,0,hier);

        for(int i =0 ; contours.size() > i ; i++) {
            int secondParent=-1, thirdParent = -1, fourhParent = -1;;

            int firstParent = hier[i*4 + 3];
            if(firstParent!=-1)
                secondParent = hier[firstParent*4 + 3];
            if(secondParent!=-1)
                thirdParent= hier[secondParent*4  + 3];
            if(fourhParent!=-1)
                fourhParent= hier[thirdParent*4  + 3];

            if(fourhParent != -1)
                Log.d("contoursFinderPattern", "fourhParent != -1");

            if (firstParent != -1 && secondParent != -1 && thirdParent != -1 ) {

                double area1 = Imgproc.contourArea(contours.get(thirdParent));
                double area2 = Imgproc.contourArea(contours.get(firstParent));


                double ratio = area1 / area2;

                if (2.5 < ratio && ratio < 4.5)
                {
                    Log.d("contoursFinderPattern", "ratio: " + ratio);

                    contoursFinderPattern.add(contours.get(thirdParent));
                }
            }

        }



        Collections.sort(contoursFinderPattern, new CountourAreaComparator());



        // find the 3 areas with the lowest stdDev
        if(contoursFinderPattern.size() > 3) {
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

                contoursFinderPattern =  contoursFinderPattern.subList(position, position +2);

            }
        }

        // compute mass centers
        List<Point> mc = new ArrayList<Point>();

        for(int i =0 ; contoursFinderPattern.size() > i ; i++) {

            Moments mu = Imgproc.moments(contoursFinderPattern.get(i));
            mc.add(new Point(mu.get_m10() / mu.get_m00(), mu.get_m01() / mu.get_m00()));

            Imgproc.drawContours(contoursDrawing, contoursFinderPattern, i, Scalar.all(100));
        }

        FinderPattern finderPattern = null;

        if(contoursFinderPattern.size() == 3) {

           int rightTopIndex = 0;
           int leftBottomIndex = 0;
           int leftTopIndex = 0;


            for(int i =0 ; mc.size() > i ; i++) {
                if(mc.get(i).x > rightTopIndex)
                    rightTopIndex = i;
                if(mc.get(i).y > leftBottomIndex)
                    leftBottomIndex = i;
            }

            for(int i =0 ; mc.size() > i ; i++) {
                if(i != rightTopIndex && i != leftBottomIndex)
                    leftTopIndex = i;
            }

            finderPattern = new FinderPattern(mc.get(leftTopIndex),mc.get(rightTopIndex), mc.get(leftBottomIndex));
            finderPattern.setMat(contoursDrawing);
        }

        Log.d("contoursFinderPattern", "size: " + contoursFinderPattern.size());

        return finderPattern;
    }


}
