package vandyapps.com.qualityprinter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;

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
    private boolean runTest, searchInside;
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
            //this should fix the recycled bitmap problem
            startMethod();
            printer.put("isPrinting",true);
            printer.put("error",errorString);
            printer.saveInBackground();
         }
    };

    /////////////////above this is for taking a picture
    public static CameraFragment newInstance(String id, double pixelError, boolean search, String xmin, String xmax, String ymin, String ymax,String method, String iconStr){
        Bundle args = new Bundle();
        args.putDouble("pixelerror",pixelError);
        args.putBoolean("searchInside",search);
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
        error = getArguments().getDouble("pixelerror");//TODO:change xh etc calculations back
        xl=Double.parseDouble(getArguments().getString("xmin"))-6;
        xh = Double.parseDouble(getArguments().getString("xmax"))+6;
        yl = Double.parseDouble(getArguments().getString("ymin"))-6;
        yh = Double.parseDouble(getArguments().getString("ymax"))+6;
        searchInside = getArguments().getBoolean("searchInside");
        method = getArguments().getString("method");
        icon = getArguments().getString("icon");
        runTest = true;
        myId = getArguments().getString("printerid");
        //TODO: possibly uncomment
        //Parse.initialize(getActivity(), "OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "F5QSRuhNYJ9qpiBsVvUOFJbNX2v0TJf0xeF9SCDA");
        getPrinterParse();
    }

    //only run in on create to get object initially
    public void getPrinterParse(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();//ParseQuery.getQuery("User");
        query.getInBackground(myId, new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    runTest =  object.getBoolean("isPrinting"); //run if false
                    //TODO:change back

                    object.put("errorPixels", error);
                    object.put("inside", searchInside);
                    if(method.equals("subtraction"))
                        object.put("method","s");
                    else
                        object.put("analysis","a");
                    object.saveInBackground();

                    //TODO:
                    printer = object;
                } else {
                    //error
                }
            }
        });
    }

    public void updatePrinter(){
        printer.fetchInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    runTest = object.getBoolean("isPrinting");
                } else {
                    //error
                }
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);
        ////////////////newest stuff
        camPrev = (RelativeLayout)v.findViewById(R.id.camera_preview);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        camWidth = size.x;
        camPrev.getLayoutParams().width = size.x;
        //camPrev.getLayoutParams().height = 1064;
        //////////////////
        ////////////////for taking pictures
        mProgressContainer = v.findViewById(R.id.camera_progressContainer);
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

        start = (Button)v.findViewById(R.id.start_analysis);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while(printer.getBoolean("isPrinting")){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updatePrinter();
                }
                printedMethod();//parseupdated in picture callback
            }
        });
        //////////////////for taking pictures

        mSurfaceView = (SurfaceView)v.findViewById(R.id.camera_surfaceView);

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
        if(method.equals("subtraction")){//printer.getString("method").equals("s")){//TODO
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
            } else if(icon.equals("Person")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.standing);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else {//if icon is null/empty
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            }

            Bitmap printed1 = printed.copy(Bitmap.Config.ARGB_8888, true);
            //printed.recycle();
            PictureAnalyzer picture = new PictureAnalyzer(layer, blank1, printed1, (int)error, xh-xl, yh-yl);//printer.getInt("errorPixels"), xh-xl, yh-yl);//TODO
            errorString = picture.subtractImages((searchInside));//printer.getBoolean("inside"));//TODO: change back
            Toast toast = Toast.makeText(getActivity(), errorString+"", Toast.LENGTH_LONG);
            toast.show();
            layer.recycle();
            //printed.recycle();
            //printed = null;
            edittedImage.setImageBitmap(blank1);
        } else if(method.equals("analysis")){//printer.getString("method").equals("a")){//TODO
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
            } else if(icon.equals("Person")){
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.standing);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            } else {//if icon is null/empty
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.batarang);
                layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
            }
            Bitmap printed1 = printed.copy(Bitmap.Config.ARGB_8888, true);
            //printed.recycle();
            PictureAnalyzer picture = new PictureAnalyzer(layer, printed1,(int) error,xh-xl,yh-yl);//printer.getInt("errorPixels"),xh-xl,yh-yl);//TODO
            errorString = picture.analysis(searchInside);//printer.getBoolean("inside"));//TODO: change back
            Toast toast = Toast.makeText(getActivity(), errorString+"", Toast.LENGTH_LONG);
            toast.show();
            layer.recycle();
            //printed.recycle();
            //printed = null;
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