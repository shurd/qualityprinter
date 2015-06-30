package vandyapps.com.qualityprinter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sam on 6/11/2015.
 */
public class PictureAnalyzer {
    private Bitmap layer, blank, printed, resized;
    private int xmin, xmax, ymin, ymax;
    private int lowx = -1, highx = 0, lowy=-1, highy=0;
    private int iconWidth, iconHeight;
    private double percentResize, percentResizeX, percentResizeY;
    private ArrayList<Integer> x = new ArrayList<Integer>();
    private ArrayList<Integer> y = new ArrayList<Integer>();
    private int centerx, centery;
    private int errorPixels;
    private double xLength, yHeight;

    public PictureAnalyzer(Bitmap layer1, Bitmap blank1, Bitmap printed1, int error, double xLen, double yLen){
        layer = layer1;
        blank = blank1;
        printed = printed1;
        xmin = 0; //?
        xmax = layer.getWidth();
        ymin = 0;//?
        ymax = layer.getHeight();
        errorPixels = error;
        xLength = xLen;//the mm width of crop
        yHeight = yLen;//the mm height of crop
    }
    public PictureAnalyzer(Bitmap layer1, Bitmap printed1, int error, double xLen, double yLen){
        layer = layer1;
       // blank = blank1;
        printed = printed1;
        xmin = 0; //?
        xmax = layer.getWidth();
        ymin = 0;//?
        ymax = layer.getHeight();
        errorPixels = error;
        xLength = xLen;
        yHeight = yLen;
    }

