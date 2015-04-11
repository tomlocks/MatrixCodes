package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Tomasz on 2015-04-07.
 */
public class QRCode {
    private FinderPattern finderPattern;
    private Mat qrCodeMat;

    public QRCode(FinderPattern finderPattern, Mat srcMat) {
        this.qrCodeMat = new Mat(300,300, srcMat.type());


        MatOfPoint2f src = new MatOfPoint2f( finderPattern.getRightTopBorder(), finderPattern.getLeftTopBorder(), finderPattern.getLeftBottomBorder());
        MatOfPoint2f dest = new MatOfPoint2f(new Point(0, qrCodeMat.cols()), new Point(0,0), new Point(qrCodeMat.rows(), 0));

        Mat warpMat = Imgproc.getAffineTransform(src,dest);

        Imgproc.warpAffine(srcMat, qrCodeMat, warpMat, qrCodeMat.size());
    }

    public Mat getQrCodeMat() {
        return qrCodeMat;
    }

}
