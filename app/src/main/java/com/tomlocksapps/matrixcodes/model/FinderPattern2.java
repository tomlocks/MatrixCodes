package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

/**
 * Created by Tomasz on 2015-03-13.
 */
public class FinderPattern2 {

    private Point leftTop;
    private Point rightTop;
    private Point leftBottom;

    private Point leftTopBorder;



    public Point getLeftTopBorder() {
        return leftTopBorder;
    }

    public void setLeftTopBorder(Point leftTopBorder) {
        this.leftTopBorder = leftTopBorder;
    }


    public Point[] getRightTopBorder() {
        return rightTopBorder;
    }

    public void setRightTopBorder(Point[] rightTopBorder) {
        this.rightTopBorder = rightTopBorder;
    }

    public Point[] getLeftBottomBorder() {
        return leftBottomBorder;
    }

    public void setLeftBottomBorder(Point[] leftBottomBorder) {
        this.leftBottomBorder = leftBottomBorder;
    }

    private Point[] rightTopBorder;
    private Point[] leftBottomBorder;

    private Mat mat;

    public Mat contours;


    public FinderPattern2(Point leftTop, Point rightTop, Point leftBottom, Point leftTopBorder, Point[] rightTopBorder, Point[] leftBottomBorder) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
        this.leftTopBorder = leftTopBorder;
        this.rightTopBorder = rightTopBorder;
        this.leftBottomBorder = leftBottomBorder;
    }


    public FinderPattern2(Point leftTop, Point rightTop, Point leftBottom) {
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
