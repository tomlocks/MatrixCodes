package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Point;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class GlobalPosition {



    public static Point getGlobalPosition(Point qrPosition, int qrDegree, double distance, double degree) {
        Point globalPosition = new Point();

        globalPosition.x = qrPosition.x + distance * Math.cos(Math.toRadians(qrDegree + degree));
        globalPosition.y = qrPosition.y + distance * Math.sin(Math.toRadians(qrDegree + degree));

        return globalPosition;
    }




}