    public void findLayerBoundaries(){
        //find the exact boundaries of a box right around the icon
        for(int i =xmin;i<xmax;i++){
            for(int j=ymin;j<ymax;j++){
                int pixelColor = Color.blue(layer.getPixel(i,j));
                if(pixelColor<10){//>230 //black instead of white
                    if(lowx==-1||i<lowx)
                        lowx=i;
                    if(i>highx)
                        highx=i;
                    if(lowy==-1||j<lowy)
                        lowy=j;
                    if(j>highy)
                        highy=j;
                }
            }
        }
        //finding the width and height of the icon
        iconWidth = highx-lowx;
        iconHeight = highy-lowy;
        //scaling the image based on the smaller size
        //if((double)iconWidth/(double)blank.getWidth()>(double)iconHeight/(double)blank.getHeight())
        //percentResize=(double)iconWidth/blank.getWidth();
        //else
        //percentResize=(double)iconHeight/blank.getHeight();

        //if((double)iconWidth/(double)printed.getWidth()>(double)iconHeight/(double)printed.getHeight())
            percentResizeX=(double)iconWidth/printed.getWidth();
        //else
            percentResizeY=(double)iconHeight/printed.getHeight();

        //should i change the above to incorporate percentResize x and y
        resize((int)(layer.getWidth()/percentResizeX), (int)(layer.getHeight()/percentResizeY));
        /*
        highx/=percentResize;
        lowx/=percentResize;
        highy/=percentResize;
        lowy/=percentResize;*/
    }
    //called in findLayerBoundaries
    public void resize(int scaledWidth, int scaledHeight){
        scaledWidth = (int)((scaledWidth/xLength)*(xLength-13));//adds a 13 mm buffer
        scaledHeight = (int)((scaledHeight/yHeight)*(yHeight-13));
        //scaledHeight=(int)(scaledHeight*1.05);
        //scaledWidth=(int)(scaledWidth*1.05);
        //new calculation of percent resized
        percentResizeY = ((double)layer.getHeight()/(double)scaledHeight); //b/c scaledWidth = layer.getWidth()/percentresize
        percentResizeX = ((double)layer.getWidth()/(double)scaledWidth);
        highx/=percentResizeX;
        lowx/=percentResizeX;
        highy/=percentResizeY;
        lowy/=percentResizeY;//for centering

        layer = Bitmap.createScaledBitmap(layer, scaledWidth, scaledHeight, true);
        xmax = layer.getWidth();
        ymax = layer.getHeight();
    }
    //after resizing the layer image to where it would fill the screen, get an array full of the icon points
    public void getPoints(){
        for (int i = xmin;i<xmax;i++)
        {
            for (int j = ymin;j<ymax;j++)
            {
                int pixelColor = Color.blue(layer.getPixel(i, j));
                //for slic3r change below to red>240&&blue<200
                if (pixelColor<10)//red but not white (red<240&&blue<200) gives outline and perimeter
                //pixelColor>240
                {// use .svg and http://garyhodgson.github.io/slic3rsvgviewer/
                    x.add(i);
                    y.add(j);
                }
            }
        }
    }
    //now adjust all the pixel values so that the icon will be in the center of the image
    public void centerAdjustments(){
        centerx = (printed.getWidth()-(highx-lowx))/2;
        centery = (printed.getHeight()-(highy-lowy))/2;
        for(int i = 0; i<x.size();i++){
            x.set(i, x.get(i)+centerx-lowx);//the subtraction of lowx should align it properly
            y.set(i, y.get(i)+centery-lowy);
        }
    }
    //put the icon into the image now
    public void putPointsInPicture(Bitmap image){
        for(int i = 0; i<x.size();i++){
            if(x.get(i)<image.getWidth()-errorPixels&&y.get(i)<image.getHeight()-errorPixels&&x.get(i)>errorPixels&&y.get(i)>errorPixels){
                image.setPixel(x.get(i), y.get(i), Color.rgb(255, 255, 255));
                for(int j =1; j<errorPixels;j++){
                    image.setPixel(x.get(i) + j, y.get(i), Color.rgb(255, 255, 255));
                    image.setPixel(x.get(i), y.get(i) + j, Color.rgb(255, 255, 255));
                    image.setPixel(x.get(i) - j, y.get(i), Color.rgb(255, 255, 255));
                    image.setPixel(x.get(i), y.get(i) - j, Color.rgb(255, 255, 255));
                    image.setPixel(x.get(i) + j, y.get(i) + j, Color.rgb(255, 255, 255));
                    image.setPixel(x.get(i) - j, y.get(i) - j, Color.rgb(255, 255, 255));
                }
            }
        }
    }
    //perform subtraction, blank is image that is changed
    public double subtractImages(){
        Log.e("sub done", "yes");
        findLayerBoundaries();
        Log.e("boundaries done", "yes");
        getPoints();
        Log.e("points done", "yes");
        centerAdjustments();
        Log.e("center adj done", "yes");
        putPointsInPicture(printed);
        Log.e("put points in picture done", "yes");
        putPointsInPicture(blank);
        Log.e("put points in pic 2 done", "yes");
        int xmax1 = blank.getWidth();
        int ymax1 = blank.getHeight();
        int totalPixels = (x.size()), numberOfGreen=0;//comparing number outside to size of object
        for (int i = 0;i<xmax1;i++)
        {
            for (int j = 0;j<ymax1;j++)
            {
                if(i<blank.getWidth()&&i<printed.getWidth()&&j<blank.getHeight()&&j<printed.getHeight()){
                    int blue = Color.blue(blank.getPixel(i,j));
                    int red = Color.red(blank.getPixel(i,j));
                    int green = Color.green(blank.getPixel(i,j));
                    int blue2 = Color.blue(printed.getPixel(i, j));
                    int red2 = Color.red(printed.getPixel(i, j));
                    int green2 = Color.green(printed.getPixel(i, j));
                    if(Math.abs(green-green2)>100)
                        numberOfGreen ++;
                    blank.setPixel(i, j, Color.rgb(Math.abs(blue - blue2), Math.abs(red - red2), Math.abs(green - green2)));
                }
            }
        }
        return (double)numberOfGreen/(double)totalPixels;
    }
    //perform analysis, printed is changed
    public double analysis(){
        findLayerBoundaries();
        getPoints();
        centerAdjustments();
        putPointsInPicture(printed);
        int xmax1 = printed.getWidth();
        int ymax1 = printed.getHeight();
        int totalPixels = (x.size());//comparing number outside to size of object
        int numberOfGreen=0;
        for (int i = 0;i<xmax1;i++)
        {
            for (int j = 0;j<ymax1;j++)
            {
                int blue = Color.blue(printed.getPixel(i, j));
                int red = Color.red(printed.getPixel(i, j));
                int green = Color.green(printed.getPixel(i, j));
                if(blue<50&&red<50&&green<50){
                    printed.setPixel(i, j, Color.rgb(255, 0, 0));
                    numberOfGreen++;
                }
            }
        }
        return (double)numberOfGreen/(double)totalPixels;
    }
}
