package com.tomlocksapps.matrixcodes;

import android.util.Log;

import com.tomlocksapps.matrixcodes.model.FinderPattern;
import com.tomlocksapps.matrixcodes.model.FinderPattern2;
import com.tomlocksapps.matrixcodes.model.QRCode;
import com.tomlocksapps.matrixcodes.utils.CountourAreaComparator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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

    public static QRCode findFinderPattern(Mat image) {


        Mat imageCanny = Mat.zeros(image.size(), image.type());

        Imgproc.Canny(image, imageCanny, 100, 300);

        List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
        List<MatOfPoint> contoursFinderPattern = new LinkedList<MatOfPoint>();

        Mat hierarchy = new Mat(100, 100, CvType.CV_32SC4);

        Imgproc.findContours(imageCanny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contoursDrawing = Mat.zeros(image.size(), CvType.CV_8UC1);


        int[] hier = new int[(int) hierarchy.total() * hierarchy.channels()];

        hierarchy.get(0, 0, hier);

        Mat contourMat = Mat.zeros(image.size(),  CvType.CV_8UC1);


//
//        for (int i = 0; contours.size() > i; i++) {
//            int secondParent = -1, thirdParent = -1, fourhParent = -1;
//
//
//            int firstParent = hier[i * 4 + 3];
//            if (firstParent != -1)
//                secondParent = hier[firstParent * 4 + 3];
//            if (secondParent != -1)
//                thirdParent = hier[secondParent * 4 + 3];
//            if (fourhParent != -1)
//                fourhParent = hier[thirdParent * 4 + 3];
//
//            if (fourhParent != -1)
//                Log.d("contoursFinderPattern", "fourhParent != -1");
//
//            Imgproc.drawContours(contourMat, contours, i, Scalar.all(255), 5);
//
//            if (firstParent != -1 && secondParent != -1 && thirdParent != -1) {
//
//                double area1 = Imgproc.contourArea(contours.get(thirdParent));
//                double area2 = Imgproc.contourArea(contours.get(firstParent));
//
//
//
//
//                double ratio = area1 / area2;
//
//                if (2.5 < ratio && ratio < 4.5) {
//
////                    Imgproc.drawContours(contourMat, contours, i, Scalar.all(70), 1);
////                    Imgproc.drawContours(contourMat, contours, firstParent, Scalar.all(100), 1);
////                    Imgproc.drawContours(contourMat, contours, secondParent, Scalar.all(170), 1);
////                    Imgproc.drawContours(contourMat, contours, thirdParent, Scalar.all(250), 1);
//
//
//                    Log.d("contoursFinderPattern", "ratio: " + ratio);
//
//                    contoursFinderPattern.add(contours.get(thirdParent));
//                }
//            }
//
//        }


//        for (int i = 0; contours.size() > i; i++) {
//            int secondChild = -1, thirdChild = -1, fourhChild = -1;
//
//
//            int firstChild = hier[i * 4 + 2];
//            if (firstChild != -1)
//                secondChild = hier[firstChild * 4 + 3];
//            if (secondChild != -1)
//                thirdChild = hier[secondChild * 4 + 3];
//            if (thirdChild != -1)
//                fourhChild = hier[fourhChild * 4 + 3];
//
//            if (fourhChild != -1)
//                Log.d("contoursFinderPattern", "fourhParent != -1");
//
//            Imgproc.drawContours(contourMat, contours, i, Scalar.all(255), 5);
//
//            if (firstChild != -1 && secondParent != -1 && thirdParent != -1) {
//
//                double area1 = Imgproc.contourArea(contours.get(thirdParent));
//                double area2 = Imgproc.contourArea(contours.get(firstParent));
//
//
//
//
//                double ratio = area1 / area2;
//
//                if (2.5 < ratio && ratio < 4.5) {
//
////                    Imgproc.drawContours(contourMat, contours, i, Scalar.all(70), 1);
////                    Imgproc.drawContours(contourMat, contours, firstParent, Scalar.all(100), 1);
////                    Imgproc.drawContours(contourMat, contours, secondParent, Scalar.all(170), 1);
////                    Imgproc.drawContours(contourMat, contours, thirdParent, Scalar.all(250), 1);
//
//
//                    Log.d("contoursFinderPattern", "ratio: " + ratio);
//
//                    contoursFinderPattern.add(contours.get(thirdParent));
//                }
//            }
//
//        }



        for (int i = 0; contours.size() > i; i++) {

            int k=i;
            int c=0;

            while(hier[k*4 + 2] != -1)
            {
                k = hier[k*4 + 2] ;
                c = c+1;
            }
            if(hier[k*4 + 2] != -1)
                c = c+1;

            if (c >= 5) {
                double areaK = Imgproc.contourArea(contours.get(k));
                double areaI = Imgproc.contourArea(contours.get(i));

                Log.d("area","area: " + areaI/areaK);

                if(4.5 < areaI/areaK && areaI/areaK < 7.5) {
                    contoursFinderPattern.add(contours.get(i));
                    Imgproc.drawContours(contourMat, contours, i, Scalar.all(255), 5);
                }
            }
        }

        Collections.sort(contoursFinderPattern, new CountourAreaComparator());


        // find the 3 areas with the lowest stdDev
        if (contoursFinderPattern.size() > 3) {
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

                contoursFinderPattern = contoursFinderPattern.subList(position, position + 2);

            }
        }

        // compute mass centers
        List<Point> mc = new ArrayList<Point>();

        for (int i = 0; contoursFinderPattern.size() > i; i++) {

            Moments mu = Imgproc.moments(contoursFinderPattern.get(i));
            mc.add(new Point(mu.get_m10() / mu.get_m00(), mu.get_m01() / mu.get_m00()));

            Imgproc.drawContours(contoursDrawing, contoursFinderPattern, i, Scalar.all(100), 5);
        }




        //     FinderPattern finderPattern =  new FinderPattern(new Point(0,0),new Point(0,0),new Point(0,0));
        //     finderPattern.setMat(contoursDrawing);

        if (contoursFinderPattern.size() == 3) {

            int rightTopIndex = 0;
            int leftBottomIndex = 0;
            int leftTopIndex = 0;

            List<RotatedRect> minAreaRects = new ArrayList<RotatedRect>(3);
            List<Rect> boundingRects= new ArrayList<Rect>(3);



            for (MatOfPoint contourFinderPattern : contoursFinderPattern) {
                minAreaRects.add(Imgproc.minAreaRect(new MatOfPoint2f(contourFinderPattern.toArray())));
                boundingRects.add(Imgproc.boundingRect(contourFinderPattern));


             //   boundingRects.add(Imgproc.boundingRect(new MatOfPoint(contourFinderPattern.toArray())));

            }


//            for (Rect rect : boundingRects) {
//              Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x+rect.width, rect.y + rect.height), Scalar.all(100) , 5);
//
//                //   boundingRects.add(Imgproc.boundingRect(new MatOfPoint(contourFinderPattern.toArray())));
//
//            }


            for (RotatedRect areaRect : minAreaRects) {
                double blob_angle_deg = areaRect.angle;
                if (areaRect.size.width < areaRect.size.height) {
                    blob_angle_deg = 0 + blob_angle_deg; // 90
                }

                Log.d("areaRect", "areaRect angle: " + blob_angle_deg + " center: " + areaRect.center);
            }

//            for(RotatedRect r :minAreaRects ) {
//                Point[] pt = new Point[4];
//                r.points(pt);
//
//                for(int i=0; pt.length > i ; i++) {
//                    pt[i] = ImageUtils.rotatePoint(pt[i], r.angle);
//                    Core.circle(contoursDrawing, pt[i], 20, new Scalar(100, 100, 100), 2);
//                }
//            }


            Log.d("areaRect", "areaRect angle: ----------- ");

            double maxLength = 0;
            int maxIndex = 0;

            for (int i = 0; mc.size() > i; i++) {
                double length = Math.sqrt(Math.pow((mc.get(i).x - mc.get((i + 1) % 3).x), 2) + Math.pow((mc.get(i).y - mc.get((i + 1) % 3).y), 2));
                if (length > maxLength) {
                    maxLength = length;
                    maxIndex = i;
                }
            }

            Point center = new Point((mc.get(maxIndex).x + mc.get((maxIndex + 1) % 3).x)/2 , (mc.get(maxIndex).y + mc.get((maxIndex + 1) % 3).y)/2);

            leftTopIndex = (maxIndex + 2) % 3;

            Log.d("TopLeft", "Center:  " + center.toString()  + " -- leftTop: " + mc.get(leftTopIndex).toString() );

            double angle = 0;

            if(mc.get((maxIndex + 2) % 3).y > center.y) {
                Log.d("TopLeft", "TopLeft :  TOP"  );
                if((mc.get(maxIndex).x > mc.get((maxIndex + 1) % 3).x)) {
                    leftBottomIndex = maxIndex;
                    rightTopIndex  = (maxIndex + 1) % 3;
               }
                else {
                    leftBottomIndex= (maxIndex + 1) % 3;
                    rightTopIndex  = maxIndex;
               }
            } else if(mc.get((maxIndex + 2) % 3).y < center.y){
                Log.d("TopLeft", "TopLeft :  BOTTOM"  );
                angle = 90;
                if((mc.get(maxIndex).x < mc.get((maxIndex + 1) % 3).x)) {
                    leftBottomIndex = maxIndex;
                    rightTopIndex        = (maxIndex + 1) % 3;
                }
                else {
                    leftBottomIndex    = (maxIndex + 1) % 3;
                    rightTopIndex = maxIndex;
                }
            } else
                return null;



//            Imgproc.drawContours(image, contoursFinderPattern, 0,  new Scalar(200,0,200), 5);
//            Imgproc.drawContours(image, contoursFinderPattern, 1,  new Scalar(200,0,200), 5);
//            Imgproc.drawContours(image, contoursFinderPattern, 2,  new Scalar(200,0,200), 5);



            Core.line(image, mc.get(maxIndex), mc.get((maxIndex + 1) % 3), new Scalar(100, 100, 100), 5);


            FinderPattern topLeft = new FinderPattern(contoursFinderPattern.get(leftTopIndex), FinderPattern.SquarePosition.TOP_LEFT, center, mc.get(leftTopIndex));
            FinderPattern topRight = new FinderPattern(contoursFinderPattern.get(rightTopIndex), FinderPattern.SquarePosition.TOP_RIGHT, center, mc.get(rightTopIndex));
            FinderPattern bottomLeft = new FinderPattern(contoursFinderPattern.get(leftBottomIndex), FinderPattern.SquarePosition.BOTTOM_LEFT, center, mc.get(leftBottomIndex));


            topLeft.findAdditionalPoints(topRight.getCenter());
            topRight.findAdditionalPoints(topLeft.getCenter());
            bottomLeft.findAdditionalPoints(topLeft.getCenter());


            Core.circle(image, topLeft.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
            Core.circle(image, topLeft.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
            Core.circle(image, topLeft.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
            Core.circle(image, topLeft.getTopRight(), 5, new Scalar(0, 255, 0 ), 7);

            Core.circle(image, topRight.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
            Core.circle(image, topRight.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
            Core.circle(image, topRight.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
            Core.circle(image, topRight.getTopRight(), 5, new Scalar(0, 255, 0 ), 7);

            Core.circle(image, bottomLeft.getTopLeft(), 5, new Scalar(255, 0, 0), 7);
            Core.circle(image, bottomLeft.getBottomRight(), 5, new Scalar(0, 0, 255), 7);
            Core.circle(image, bottomLeft.getBottomLeft(), 5, new Scalar(0, 255, 255), 7);
            Core.circle(image, bottomLeft.getTopRight(), 5, new Scalar(0, 255, 0 ), 7);



            QRCode qrCode = new QRCode(topLeft, topRight, bottomLeft);

            return qrCode;

//            for (int i = 0; mc.size() > i; i++) {
//                if (mc.get(i).x > mc.get(rightTopIndex).x)
//                    rightTopIndex = i;
//                if (mc.get(i).y > mc.get(leftBottomIndex).y)
//                    leftBottomIndex = i;
//            }
//
//            for (int i = 0; mc.size() > i; i++) {
//                if (i != rightTopIndex && i != leftBottomIndex)
//                    leftTopIndex = i;
//            }

//            int rightTopBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(minAreaRects.get(rightTopIndex), mc.get(rightTopIndex), true, false);
//            int leftTopBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(minAreaRects.get(leftTopIndex), mc.get(leftTopIndex), false, false);
//            int leftBottomBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(minAreaRects.get(leftBottomIndex), mc.get(leftBottomIndex), false, true);

//            int rightTopBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(contoursFinderPattern.get(rightTopIndex), mc.get(rightTopIndex), true, false);
//            int leftTopBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(contoursFinderPattern.get(leftTopIndex), mc.get(leftTopIndex), false, false);
//            int leftBottomBorderIndex = ImageUtils.getMaximumLengthBetweenContourAndPointIndex(contoursFinderPattern.get(leftBottomIndex), mc.get(leftBottomIndex), false, true);

//            Core.circle(contoursDrawing, contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex], 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, contoursFinderPattern.get(leftTopIndex).toArray()[leftTopBorderIndex], 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, contoursFinderPattern.get(leftBottomIndex).toArray()[leftBottomBorderIndex], 20, new Scalar(100, 100, 100), 2);


            // wykorzystanie katow rotstedRect

//            Size s = minAreaRects.get(leftTopIndex).size;
//
//            double tempX = 0.5d*(s.height*Math.sin(Math.toRadians(minAreaRects.get(leftTopIndex).angle)) - s.width*Math.cos(Math.toRadians(minAreaRects.get(leftTopIndex).angle)));
//            double tempY = 0.5d*(s.height*Math.cos(Math.toRadians(minAreaRects.get(leftTopIndex).angle)) + s.width*Math.sin(Math.toRadians(minAreaRects.get(leftTopIndex).angle)));
//
//            Log.d("balbla", "tempX :" + tempX + "   -- tempY: " + tempY);
//
//            Log.d("balbla", "size :" + s.height  +  " angle " +minAreaRects.get(leftTopIndex).angle );
//
//            Point leftTopBorder = new Point(mc.get(leftTopIndex).x + 0.5d*(s.height*Math.sin(Math.toRadians(minAreaRects.get(leftTopIndex).angle + angle)) - s.width*Math.cos(Math.toRadians(minAreaRects.get(leftTopIndex).angle + angle)))  ,
//                    mc.get(leftTopIndex).y - 0.5d*(s.height*Math.cos(Math.toRadians(minAreaRects.get(leftTopIndex).angle  + angle)) + s.width*Math.sin(Math.toRadians(minAreaRects.get(leftTopIndex).angle + angle))));
//
//            s = minAreaRects.get(rightTopIndex).size;
//
//            Point rightTopBorder = new Point(mc.get(rightTopIndex).x + 0.5d*(s.height*Math.sin(Math.toRadians(minAreaRects.get(rightTopIndex).angle)) + s.width*Math.cos(Math.toRadians(minAreaRects.get(rightTopIndex).angle)))  ,
//                    mc.get(rightTopIndex).y - 0.5d*(s.height*Math.cos(Math.toRadians(minAreaRects.get(rightTopIndex).angle)) - s.width*Math.sin(Math.toRadians(minAreaRects.get(rightTopIndex).angle))));
//
//            s = minAreaRects.get(leftBottomIndex).size;
//
//            Point leftBottomBorder = new Point(mc.get(leftBottomIndex).x - 0.5d*(s.height*Math.sin(Math.toRadians(minAreaRects.get(leftBottomIndex).angle)) + s.width*Math.cos(Math.toRadians(minAreaRects.get(leftBottomIndex).angle)))  ,
//                    mc.get(leftBottomIndex).y + 0.5d*(s.height*Math.cos(Math.toRadians(minAreaRects.get(leftBottomIndex).angle)) - s.width*Math.sin(Math.toRadians(minAreaRects.get(leftBottomIndex).angle))));



//            Core.circle(contoursDrawing, mc.get(leftTopIndex), 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, mc.get(rightTopIndex), 5, new Scalar(25, 25, 25), 20);
//            Core.circle(contoursDrawing, mc.get(leftBottomIndex), 5, new Scalar(255, 255, 255), 5);


//            Core.circle(contoursDrawing, leftTopBorder, 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, rightTopBorder, 5, new Scalar(25, 25, 25), 20);
//            Core.circle(contoursDrawing, leftBottomBorder, 5, new Scalar(255, 255, 255), 5);





//            finderPattern = new FinderPattern(mc.get(leftTopIndex), mc.get(rightTopIndex), mc.get(leftBottomIndex), contoursFinderPattern.get(leftTopIndex).toArray()[leftTopBorderIndex], contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex], contoursFinderPattern.get(leftBottomIndex).toArray()[leftBottomBorderIndex]);


//            Point leftTopBorder = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(leftTopIndex), center);
//
//            Point[] rightTopBorder = ImageUtils.getMinMaxLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), center);
//            Point[] leftBottomBorder = ImageUtils.getMinMaxLengthBetweenContourAndPoint(contoursFinderPattern.get(leftBottomIndex), center);
//
//            int[] rightTopBorderIndex = ImageUtils.getMinMaxLengthBetweenContourAndPointIndex(contoursFinderPattern.get(rightTopIndex), center);
//            int[] leftBottomBorderIndex = ImageUtils.getMinMaxLengthBetweenContourAndPointIndex(contoursFinderPattern.get(leftBottomIndex), center);
//
//            Log.d("indexes", "indexes: " + rightTopBorderIndex[0] + " | " + rightTopBorderIndex[1]);
//
//
//            Core.circle(image, contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[0]] , 6, new Scalar(0, 0, 255), 7);
//            Core.circle(image, contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[1]] , 6, new Scalar(0, 255, 0), 7);
//
//
//
////                Imgproc.drawContours(image, contoursFinderPattern, rightTopIndex ,  new Scalar(255, 0, 0), 5);
//
//
//            Log.d("indexes", "contour size: " + contoursFinderPattern.get(rightTopIndex).toArray().length
//            );
//
//            List<MatOfPoint> mt = null;
//
//            if(rightTopBorderIndex[0] < rightTopBorderIndex[1])
//                mt = ImageUtils.findSubArrays(contoursFinderPattern.get(rightTopIndex), rightTopBorderIndex[0], rightTopBorderIndex[1]);
//            else
//                mt = ImageUtils.findSubArrays(contoursFinderPattern.get(rightTopIndex), rightTopBorderIndex[1], rightTopBorderIndex[0]);
//
//            Imgproc.drawContours(image, mt, 0, new Scalar(255, 0, 0), 5);
//            Imgproc.drawContours(image, mt , 1, new Scalar(0,0,255), 5);
//
//            Point p1 =  ImageUtils.findFurthestPointBetweenPoints(mt.get(0), contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[0]], contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[1]]);
//
//            Point p2 =  ImageUtils.findFurthestPointBetweenPoints(mt.get(1), contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[0]], contoursFinderPattern.get(rightTopIndex).toArray()[rightTopBorderIndex[1]]);
//
//
//            Core.circle(image, p1 , 8, new Scalar(255, 0, 255), 10);
//            Core.circle(image, p2 , 8, new Scalar(0, 255, 255), 10);
//



//            Point[] rightTopBorder = ImageUtils.getThreeEdges(contoursFinderPattern.get(rightTopIndex), center, mc.get(leftTopIndex));
//            Point[] leftBottomBorder = ImageUtils.getThreeEdges(contoursFinderPattern.get(leftBottomIndex), center, mc.get(leftTopIndex));

//            Point[] rightTopBorder = ImageUtils.getFourEdges(contoursFinderPattern.get(rightTopIndex));
//            Point[] leftBottomBorder = ImageUtils.getFourEdges(contoursFinderPattern.get(leftBottomIndex));



//            rightTopBorder[0] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), center);
//            leftTopBorder[0]= ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(leftTopIndex), center);
//            leftBottomBorder[0] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(leftBottomIndex), center);


//            rightTopBorder[1] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), mc.get(rightTopIndex));
//            rightTopBorder[2] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), mc.get(rightTopIndex));
//            rightTopBorder[3] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), mc.get(rightTopIndex));

//            rightTopBorder[1] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(rightTopIndex), leftTopBorder[0]);
//            leftBottomBorder[1] = ImageUtils.getMaximumLengthBetweenContourAndPoint(contoursFinderPattern.get(leftBottomIndex), leftTopBorder[0]);

//            Log.d("Matrix Codes","rightTopBorder: " + rightTopBorder[1] + " | leftBottomBorder: " + leftBottomBorder[1]);

//            Core.circle(contoursDrawing, rightTopBorder[0], 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, rightTopBorder[1], 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, rightTopBorder[2], 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, rightTopBorder[3], 20, new Scalar(100, 100, 100), 2);


//            Core.circle(contoursDrawing, leftTopBorder, 20, new Scalar(100, 100, 100), 2);
//            Core.circle(contoursDrawing, leftBottomBorder[1], 20, new Scalar(100, 100, 100), 2);

       //     rightTopBorder, leftBottomBorder



            //minAreaRects

//            finderPattern = new FinderPattern(mc.get(leftTopIndex), mc.get(rightTopIndex), mc.get(leftBottomIndex));

//            finderPattern = new FinderPattern(mc.get(leftTopIndex), mc.get(rightTopIndex), mc.get(leftBottomIndex), leftTopBorder, rightTopBorder, leftBottomBorder);


//            ImageUtils.findEdges2(contoursFinderPattern.get(leftTopIndex), leftTop, )



//            Log.d("contour" , "contour size: " +       contoursFinderPattern.get(leftTopIndex).toArray().length);
//            Log.d("contour" , "contour: " +       contoursFinderPattern.get(leftTopIndex).toArray()[0].x + " | " + contoursFinderPattern.get(leftTopIndex).toArray()[0].y);
//
//
//            Log.d("contour" , "contour: " +       contoursFinderPattern.get(leftTopIndex).toArray()[5].x + " | " + contoursFinderPattern.get(leftTopIndex).toArray()[5].y);
//
//
//            Log.d("contour" , "contour: " +       contoursFinderPattern.get(leftTopIndex).toArray()[10].x + " | " + contoursFinderPattern.get(leftTopIndex).toArray()[10].y);



//            Core.circle(contourMat, fff, 5, new Scalar(255,255,255), 10);


//            Core.circle(image, finderPattern.getLeftBottomBorder()[0], 3, new Scalar(0, 0, 255), 5);
//            Core.circle(image, finderPattern.getRightTopBorder()[0], 3, new Scalar(0, 0, 255), 5);
//            Core.circle(image, finderPattern.getLeftTopBorder()[0], 3 ,new Scalar(0, 0, 255), 5);

//            Point[] ppp = ImageUtils.findEdges(contoursFinderPattern.get(leftBottomIndex));
//
//            for(int i=0; ppp.length > i ; i++) {
//                Core.circle(image, ppp[i], 7, new Scalar(0, 0, 255), 7);
//            }
//
//            ppp = ImageUtils.findEdges(contoursFinderPattern.get(rightTopIndex));
//
//            for(int i=0; ppp.length > i ; i++) {
//                Core.circle(image, ppp[i], 7, new Scalar(0, 0, 255), 7);
//            }
//
//            ppp = ImageUtils.findEdges(contoursFinderPattern.get(leftTopIndex));
//
//            for(int i=0; ppp.length > i ; i++) {
//                Core.circle(image, ppp[i], 7, new Scalar(0, 0, 255), 7);
//            }

//            Core.circle(image, finderPattern.getLeftBottomBorder()[1], 7, new Scalar(0, 0, 255), 7);
//            Core.circle(image, finderPattern.getRightTopBorder()[1], 7, new Scalar(0, 0, 255), 7);
//
//            Core.circle(image, finderPattern.getLeftBottomBorder()[0], 5, new Scalar(255, 0, 0), 5);
//            Core.circle(image, finderPattern.getRightTopBorder()[0], 5, new Scalar(255, 0, 0), 5);
//
//
//            Core.circle(image, finderPattern.getLeftBottomBorder()[2], 3, new Scalar(0,255, 0), 3);
//            Core.circle(image, finderPattern.getRightTopBorder()[2], 3, new Scalar(0, 255, 0), 3);
//
//            Core.circle(image, finderPattern.getLeftBottomBorder()[3], 3, new Scalar(0,255, 0), 3);
//            Core.circle(image, finderPattern.getRightTopBorder()[3], 3, new Scalar(0, 255, 0), 3);



//            Core.circle(image, finderPattern.getLeftBottom(), 5, new Scalar(255, 0 , 0), 5);
//            Core.circle(image, finderPattern.getRightTop(), 5, new Scalar(0,  255, 0), 5);
//            Core.circle(image, finderPattern.getLeftTop(), 5, new Scalar(0, 0, 255), 5);

//            Point ff =  ImageUtils.calculateFourthPoint(finderPattern2.getLeftBottomBorder()[0], finderPattern2.getLeftBottomBorder()[1],
//                    finderPattern2.getRightTopBorder()[0], finderPattern2.getRightTopBorder()[1]);
//
//            if(ff!=null)
//                Core.circle(image, ff, 3, new Scalar(0, 0, 255), 5);
//
//            finderPattern2.contours = contourMat;



//            finderPattern2.setMat(image);
        }

        return null;
    }


}
