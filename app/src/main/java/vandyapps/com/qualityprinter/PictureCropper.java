package vandyapps.com.qualityprinter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by Sam on 6/16/2015.
 */
public class PictureCropper {
    private Bitmap bitmap;
    private int xmax1, xmax2, xmin1, xmin2;
    private int ymax1, ymax2, ymin1, ymin2;
    private int xl, yl, objectHeight, objectWidth;
    private double xmin, xmax, ymin, ymax;
    private double xpermm, ypermm;
    private boolean yellow, orange, green, red;

    //constructor
    //pass in the Bitmap to crop
    public PictureCropper(Bitmap bmp, double x1, double x2, double y1, double y2){
        bitmap = bmp;
        xmin = x1+3.69;//adjustment because printer is off-set (x=0 is actually ~x=3.69)
        xmax = x2+3.69;
        ymin = y1;
        ymax = y2;
        yellow=true;
        orange=true;
        green=true;
        red=true;
    }

    public Bitmap rectangleProgram(){
        int xcorner = (int)((xmin/200.)*bitmap.getWidth());
        int ycorner = (int)(((250.-ymax)/250.)*bitmap.getHeight());
        int xlength = (int)(((xmax-xmin)/200.)*bitmap.getWidth());
        int ylength = (int)(((ymax-ymin)/250.)*bitmap.getHeight());
        Bitmap bmp=Bitmap.createBitmap(bitmap,xcorner, ycorner, xlength, ylength);
        return bmp;
    }
}
