package com.tomlocksapps.matrixcodes.model;

import android.os.Build;

import com.tomlocksapps.matrixcodes.MathOpertions;
import com.tomlocksapps.matrixcodes.utils.ImageUtils;
import com.tomlocksapps.matrixcodes.utils.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

/**
 * Created by Tomasz on 2015-04-07.
 */
public class QRCode {

    public FinderPattern getTopLeftFP() {
        return topLeftFP;
    }

    public FinderPattern getTopRightFP() {
        return topRightFP;
    }

    public FinderPattern getBottomLeftFP() {
        return bottomLeftFP;
    }

    private FinderPattern topLeftFP;
    private FinderPattern topRightFP;
    private FinderPattern bottomLeftFP;

    public Point getCenter() {
        return center;
    }

    private Point center;

    private Mat qrCodeMat;

    public Point getFourthPoint() {
        return fourthPoint;
    }

    private Point fourthPoint;

    private UserPosition userPosition;

    private QRCodeContent qrCodeContent;
    private double distanceToCode;
    private double codeAngle;
    private boolean direction;

    public QRCode(FinderPattern topLeftFP,FinderPattern topRightFP,FinderPattern bottomLeftFP) {


        this.bottomLeftFP = bottomLeftFP;
        this.topLeftFP = topLeftFP;
        this.topRightFP = topRightFP;

        this.center = new Point((topLeftFP.getCenter().x + getTopRightFP().getCenter().x)/2 , (topLeftFP.getCenter().y + bottomLeftFP.getCenter().y)/2);

        this.fourthPoint =  ImageUtils.calculateFourthPoint(topRightFP.getTopRight(), topRightFP.getBottomRight(),
                bottomLeftFP.getBottomLeft(), bottomLeftFP.getBottomRight());




//        int fX = (int) (finderPattern.getLeftBottom().x - finderPattern.getLeftTop().x);
//        int fY = (int) (finderPattern.getLeftBottom().y - finderPattern.getLeftTop().y);
//
//        Point fff = new Point(finderPattern.getRightTop().x + fX+20 , finderPattern.gettopRightFP.getCenter()().y + fY+20);



        //Core.circle(srcMat, fff, 2, new Scalar(255, 255, 255), 5);

//        Point ff =  ImageUtils.calculateFourthPoint(finderPattern2.getLeftBottomBorder()[0], finderPattern2.getLeftBottomBorder()[1],
//                finderPattern2.gettopRightFP.getCenter()Border()[0], finderPattern2.getRightTopBorder()[1]);
//
//
//        int offset = (int)ImageUtils.calculateDistance(finderPattern2.getLeftBottom(), finderPattern2.getLeftTop())/4;
//
//        if(ff != null) {
//
//            MatOfPoint2f src = new MatOfPoint2f(finderPattern2.getRightTopBorder()[0], finderPattern2.getLeftTopBorder(), finderPattern2.getLeftBottomBorder()[0], ff);
//            MatOfPoint2f dest = new MatOfPoint2f(new Point(offset, qrCodeMat.cols() - offset), new Point(offset, offset), new Point(qrCodeMat.rows() - offset, offset), new Point(qrCodeMat.rows() - offset, qrCodeMat.cols() - offset));
//
//
//            Core.circle(srcMat, ff, 2, new Scalar(150, 150, 150) , 5);
//            Log.d("Fourth Point","Fourth Point: " + ff.toString());
//
//
////        MatOfPoint2f src = new MatOfPoint2f( finderPattern.getRightTop(), finderPattern.getLeftTop(), finderPattern.getLeftBottom(), fff);
////        MatOfPoint2f dest = new MatOfPoint2f(new Point(offset, qrCodeMat.cols() - offset), new Point(offset,offset), new Point(qrCodeMat.rows() - offset, offset), new Point(qrCodeMat.rows() - offset,qrCodeMat.cols() - offset));
////
////        MatOfPoint2f src = new MatOfPoint2f( finderPattern.getRightTop(), finderPattern.getLeftTop(), finderPattern.getLeftBottom(), fff);
////        MatOfPoint2f dest = new MatOfPoint2f(new Point( qrCodeMat.cols() - offset,offset), new Point(offset,offset), new Point(offset,qrCodeMat.rows() - offset), new Point(qrCodeMat.rows() - offset,qrCodeMat.cols() - offset));
//
////        MatOfPoint2f src = new MatOfPoint2f( finderPattern.getRightTop(), finderPattern.getLeftTop(), finderPattern.getLeftBottom());
////        MatOfPoint2f dest = new MatOfPoint2f(new Point( qrCodeMat.cols() - offset,offset), new Point(offset,offset), new Point(offset,qrCodeMat.rows() - offset));
//
////        Mat warpMat = Imgproc.getAffineTransform(src, dest);
//
//            Mat warpMat = Imgproc.getPerspectiveTransform(src, dest);
//
//            Imgproc.warpPerspective(srcMat, qrCodeMat, warpMat, qrCodeMat.size());
//        }
//        else {
//            MatOfPoint2f src = new MatOfPoint2f(finderPattern2.getRightTopBorder()[0], finderPattern2.getLeftTopBorder(), finderPattern2.getLeftBottomBorder()[0]);
//            MatOfPoint2f dest = new MatOfPoint2f(new Point(offset, qrCodeMat.cols() - offset), new Point(offset, offset), new Point(qrCodeMat.rows() - offset, offset) );
//
//           Mat warpMat = Imgproc.getAffineTransform(src, dest);
//
//            Imgproc.warpAffine(srcMat, qrCodeMat, warpMat, qrCodeMat.size());
//        }
    }

