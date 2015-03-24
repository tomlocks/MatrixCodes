
package com.tomlocksapps.matrixcodes;



        import org.opencv.core.Point;
        import static java.lang.Math.*;

/**
 * Created by AveAmore on 2015-03-23.
 */
public class MathOpertions {

    boolean direction; //true - horizontal , false vertical

    private Point leftBottom ;
    private Point leftTop;
    private Point rightTop;
    private double yDeg;
    private double zDeg;

    public MathOpertions(Point leftBottom, Point leftTop, Point rightTop, double yDeg, double zDeg) {
        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.yDeg = yDeg;
        this.zDeg = zDeg - 90;
    }


    /**
         *

         * @return
         */
        public double DegreeMath()
        {

            Point vect1 = new Point();
            Point vect2 = new Point();
            Point vect1_temp = new Point();
            Point vect2_temp = new Point();
            double vertical,horizontal;
            double DegreeParam,DegreeHelper;

//vect declaration
            vect1.x = leftBottom.x- leftTop.x;
            vect1.y =  ((leftBottom.y- leftTop.y)/cos(yDeg));
            vect2.x = rightTop.x- leftTop.x;
            vect2.y = ((rightTop.y- leftTop.y)/cos(yDeg));
            vect1_temp.x = leftBottom.x- leftTop.x;
            vect1_temp.y =  ((leftBottom.y- leftTop.y)/cos(yDeg));
            vect2_temp.x = rightTop.x- leftTop.x;
            vect2_temp.y = ((rightTop.y- leftTop.y)/cos(yDeg));




            vect1.x = (vect1_temp.x * cos(zDeg)- vect1_temp.y * sin(zDeg));
            vect1.y = (vect1_temp.x * sin(zDeg) + vect1_temp.y * cos(zDeg));

            vect2.x = (vect2_temp.x * cos(zDeg) - vect2_temp.y * sin(zDeg));
            vect2.y = (vect2_temp.x * sin(zDeg) + vect2_temp.y * cos(zDeg));

            vertical = (sqrt(vect1.x*vect1.x+vect1.y*vect1.y));
            horizontal = (sqrt(vect2.x*vect2.x+vect2.y*vect2.y));
            DegreeHelper = (acos(vertical/horizontal))*180/PI;
            if(abs(horizontal)<abs(vertical)) {
                DegreeParam = (acos(horizontal / vertical)) * 180 / PI;
                direction = true;
            }
            else {

                DegreeParam=DegreeHelper;
                direction = false;
            }


            return DegreeParam;
        }

    /**
     *
     * @return
     */

    public double DistanceMath()
    {
        Point vect1 = new Point();
        Point vect1_temp = new Point();
        double vertical;
        double DistanceParam;
        double DistanceHelper = 140;

//vect declaration
        vect1.x = leftBottom.x- leftTop.x;
        vect1.y =  ((leftBottom.y- leftTop.y)/cos(yDeg));
        vect1_temp.x = leftBottom.x- leftTop.x;
        vect1_temp.y =  ((leftBottom.y- leftTop.y)/cos(yDeg));
        vect1.x = (vect1_temp.x * cos(zDeg)- vect1_temp.y * sin(zDeg));
        vect1.y = (vect1_temp.x * sin(zDeg) + vect1_temp.y * cos(zDeg));



        vertical = (sqrt(vect1.x*vect1.x+vect1.y*vect1.y));
        DistanceParam = DistanceHelper/ vertical;

        return DistanceParam;
    }


    public boolean getDirection() {
        return direction;
    }


}


