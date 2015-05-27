package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Point;

import java.lang.reflect.Field;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class QRCodeContent {
    private int size; // in cm
    private Point p; //
    private int angle;
    private int z; // floor

    public QRCodeContent()  {

    }

    public QRCodeContent(int size, Point p, int angle, int z) {
        this.size = size;
        this.p = p;
        this.angle = angle;
        this.z = z;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPoint(Point p) {
        this.p = p;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getSize() {
        return size;
    }

    public Point getP() {
        return p;
    }

    public int getAngle() {
        return angle;
    }

    public int getZ() {
        return z;
    }
}
