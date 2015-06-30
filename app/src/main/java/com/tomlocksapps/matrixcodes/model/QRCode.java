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
        double distLeft = ImageUtils.calculateDistance(topLeftFP.getTopLeft(), topLeftFP.getBottomLeft());
        double distRight = ImageUtils.calculateDistance(topRightFP.getTopRight(), topRightFP.getBottomRight());


        double distTop = ImageUtils.calculateDistance(topLeftFP.getTopLeft(), topLeftFP.getTopRight());
        double distBottom = ImageUtils.calculateDistance(bottomLeftFP.getBottomLeft(), bottomLeftFP.getBottomRight());


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


}
