package vandyapps.com.qualityprinter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        float width = canvas.getWidth()-25;
        float height = (float)1.25*(canvas.getWidth()+50);
        //canvas.drawRect(25,50,width,height, paint);
        canvas.drawRect(00,00,800,1050, paint);

        Log.e("canvas width",""+canvas.getWidth());
        Log.e("canvas height",""+canvas.getHeight());
        Log.e("view width",""+getWidth());
        Log.e("View height",""+getHeight());


        // canvas.drawRect(50,50,canvas.getWidth()-50,canvas.getHeight()-250, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(00,00,800,1050, paint);

       // canvas.drawRect(25,50,width,height, paint);
       /* paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        canvas.drawRect(0, 0, 180, 180, paint);
        paint.setStrokeWidth(0);*/
    }
}