package com.tomlocksapps.matrixcodes;


import com.tomlocksapps.matrixcodes.model.QRCode;

import org.opencv.core.Point;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

/**
 * Created by AveAmore on 2015-03-23.
 */
public class MathOpertions {

//    boolean direction; //true - horizontal , false vertical

    private Point leftBottom;
    private Point leftTop;
    private Point rightTop;
    private double scale;
    private double cameraFactor;
    private QRCode.UserPosition userPosition;
    private double distanceToCode;
    private double codeAngle;

    public MathOpertions(Point leftBottom, Point leftTop, Point rightTop, double scale, double cameraFactor, QRCode.UserPosition userPosition) {
        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.scale = scale / 20;
        this.cameraFactor = cameraFactor;
        this.userPosition = userPosition;
        this.distanceToCode = distanceMath();
        this.codeAngle = degreeMath();
      //  this.direction = getDirection();
    }

//    private boolean getDirection() {
//        Point vect1 = new Point();
//        Point vect2 = new Point();
//        double vertical, horizontal;
//        double DegreeParam, DegreeHelper;
//
//
//        vect1.x = leftBottom.x - leftTop.x;
//        vect1.y = ((leftBottom.y - leftTop.y));
//        vect2.x = rightTop.x - leftTop.x;
//        vect2.y = ((rightTop.y - leftTop.y));
//
//
//        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
//        horizontal = (sqrt(vect2.x * vect2.x + vect2.y * vect2.y));
//        DegreeHelper = (acos(vertical / horizontal)) * 180 / PI;
//        if (abs(horizontal) < abs(vertical)) {
//            DegreeParam = (acos(horizontal / vertical)) * 180 / PI;
//            direction = true;
//        } else {
//
//            DegreeParam = DegreeHelper;
//            direction = false;
//        }
//    }

    private double degreeMath() {

        Point vect1 = new Point();
        Point vect2 = new Point();
        double vertical, horizontal;
        double DegreeParam, DegreeHelper;


        vect1.x = leftBottom.x - leftTop.x;
        vect1.y = ((leftBottom.y - leftTop.y));
        vect2.x = rightTop.x - leftTop.x;
        vect2.y = ((rightTop.y - leftTop.y));


        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
        horizontal = (sqrt(vect2.x * vect2.x + vect2.y * vect2.y));
        DegreeHelper = (acos(vertical / horizontal)) * 180 / PI;
        if (abs(horizontal) < abs(vertical)) {
            DegreeParam = (acos(horizontal / vertical)) * 180 / PI;
//            direction = true;
        } else {

            DegreeParam = DegreeHelper;
//            direction = false;
        }

        int a = userPosition.getFactor() * 2;

        DegreeParam = DegreeParam * userPosition.getFactor();
        return DegreeParam;
    }

    /**
     * @return
     */

    private double distanceMath() {
        Point vect1 = new Point();
        double vertical;
        double DistanceParam;

        vect1.x = leftBottom.x - leftTop.x;
        vect1.y = ((leftBottom.y - leftTop.y));
        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
        DistanceParam = (cameraFactor / vertical) * scale;

        return DistanceParam;
    }

    public double getDistanceToCode() {
        return distanceToCode;
    }

    public double getCodeAngle() {
        return codeAngle;
    }
}