    public boolean snipCode(Mat sourceImage) {
        this.qrCodeMat = new Mat(300,300, sourceImage.type());



        if(fourthPoint!=null) {
            MatOfPoint2f src = new MatOfPoint2f(topRightFP.getTopRight(), topLeftFP.getTopLeft() , bottomLeftFP.getBottomLeft(), fourthPoint);

            int offset = sourceImage.width()/25;

            MatOfPoint2f dest = new MatOfPoint2f(new Point(qrCodeMat.rows() - offset, offset), new Point(offset, offset), new Point(offset, qrCodeMat.cols() - offset) , new Point(qrCodeMat.rows() - offset, qrCodeMat.cols() - offset));

            Mat warpMat = Imgproc.getPerspectiveTransform(src, dest);

            Imgproc.warpPerspective(sourceImage, qrCodeMat, warpMat, qrCodeMat.size());
            return true;
        }

        return false;
    }

    public Mat getQrCodeMat() {
        return qrCodeMat;
    }

    public enum UserPosition {
        LEFT(-1), RIGHT(1),UP(1), DOWN(0), IN_FRONT_OF(0);

        int factor;

        private UserPosition(int factor) {
            this.factor = factor;
        }

        public int getFactor() {
            return this.factor;
        }

    }

    public void computeUserPosition() {
//        double distLeft = ImageUtils.calculateDistance(topLeftFP.getTopLeft(), bottomLeftFP.getBottomLeft());
//        double distRight = ImageUtils.calculateDistance(topRightFP.getTopRight(), fourthPoint);

        double distLeft = ImageUtils.calculateDistance(topLeftFP.getTopLeft(), topLeftFP.getBottomLeft());
        double distRight = ImageUtils.calculateDistance(topRightFP.getTopRight(), topRightFP.getBottomRight());


        double distTop = ImageUtils.calculateDistance(topLeftFP.getTopLeft(), topLeftFP.getTopRight());
        double distBottom = ImageUtils.calculateDistance(bottomLeftFP.getBottomLeft(), bottomLeftFP.getBottomRight());

//        if(Math.abs(distLeft - distTop) < distLeft*0.03) {
//            userPosition = UserPosition.IN_FRONT_OF;
//        } else
        if(distLeft > distTop) {
            if (distLeft > distRight) {
                userPosition = UserPosition.LEFT;
            } else
                userPosition = UserPosition.RIGHT;
        } else {
            if (distTop > distBottom) {
                userPosition = UserPosition.UP;
            } else
                userPosition = UserPosition.DOWN;
        }

    }

    public UserPosition getUserPosition() {
        return userPosition;
    }

    public boolean parseCode(String content, List<Integer> dividers) {
        qrCodeContent =  QRCodeContentParser.parseCode(content, dividers);
//        qrCodeContent =  new QRCodeContent(18,new Point(20,20), 0, 1);
        if(qrCodeContent!=null)
            return true;
        else
            return false;
    }

    public QRCodeContent getQrCodeContent() {
        return qrCodeContent;
    }


    private void computeDirection() {
        Point vect1 = new Point();
        Point vect2 = new Point();
        double vertical, horizontal;
        double DegreeParam, DegreeHelper;


        vect1.x = bottomLeftFP.getCenter().x - topLeftFP.getCenter().x;
        vect1.y = ((bottomLeftFP.getCenter().y - topLeftFP.getCenter().y));
        vect2.x = topRightFP.getCenter().x - topLeftFP.getCenter().x;
        vect2.y = ((topRightFP.getCenter().y - topLeftFP.getCenter().y));


        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
        horizontal = (sqrt(vect2.x * vect2.x + vect2.y * vect2.y));
        DegreeHelper = (acos(vertical / horizontal)) * 180 / PI;
        if (abs(horizontal) < abs(vertical)) {
            DegreeParam = (acos(horizontal / vertical)) * 180 / PI;
            direction = true;
        } else {

            DegreeParam = DegreeHelper;
            direction = false;
        }
    }

    private double cumputeDegree() {

        Point vect1 = new Point();
        Point vect2 = new Point();
        double vertical, horizontal;
        double DegreeParam, DegreeHelper;


        vect1.x = bottomLeftFP.getCenter().x - topLeftFP.getCenter().x;
        vect1.y = ((bottomLeftFP.getCenter().y - topLeftFP.getCenter().y));
        vect2.x = topRightFP.getCenter().x - topLeftFP.getCenter().x;
        vect2.y = ((topRightFP.getCenter().y - topLeftFP.getCenter().y));


        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
        horizontal = (sqrt(vect2.x * vect2.x + vect2.y * vect2.y));
        DegreeHelper = (acos(vertical / horizontal)) * 180 / PI;
        if (abs(horizontal) < abs(vertical)) {
            DegreeParam = (acos(horizontal / vertical)) * 180 / PI;
            direction = true;
        } else {

            DegreeParam = DegreeHelper;
            direction = false;
        }

        int a = userPosition.getFactor() * 2;

        DegreeParam = DegreeParam * userPosition.getFactor();
        return DegreeParam;
    }


    private double computeDistance() {
        Point vect1 = new Point();
        double vertical;
        double DistanceParam;

        vect1.x = bottomLeftFP.getCenter().x - topLeftFP.getCenter().x;
        vect1.y = ((bottomLeftFP.getCenter().y - topLeftFP.getCenter().y));
        vertical = (sqrt(vect1.x * vect1.x + vect1.y * vect1.y));
        DistanceParam = (CameraModel.getFactor(Build.MODEL) / vertical) * qrCodeContent.getSize()/20;

        return DistanceParam;
    }

}
