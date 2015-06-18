package vandyapps.com.qualityprinter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Sam on 6/17/2015.
 */
public class RectangleView extends View {

    Paint paint = new Paint();

    public RectangleView(Context context) {
        super(context);
    }

    public RectangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // real work here
    }

    @Override
    public void onDraw(Canvas canvas) {
        //y is 250/200 * x
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        //camera set to 1024x768 (picture twice this)
        //screen set to 1200x800
        //1.5 screen ratio, 1.33333 camera ratio
        //camera scales to 1067x800
        //the difference is 133px (1200-1067)
        //try several things
        //800x1.25+133, 800x1.25+133(1066/1200)x1.25, 800x1.25+1.33x1.25
        //1133, 1147.68, 1166.25 - different alterations of the 133 stretch factor
        //800x1.25x1.5/1.33 - 1125 but way too small

        Rect rectangle = new Rect(0,0,800,1167);//1167 works well
        canvas.drawRect(rectangle, paint);

        Log.e("canvas width",""+canvas.getWidth());
        Log.e("canvas height",""+canvas.getHeight());
        Log.e("view width",""+getWidth());
        Log.e("View height",""+getHeight());

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectangle, paint);
    }
}