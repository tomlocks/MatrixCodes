package com.tomlocksapps.matrixcodes.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.core.Point;

/**
 * Created by Tomasz on 2015-05-09.
 */
public class DrawView extends View {

    private Paint p;
    private Point topLeft, topRight, bottomLeft;
    private double xRatio = 1;
    private double yRatio = 1;


    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
      //  p.setAlpha(67);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      //  canvas.drawRect(getWidth()/4, getHeight()/4, 3*getWidth()/4, 3*getHeight()/4, p);

//        drawFinderPatternCenters(canvas);
        if(topLeft != null)
        canvas.drawCircle((float)(topLeft.x* xRatio) ,(float)topLeft.y, 15, p);
    }

//    private void drawFinderPatternCenters(Canvas canvas) {
//        if(topLeft == null || topRight == null || bottomLeft == null)
//            return;
//
//
//        canvas.drawCircle((float)topLeft.x ,(float)topLeft.y, 15, p);
//
//    }

    public void setRatio(Camera.Size cameraSize) {
        this.xRatio = (double)cameraSize.height/getHeight();
        this.yRatio = (double)cameraSize.width/getWidth();
    }

    public void setPoints(Point topLeft, Point topRight, Point bottomLeft) {

        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
    }



}
