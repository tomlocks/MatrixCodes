package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Point;

/**
 * Created by Tomasz on 2015-04-21.
 */
public class PointIndex extends Point{
    private int index;

    public PointIndex() {
        super();
    }

    public PointIndex(int index) {
        super();
        this.index = index;
    }

    public PointIndex(Point p , int index) {
        super(p.x, p.y);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
