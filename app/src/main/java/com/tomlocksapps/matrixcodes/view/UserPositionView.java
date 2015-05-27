package com.tomlocksapps.matrixcodes.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import com.tomlocksapps.matrixcodes.R;
import com.tomlocksapps.matrixcodes.model.GlobalPosition;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.Locale;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class UserPositionView extends View {
    private int rotation;
    private double userPosition;
    private double userAngle;
    private Paint paintr,paintg,paintb,paintk,paintw, paintStroke;
    private float radius;
    private double maxUserPosition = 50.0d; // 1 m
    private RectF qrCodeRect;
    private RectF userZoneRect;
    private int distanceNormalized;
    private Point globalPosition;


    public UserPositionView(Context context) {
        super(context);
        init();
    }

    public UserPositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserPositionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintr = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintr.setColor(Color.RED);
        paintg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintg.setColor(Color.GREEN);
        paintb = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintb.setColor(Color.BLUE);
        paintk = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintk.setColor(Color.BLACK);
        paintk.setTextSize(getResources().getDimension(R.dimen.text_size_user_position));
        paintw = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintb.setTextSize(getResources().getDimension(R.dimen.text_size_user_position));
        paintw.setColor(Color.WHITE);//
        paintw.setTextSize(getResources().getDimension(R.dimen.text_size_user_position));
        paintw.setAlpha(200);// p.setAlpha(67);
        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.parseColor("#222222"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        distanceNormalized = (int)((radius*userPosition/maxUserPosition) - getResources().getDimension(R.dimen.text_size_user_position)*2);
        userZoneRect = new RectF(getWidth()/2 - distanceNormalized, getHeight()/2 - distanceNormalized, getWidth()/2 + distanceNormalized, getHeight()/2 + distanceNormalized);



        canvas.save();
//        canvas.translate(0, -getHeight()/3);

//        canvas.drawRect(getHeight()/2, getWidth()/2, getHeight(), getWidth(), paintw);


        canvas.drawText(String.format(Locale.getDefault(), "x: %3.2f", globalPosition.x) , (float)  getResources().getDimension(R.dimen.text_size_user_position) * 1, (float) (getHeight() - getResources().getDimension(R.dimen.text_size_user_position) * 1), paintk);
        canvas.drawText(String.format(Locale.getDefault(), "y: %3.2f", globalPosition.y) , (float) (getWidth()) - getResources().getDimension(R.dimen.text_size_user_position) * 5, (float) (getHeight() - getResources().getDimension(R.dimen.text_size_user_position) * 1), paintk);


        canvas.rotate(+rotation, getHeight() / 2, getWidth() / 2);
        canvas.drawCircle(getHeight() / 2, getWidth() / 2, radius, paintw);
        canvas.drawRoundRect(qrCodeRect, 5, 5, paintk);
        canvas.drawText(String.format(Locale.getDefault(), "%3.2f", userAngle) + " °", (float)(getHeight()/2) - 30,(float)( getWidth()/2  - 25), paintb);

        canvas.drawArc(userZoneRect, 0, 180, true, paintStroke);

        canvas.rotate((float) (-userAngle), getHeight() / 2, getWidth() / 2);
        canvas.drawCircle((float)(getHeight()/2),(float)( getWidth()/2 + distanceNormalized),5,paintb);
      //  canvas.drawCircle((float)(getHeight()/2),(float)( getWidth()/2 ), (float) (radius*userPosition/maxUserPosition) ,paintStroke);

//        canvas.drawRect(userZoneRect, paintk);


        canvas.drawLine((float) (getHeight() / 2), (float) (getWidth() / 2), (float) (getHeight() / 2), (float) (getWidth() / 2 + (distanceNormalized)), paintb);



        canvas.drawText(String.format(Locale.getDefault(), "%3.2f", userPosition) + " cm", (float)(getHeight()/2) - 30,(float)( getWidth()/2 +(distanceNormalized) + 25), paintb);



    }

    @Override
    protected void  onSizeChanged (int w, int h, int oldw, int oldh) {
        radius = Math.min(w / 2, h/2);
//        qrCodeRect =  new RectF(w/2 - 1*w/4 , h/2 - h/50 ,w/2 + 1*w/4 ,h/2 + h/50);
        qrCodeRect =  new RectF(w/2 - 1*w/20 , h/2 - h/50 ,w/2 + 1*w/20 ,h/2 + h/50);
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setUserPosition(double userPosition) {
        if(userPosition > maxUserPosition)
            maxUserPosition = userPosition;
        this.userPosition = userPosition;
    }

    public void setGlobalPosition(Point globalPosition) {
        this.globalPosition = globalPosition;
    }

    public void setUserAngle(double userAngle) {
        this.userAngle = userAngle;
    }
}
