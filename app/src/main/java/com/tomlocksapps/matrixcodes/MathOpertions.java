
package com.tomlocksapps.matrixcodes;

        import android.app.Application;
        import android.graphics.Point;
        import android.graphics.PointF;

        import static java.lang.Math.*;

/**
 * Created by AveAmore on 2015-03-23.
 */
public class MathOpertions {


        /**
         *
         * @param point1 first point of QRcode
         * @param point2 center point of QRcode
         * @param point3 third point of QRcode
         * @param xdeg degree around x axis
         * @param zdeg degree around z axis
         * @return
         */
        public float DegreeMath(Point point1, Point point2, Point point3, float xdeg, float zdeg)
        {

            PointF vect1 = new PointF();
            PointF vect2 = new PointF();
            PointF vect1_temp = new PointF();
            PointF vect2_temp = new PointF();
            float vertical,horizontal;
            float DegreeParam;

//vect declaration
            vect1.x = point1.x-point2.x;
            vect1.y = (float) ((point1.y-point2.y)/cos(xdeg));
            vect2.x = point3.x-point2.x;
            vect2.y = (float)((point3.y-point2.y)/cos(xdeg));
            vect1_temp = vect1;
            vect2_temp = vect2;
            vect1.x = (float)(vect1_temp.x * cos(zdeg)- vect1_temp.y * sin(zdeg));
            vect1.y = (float)(vect1_temp.x * sin(zdeg) + vect1_temp.y * cos(zdeg));

            vect2.x = (float)(vect2_temp.x * cos(zdeg) - vect2_temp.y * sin(zdeg));
            vect2.y = (float)(vect2_temp.x * sin(zdeg) + vect2_temp.y * cos(zdeg));

            vertical = (float)(sqrt(vect1.x*vect1.x+vect1.y*vect1.y));
            horizontal = (float)(sqrt(vect2.x*vect2.x+vect2.y*vect2.y));
            DegreeParam = (float)(acos(horizontal/vertical));

            return DegreeParam;
        }

    }


