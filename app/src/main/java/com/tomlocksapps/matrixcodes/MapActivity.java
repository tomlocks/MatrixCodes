package com.tomlocksapps.matrixcodes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.tomlocksapps.matrixcodes.utils.Log;

import org.opencv.core.Point;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.tomlocksapps.matrixcodes.R.drawable.building_plan;


public class MapActivity extends Activity {

    private ImageView imageView;
    private PhotoViewAttacher mAttacher;

    public static final String BUNDLE_GLOBAL_X = "bundle_global_X";
    public static final String BUNDLE_GLOBAL_Y = "bundle_global_Y";

    private Paint paintRed;
    private Point globalPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent i = getIntent();

        if(i!=null) {

            float x = (float) i.getDoubleExtra(BUNDLE_GLOBAL_X, -1);
            float y = (float) i.getDoubleExtra(BUNDLE_GLOBAL_Y, -1);

            globalPosition = new Point(x, y);

            Toast.makeText(this, "X: " + x + "| y: " + y, Toast.LENGTH_SHORT).show();

            paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintRed.setColor(Color.RED);

            imageView = (ImageView) findViewById(R.id.imageViewMap);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inScaled = false;

            Bitmap bitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.building_plan, o);

            Bitmap bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(bitmapMutable);

            canvas.drawCircle((float) globalPosition.x, (float) globalPosition.y, 10, paintRed);

            imageView.setImageBitmap(bitmapMutable);

            mAttacher = new PhotoViewAttacher(imageView);

        }
// If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
//        mAttacher.update();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_map, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
