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
    private Paint paintBlack;
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

        paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBlack.setColor(Color.BLACK);
        paintBlack.setAlpha(125);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.translate(getWidth()/2, getHeight()/2);
//        drawArrow(canvas);

        canvas.drawRect(0,0, getWidth()/4, getHeight(), paintBlack );
        canvas.drawRect(3*getWidth()/4,0, getWidth(), getHeight(), paintBlack );

        canvas.drawRect(getWidth()/4, 0 , 3*getWidth()/4, getHeight()/4, paintBlack );

        canvas.drawRect(getWidth()/4, 3*getHeight()/4 , 3*getWidth()/4, getHeight(), paintBlack );

//        canvas.drawRect(0,0, getWidth()/4, getHeight(), paintBlack );
//        canvas.drawRect(0,0, getWidth()/4, getHeight(), paintBlack );



    }

//    protected void drawArrow(Canvas canvas) {
//        canvas.drawRect(-getWidth()/12, -2*getHeight()/10,  getWidth()/10, 2*getHeight()/12,  p);
//
//    }



}
