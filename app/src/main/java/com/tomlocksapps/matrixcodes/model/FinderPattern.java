package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

/**
 * Created by Tomasz on 2015-03-13.
 */
public class FinderPattern {

    private Point leftTop;
    private Point rightTop;
    private Point leftBottom;

    private Mat mat;


    public FinderPattern(Point leftTop, Point rightTop, Point leftBottom) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
    }

    public Point getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(Point leftTop) {
        this.leftTop = leftTop;
    }

    public Point getRightTop() {
        return rightTop;
    }

    public void setRightTop(Point rightTop) {
        this.rightTop = rightTop;
    }

    public Point getLeftBottom() {
        return leftBottom;
    }

    public void setLeftBottom(Point leftBottom) {
        this.leftBottom = leftBottom;
    }


    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }


}
