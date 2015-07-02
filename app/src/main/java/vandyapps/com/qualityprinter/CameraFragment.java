package vandyapps.com.qualityprinter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sam on 6/11/2015.
 */
public class CameraFragment extends Fragment {
    public static int camHeight, camWidth;
    final private String myPrinter = "my_printer_id";
    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private View mProgressContainer;
    private SurfaceView mSurfaceView;
    private Button take1Button, take2Button, start;
    private ImageView blankImage, printedImage, edittedImage;
    private Bitmap blank, printed;
    private RectangleView rView;
    private double xl,xh,yl,yh;
    private String color, method, icon, myId;
    private double error, errorString;
    public ParseObject printer;
    private boolean runTest;
    private RelativeLayout camPrev;
    /////////////////////////below this for taking  a picture
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            mProgressContainer.setVisibility(View.INVISIBLE);
            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            //
            //PictureCropper here then call the get methods
            //
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(picture , 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

            picture = Bitmap.createBitmap(rotatedBitmap, 0,0,rotatedBitmap.getWidth(),(int)(1.25*rotatedBitmap.getWidth()));
            PictureCropper newP = new PictureCropper(picture,xl,xh,yl,yh);

            blank = newP.rectangleProgram();
            printedImage.setImageBitmap(blank);
            //blank=rotatedBitmap;
            mCamera.startPreview();
        }
    };
    private Camera.ShutterCallback mShutterCallback2 = new Camera.ShutterCallback() {
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallBack2 = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            mProgressContainer.setVisibility(View.INVISIBLE);
            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(picture , 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

            picture = Bitmap.createBitmap(rotatedBitmap, 0,0,rotatedBitmap.getWidth(),(int)(1.25*rotatedBitmap.getWidth()));
            PictureCropper newP = new PictureCropper(picture,xl,xh,yl,yh);

            printed = newP.rectangleProgram();
            blankImage.setImageBitmap(printed);
            //printed=rotatedBitmap;
            mCamera.startPreview();
         }
    };

    /////////////////above this is for taking a picture
    public static CameraFragment newInstance(String id, double pixelError, String color, String xmin, String xmax, String ymin, String ymax,String method, String iconStr){
        Bundle args = new Bundle();
        args.putDouble("pixelerror",pixelError);
        args.putString("placolor",color);
        args.putString("xmin", xmin);
        args.putString("xmax", xmax);
        args.putString("ymin", ymin);
        args.putString("ymax", ymax);
        args.putString("method", method);
        args.putString("icon", iconStr);
        args.putString("printerid", id);

        CameraFragment fragment = new CameraFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        error = getArguments().getDouble("pixelerror");
        xl=Double.parseDouble(getArguments().getString("xmin"));
        xh = Double.parseDouble(getArguments().getString("xmax"));
        yl = Double.parseDouble(getArguments().getString("ymin"));
        yh = Double.parseDouble(getArguments().getString("ymax"));
        color = getArguments().getString("placolor");
        method = getArguments().getString("method");
        icon = getArguments().getString("icon");
        runTest = true;
        myId = getArguments().getString("printerid");
        //initialize parse
        Parse.initialize(getActivity(), "OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "F5QSRuhNYJ9qpiBsVvUOFJbNX2v0TJf0xeF9SCDA");
        getPrinterParse();
    }

    //where to put this method
    public void executeProgram(int n){

    }

    //only run in on create to get object initially
    public void getPrinterParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Printer");
        query.getInBackground(myId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    runTest =  object.getBoolean("isPrinting"); //run if false
                    printer = object;
                } else {
                    //Log.e("parse error","error");
                    // something went wrong
                }
            }
        });
    }

    public void updatePrinter(){
        printer.fetchInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    runTest = object.getBoolean("isPrinting");
                } else {

                }
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
        ////////////////newest stuff
        camPrev = (RelativeLayout)v.findViewById(R.id.crime_camera_preview);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        camWidth = size.x;
        camPrev.getLayoutParams().width = size.x;
        //camPrev.getLayoutParams().height = 1064;
        //////////////////
        ////////////////for taking pictures
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        rView = (RectangleView)v.findViewById(R.id.rectangle_view);

        edittedImage = (ImageView)v.findViewById(R.id.editted_image);
        blankImage = (ImageView)v.findViewById(R.id.blank_image);
        printedImage = (ImageView)v.findViewById(R.id.printed_image);

        take1Button = (Button)v.findViewById(R.id.take1_button);
        take1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                blankMethod();
            }
        });
        take2Button = (Button)v.findViewById(R.id.take2_button);
        take2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                printedMethod();
            }
        });
        start = (Button)v.findViewById(R.id.start_analysis);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while(printer.getBoolean("isPrinting")){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updatePrinter();
                }
                printedMethod();//here

                //must do a background delay before analysis because the picture updated on callback
                //nullpointer was happening because picture was not being saved
                //may need to make longer because recycled, was 5 now 10
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startMethod();
                        printer.put("isPrinting",true);
                        printer.put("error",errorString);
                        printer.saveInBackground();
                    }
                }, 10000);

            }
        });
        //////////////////for taking pictures

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback(){
            public void surfaceCreated(SurfaceHolder holder){
                try{
                    if(mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception){
                    //error
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder){
                if(mCamera!=null){
                    mCamera.stopPreview();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
                if(mCamera==null) return;
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s =getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);

                double heighttowidth = (double)s.width/(double)s.height;//the preview makes the screen sideways, so you need to reverse the values
                camHeight = (int)(camPrev.getLayoutParams().width*heighttowidth);
                //now set the height a new value so that the preview is not stretched
                camPrev.getLayoutParams().height = camHeight;
                parameters.setPreviewSize(s.width, s.height);//set as 1024 and 768 - screen actually 800x1200

                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);//fixes the sideways orientation
                try{
                    mCamera.startPreview();
                }catch(Exception e){
                    //error
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        return v;
    }

    public void printedMethod(){
        if (mCamera != null) {
            //printed
            mCamera.takePicture(mShutterCallback2, null, mJpegCallBack2);
        }
    }

    public void blankMethod(){
        if (mCamera != null) {
            mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
        }
    }

    public void startMethod(){
        //mProgressContainer.setVisibility(View.VISIBLE);
        if(printer.getString("method").equals("s")){//method.equals("subtraction")){
            Bitmap blank1 = blank.copy(Bitmap.Config.ARGB_8888, true);
            //blank.recycle();
            Bitmap layer;
            if(icon.equals("Batarang")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Guitar")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.guitar);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Square Ruler")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.square_ruler);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Cube")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.cube);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else {//if icon is null/empty
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            }

            Bitmap printed1 = printed.copy(Bitmap.Config.ARGB_8888, true);
            //printed.recycle();
            //Log.e("errorP", printer.getInt("errorPixels")+"");
            PictureAnalyzer picture = new PictureAnalyzer(layer, blank1, printed1, printer.getInt("errorPixels"), xh-xl, yh-yl);//(int)error, xh-xl, yh-yl);
            errorString = picture.subtractImages();
            Toast toast = Toast.makeText(getActivity(), errorString+"", Toast.LENGTH_LONG);
            toast.show();
            layer.recycle();
            printed.recycle();
            edittedImage.setImageBitmap(blank1);
        } else if(printer.getString("method").equals("a")){//method.equals("analysis")){
            //Bitmap blank1 = blank.copy(Bitmap.Config.ARGB_8888, true);
            //blank.recycle();
            Bitmap layer;
            if(icon.equals("Batarang")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Guitar")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.guitar);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Square Ruler")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.square_ruler);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else if(icon.equals("Cube")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.cube);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else {//if icon is null/empty
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            }
            Bitmap printed1 = printed.copy(Bitmap.Config.ARGB_8888, true);
            //printed.recycle();
            PictureAnalyzer picture = new PictureAnalyzer(layer, printed1,printer.getInt("errorPixels"),xh-xl,yh-yl);//(int) error,xh-xl,yh-yl);
            errorString = picture.analysis();
            Toast toast = Toast.makeText(getActivity(), errorString+"", Toast.LENGTH_LONG);
            toast.show();
            layer.recycle();
            printed.recycle();
            edittedImage.setImageBitmap(printed1);
        }
        //mProgressContainer.setVisibility(View.INVISIBLE);
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height){
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width*bestSize.height;
        for(Camera.Size s : sizes){
            int area = s.width*s.height;
            if(area>largestArea){
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    public void onResume(){
        super.onResume();
        mCamera=Camera.open(0);
    }

    public void onPause(){
        super.onPause();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
    }

}