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
        xmin = 0;
        xmax = layer.getWidth();
        ymin = 0;
        ymax = layer.getHeight();
        errorPixels = error;
        xLength = xLen;//the mm width of crop
        yHeight = yLen;//the mm height of crop
    }
    public PictureAnalyzer(Bitmap layer1, Bitmap printed1, int error, double xLen, double yLen){
        layer = layer1;
        printed = printed1;
        xmin = 0;
        xmax = layer.getWidth();
        ymin = 0;
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
                if(pixelColor<10){
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
        //finding the percent to resize each direction of icon image
        percentResizeX=(double)iconWidth/printed.getWidth();
        percentResizeY=(double)iconHeight/printed.getHeight();

        resize((int)(layer.getWidth()/percentResizeX), (int)(layer.getHeight()/percentResizeY));
    }
    //called in findLayerBoundaries
    public void resize(int scaledWidth, int scaledHeight){
        //the dimensions of the cropped image do not scale to the dimensions of the icon
        //this calculation done to account for 6.5mm buffer given by pronterface dimensions
        scaledWidth = (int)((scaledWidth/xLength)*(xLength-12));//TODO: change to 13 if needed
        scaledHeight = (int)((scaledHeight/yHeight)*(yHeight-12));
        //new resized calculation given new dimensions
        percentResizeY = ((double)layer.getHeight()/(double)scaledHeight);
        percentResizeX = ((double)layer.getWidth()/(double)scaledWidth);
        //for centering the icon in the taken image
        highx/=percentResizeX;
        lowx/=percentResizeX;
        highy/=percentResizeY;
        lowy/=percentResizeY;
        //actual rescale the icon image
        layer = Bitmap.createScaledBitmap(layer, scaledWidth, scaledHeight, true);
        xmax = layer.getWidth();
        ymax = layer.getHeight();
    }
    //after resizing the layer image to where it would fill the image, get an array full of the icon points
    public void getPoints(){
        for (int i = xmin;i<xmax;i++)
        {
            for (int j = ymin;j<ymax;j++)
            {
                int pixelColor = Color.blue(layer.getPixel(i, j));
                if (pixelColor<10)
                {
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
    public void putPointsInPicture(Bitmap image, Boolean searchInside){
        for(int i = 0; i<x.size();i++){
            if(x.get(i)<image.getWidth()-errorPixels&&y.get(i)<image.getHeight()-errorPixels&&x.get(i)>errorPixels&&y.get(i)>errorPixels){
                //have to add check that it is not white (ie has not already been set by error pixel)
                if(searchInside&&Color.blue(image.getPixel(x.get(i),y.get(i)))>100 && Color.red(image.getPixel(x.get(i),y.get(i)))>100&&Color.green(image.getPixel(x.get(i),y.get(i)))>100){
                    image.setPixel(x.get(i),y.get(i), Color.rgb(0,0,0));//if searchInside and not black (color of pla), make the pixel black. when you subtract from white blank, this becomes white so ad search for white
                } else {//works as intended for black already, but no indication that its inside rather than out
                    image.setPixel(x.get(i), y.get(i), Color.rgb(255, 255, 255));
                }
            }
        }

        for(int i = 0; i<x.size();i++){
            if(x.get(i)<image.getWidth()-errorPixels&&y.get(i)<image.getHeight()-errorPixels&&x.get(i)>errorPixels&&y.get(i)>errorPixels){
                //if error is selected, this adds more pixels
                for(int j =1; j<errorPixels;j++){//added if statement in the case of searching inside
                    if(Color.blue(image.getPixel(x.get(i)+j,y.get(i)))!=0)
                        image.setPixel(x.get(i) + j, y.get(i), Color.rgb(255, 255, 255));
                    if(Color.blue(image.getPixel(x.get(i),y.get(i)+j))!=0)
                        image.setPixel(x.get(i), y.get(i) + j, Color.rgb(255, 255, 255));
                    if(Color.blue(image.getPixel(x.get(i)-j,y.get(i)))!=0)
                        image.setPixel(x.get(i) - j, y.get(i), Color.rgb(255, 255, 255));
                    if(Color.blue(image.getPixel(x.get(i),y.get(i)-j))!=0)
                        image.setPixel(x.get(i), y.get(i) - j, Color.rgb(255, 255, 255));
                    if(Color.blue(image.getPixel(x.get(i)+j,y.get(i)+j))!=0)
                        image.setPixel(x.get(i) + j, y.get(i) + j, Color.rgb(255, 255, 255));
                    if(Color.blue(image.getPixel(x.get(i)-j,y.get(i)-j))!=0)
                        image.setPixel(x.get(i) - j, y.get(i) - j, Color.rgb(255, 255, 255));
                }
            }
        }
    }
    //perform subtraction, blank is image that is changed
    public double subtractImages(Boolean inside){
        findLayerBoundaries();
        getPoints();
        centerAdjustments();
        putPointsInPicture(printed, inside);
        putPointsInPicture(blank, false);
        int xmax1 = blank.getWidth();
        int ymax1 = blank.getHeight();
        int totalPixels = (x.size()), numberOfGreen=0;//comparing number outside to size of object
        //now look for pixels that are out of place ie. pixels that should be inside the icon but are not
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
                    int newGreen = Math.abs(green-green2);
                    int newBlue = Math.abs(blue-blue2);
                    int newRed = Math.abs(red-red2);
                    if((newRed>20&&newGreen>45&&newBlue>35||(inside&&newRed==255&&newGreen==255&&newBlue==255))) {
                        numberOfGreen++;
                        blank.setPixel(i, j, Color.rgb(75, 160, 250));
                    }else
                        blank.setPixel(i, j, Color.rgb(newRed, newGreen,newBlue));
                }
            }
        }
        return (double)numberOfGreen/(double)totalPixels;
    }
    //perform analysis, printed is changed
    public double analysis(Boolean inside){
        findLayerBoundaries();
        getPoints();
        centerAdjustments();
        putPointsInPicture(printed, inside);
        int xmax1 = printed.getWidth();
        int ymax1 = printed.getHeight();
        int totalPixels = (x.size());//comparing number outside to size of object
        int numberOfGreen=0;
        //look for black pixels (or the PLA color pixels)
        for (int i = 0;i<xmax1;i++)
        {
            for (int j = 0;j<ymax1;j++)
            {
                int blue = Color.blue(printed.getPixel(i, j));
                int red = Color.red(printed.getPixel(i, j));
                int green = Color.green(printed.getPixel(i, j));
                if(blue<100&&red<100&&green<100){
                    printed.setPixel(i, j, Color.rgb(75, 160, 250));
                    numberOfGreen++;
                }
            }
        }
        return (double)numberOfGreen/(double)totalPixels;
    }
}
