package com.tomlocksapps.matrixcodes.utils;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;

/**
 * Created by Tomasz on 2015-03-18.
 */

public class CountourAreaComparator implements Comparator<MatOfPoint> {


    @Override
    public int compare(MatOfPoint lhs, MatOfPoint rhs) {
        return Double.compare(Imgproc.contourArea(lhs), Imgproc.contourArea(rhs));
    }
}
