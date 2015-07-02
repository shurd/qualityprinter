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

    //find the coordinates of the four corners (indicated with colors)
    //need to start cycle in top right, so start with max y and then move down
   /* public void findOpticalCorners(){
        for (int i = 0;i<bitmap.getWidth();i++){
            for (int j = 0;j<bitmap.getHeight();j++){
                if(i==0&&j==0){
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawCircle(i, j, 50, paint);
                }
                int blueColor = Color.blue(bitmap.getPixel(i, j));
                int redColor = Color.red(bitmap.getPixel(i,j));
                int greenColor = Color.green(bitmap.getPixel(i,j));
                if (yellow &&((blueColor>=156&&blueColor<=175)&&(redColor>=186&&redColor<=201)&&(greenColor>=180&&greenColor<=201))){//color not determined yet && the color of the pixel then define that point
                    xmin1 = i;//upper left
                    ymax1 = j;
                    yellow = false;
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawCircle(i, j, 50, paint);
                } else if(orange &&((blueColor>=70&&blueColor<=99)&&(redColor>=210&&redColor<=255)&&(greenColor>=122&&greenColor<=158))){
                    xmax1 = i;//upper right
                    ymax2 = j;
                    orange = false;
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawCircle(i, j, 50, paint);
                } else if(green&&((blueColor>=185&&blueColor<=255)&&(redColor>=140&&redColor<=153)&&(greenColor>=211&&greenColor<=255))){
                    xmin2 = i;//bottom left
                    ymin1 = j;
                    green = false;
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawCircle(i, j, 50, paint);
                } else if(red&&((blueColor>=103&&blueColor<=116)&&(redColor>=222&&redColor<=255)&&(greenColor>=112&&greenColor<=131))){
                    xmax2 = i;//bottom right
                    ymin2 = j;
                    red = false;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawCircle(i, j, 50, paint);
                }
            }
        }
    }
    //find the average x and y dimensions based on the 4 corners then find x per mm and y per mm (pixel conversions)
    //this method may change if we rotate the image
    //with rotation, use trig to find length
    public void calculations(){
        int x = (xmax1+xmax2-xmin1-xmin2)/2;
        int y = (ymax1+ymax2-ymin1-ymin2)/2;
        xpermm = x/200;
        ypermm=y/250;
    }

    //find the pixel coordinates of the bottom left corner of the printed object using the passed in values
    public void bottomLeftOfObject(){//0,0 is top left of bitmap, may need to change stuff
        xl = (int)(xmin2+xpermm*xmin);//green corner
        yl = (int)(ymin1+ypermm*ymin);
    }

    //find the width and height of the printed item to be able to crop it
    public void findDimensions(){
        objectWidth = (int)((xmax-xmin)*xpermm);
        objectHeight = (int)((ymax-ymin)*ypermm);
    }

    //may be wrong due to orientation of 0,0 on bitmap (see below method)
    public void runProgram(){
        findOpticalCorners();
        calculations();
        bottomLeftOfObject();
        findDimensions();
    }*/
    public Bitmap rectangleProgram(){
        /*int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        xpermm = x/200;
        ypermm = y/250;*/

        int xcorner = (int)((xmin/200.)*bitmap.getWidth());
        int ycorner = (int)(((250.-ymax)/250.)*bitmap.getHeight());
        int xlength = (int)(((xmax-xmin)/200.)*bitmap.getWidth());
        int ylength = (int)(((ymax-ymin)/250.)*bitmap.getHeight());

        Bitmap bmp=Bitmap.createBitmap(bitmap,xcorner, ycorner, xlength, ylength);
        return bmp;
    }

    public int getXL(){
        return xl;
    }
    public int getYL(){
        return yl;
    }
    public int getObjectWidth(){
        return objectWidth;
    }
    public int getObjectHeight(){
        return objectHeight;
    }

}
