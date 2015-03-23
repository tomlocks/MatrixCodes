
package com.tomlocksapps.matrixcodes;



        import org.opencv.core.Point;
        import static java.lang.Math.*;

/**
 * Created by AveAmore on 2015-03-23.
 */
public class MathOpertions {

    boolean direction; //true - horizontal , false vertical

        /**
         *
         * @param point1 first point of QRcode
         * @param point2 center point of QRcode
         * @param point3 third point of QRcode
         * @param xdeg degree around x axis
         * @param zdeg degree around z axis
         * @return
         */
        public double DegreeMath(Point point1, Point point2, Point point3, float xdeg, float zdeg)
        {

            Point vect1 = new Point();
            Point vect2 = new Point();
            Point vect1_temp = new Point();
            Point vect2_temp = new Point();
            double vertical,horizontal;
            double DegreeParam,DegreeHelper;

//vect declaration
            vect1.x = point1.x-point2.x;
            vect1.y =  ((point1.y-point2.y)/cos(xdeg));
            vect2.x = point3.x-point2.x;
            vect2.y = ((point3.y-point2.y)/cos(xdeg));
            vect1_temp.x = point1.x-point2.x;
            vect1_temp.y =  ((point1.y-point2.y)/cos(xdeg));
            vect2_temp.x = point3.x-point2.x;
            vect2_temp.y = ((point3.y-point2.y)/cos(xdeg));




            vect1.x = (vect1_temp.x * cos(zdeg)- vect1_temp.y * sin(zdeg));
            vect1.y = (vect1_temp.x * sin(zdeg) + vect1_temp.y * cos(zdeg));

            vect2.x = (vect2_temp.x * cos(zdeg) - vect2_temp.y * sin(zdeg));
            vect2.y = (vect2_temp.x * sin(zdeg) + vect2_temp.y * cos(zdeg));

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

    public static double DistanceMath(Point point1, Point point2,float xdeg, float zdeg)
    {

        Point vect1 = new Point();
        Point vect1_temp = new Point();
        double vertical;
        double DistanceParam;
        double DistanceHelper = 140;

//vect declaration
        vect1.x = point1.x-point2.x;
        vect1.y =  ((point1.y-point2.y)/cos(xdeg));
        vect1_temp.x = point1.x-point2.x;
        vect1_temp.y =  ((point1.y-point2.y)/cos(xdeg));
        vect1.x = (vect1_temp.x * cos(zdeg)- vect1_temp.y * sin(zdeg));
        vect1.y = (vect1_temp.x * sin(zdeg) + vect1_temp.y * cos(zdeg));



        vertical = (sqrt(vect1.x*vect1.x+vect1.y*vect1.y));
        DistanceParam = DistanceHelper/ vertical;

        return DistanceParam;
    }


    public boolean getDirection() {
        return direction;
    }


}


