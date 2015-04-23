package com.tomlocksapps.matrixcodes.model;

import android.util.Log;

import com.tomlocksapps.matrixcodes.utils.ImageUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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

    private Mat qrCodeMat;

    public Point getFourthPoint() {
        return fourthPoint;
    }

    private Point fourthPoint;


    public QRCode(FinderPattern topLeftFP,FinderPattern topRightFP,FinderPattern bottomLeftFP) {


        this.bottomLeftFP = bottomLeftFP;
        this.topLeftFP = topLeftFP;
        this.topRightFP = topRightFP;

//        int fX = (int) (finderPattern.getLeftBottom().x - finderPattern.getLeftTop().x);
//        int fY = (int) (finderPattern.getLeftBottom().y - finderPattern.getLeftTop().y);
//
//        Point fff = new Point(finderPattern.getRightTop().x + fX+20 , finderPattern.getRightTop().y + fY+20);



        //Core.circle(srcMat, fff, 2, new Scalar(255, 255, 255), 5);

//        Point ff =  ImageUtils.calculateFourthPoint(finderPattern2.getLeftBottomBorder()[0], finderPattern2.getLeftBottomBorder()[1],
//                finderPattern2.getRightTopBorder()[0], finderPattern2.getRightTopBorder()[1]);
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

    public void snipCode(Mat sourceImage) {
        this.qrCodeMat = new Mat(300,300, sourceImage.type());

        this.fourthPoint =  ImageUtils.calculateFourthPoint(topRightFP.getTopRight(), topRightFP.getBottomRight(),
               bottomLeftFP.getBottomLeft(), bottomLeftFP.getBottomRight());

         //       MatOfPoint2f src = new MatOfPoint2f(finderPattern2.getRightTopBorder()[0], finderPattern2.getLeftTopBorder(), finderPattern2.getLeftBottomBorder()[0], ff);
//        MatOfPoint2f dest = new MatOfPoint2f(new Point(offset, qrCodeMat.cols() - offset), new Point(offset, offset), new Point(qrCodeMat.rows() - offset, offset), new Point(qrCodeMat.rows() - offset, qrCodeMat.cols() - offset));


        if(fourthPoint!=null) {
            MatOfPoint2f src = new MatOfPoint2f(topRightFP.getTopRight(), topLeftFP.getTopLeft() , bottomLeftFP.getBottomLeft(), fourthPoint);

            int offset = 0;

            MatOfPoint2f dest = new MatOfPoint2f(new Point(offset, qrCodeMat.cols() - offset), new Point(offset, offset), new Point(qrCodeMat.rows() - offset, offset), new Point(qrCodeMat.rows() - offset, qrCodeMat.cols() - offset));

            Mat warpMat = Imgproc.getPerspectiveTransform(src, dest);

            Imgproc.warpPerspective(sourceImage, qrCodeMat, warpMat, qrCodeMat.size());
        }

    }

    public Mat getQrCodeMat() {
        return qrCodeMat;
    }

}
