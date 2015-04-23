package com.tomlocksapps.matrixcodes.model;

import com.tomlocksapps.matrixcodes.utils.ImageUtils;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.List;

/**
 * Created by Tomasz on 2015-04-21.
 */
public class FinderPattern {



    private PointIndex topLeft;
    private PointIndex topRight;
    private PointIndex bottomLeft;
    private PointIndex bottomRight;
    private Point center;

    private SquarePosition squarePosition;
    private MatOfPoint contour;



    public FinderPattern(MatOfPoint cnt, SquarePosition position, Point qrCodeCenter, Point squareCenter) {
        squarePosition = position;
        contour = cnt;

        center = squareCenter;

        PointIndex[] minMaxPoints = ImageUtils.getMinMaxLengthBetweenContourAndPoint(cnt, qrCodeCenter);

        topLeft = minMaxPoints[1];
        bottomRight = minMaxPoints[0];

    }

    public void findAdditionalPoints(Point otherSquareCenter) {
        List<MatOfPoint> mt = null;

        if(bottomRight.getIndex() < topLeft.getIndex())
            mt = ImageUtils.findSubArrays(contour, bottomRight.getIndex(), topLeft.getIndex());
        else
            mt = ImageUtils.findSubArrays(contour, topLeft.getIndex(), bottomRight.getIndex());

        PointIndex p1 =  ImageUtils.findFurthestPointBetweenPoints(mt.get(0), topLeft, bottomRight);
        PointIndex p2 = ImageUtils.findFurthestPointBetweenPoints(mt.get(1), topLeft, bottomRight);

        double p1dist = ImageUtils.calculateDistance(otherSquareCenter, p1);
        double p2dist = ImageUtils.calculateDistance(otherSquareCenter, p2);

        if(p1dist > p2dist) {
            topRight = p2;
            bottomLeft = p1;
        } else {
            topRight = p1;
            bottomLeft = p2;
        }

        reArrangePoints();

    }

    private void reArrangePoints() {
        PointIndex temp;
        switch (squarePosition) {
            case TOP_LEFT:
                // do nothing, correnct orientation
                break;
            case TOP_RIGHT:
                temp = topLeft;
                topLeft = topRight;
                topRight = temp; // bottomRight; //

                temp = bottomRight;

                bottomRight = bottomLeft;
                bottomLeft =  temp; //

                break;

            case BOTTOM_LEFT:

                temp = topLeft;

                topLeft = topRight;
                topRight = bottomRight;
                bottomRight = bottomLeft;
                bottomLeft = temp;

                break;
        }
    }


    public enum SquarePosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT
    }

    public PointIndex getTopLeft() {
        return topLeft;
    }

    public PointIndex getTopRight() {
        return topRight;
    }

    public PointIndex getBottomLeft() {
        return bottomLeft;
    }

    public PointIndex getBottomRight() {
        return bottomRight;
    }

    public Point getCenter() {
        return center;
    }

    public SquarePosition getSquarePosition() {
        return squarePosition;
    }

    public MatOfPoint getContour() {
        return contour;
    }

}
