package vandyapps.com.qualityprinter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private View mProgressContainer;
    private SurfaceView mSurfaceView;
    private Button take1Button, take2Button, start;
    private ImageView blankImage, printedImage;
    private Bitmap blank, printed;
    private RectangleView rView;
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
            Log.e("rotatedBitmap height",""+rotatedBitmap.getHeight());
            Log.e("rotatedBitmap width",""+rotatedBitmap.getWidth());

            //picture = Bitmap.createBitmap(rotatedBitmap, 0,0,rotatedBitmap.getWidth(),(int)(1.25*rotatedBitmap.getWidth()));
            //PictureCropper newP = new PictureCropper(rotatedBitmap,0,0,0,0);
            //newP.findOpticalCorners();

            printedImage.setImageBitmap(rotatedBitmap);
            blank=rotatedBitmap;
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
            blankImage.setImageBitmap(rotatedBitmap);
            printed=rotatedBitmap;
            mCamera.startPreview();
         }
    };

    /////////////////above this is for taking a picture

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
        ////////////////for taking pictures
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        rView = (RectangleView)v.findViewById(R.id.rectangle_view);


        blankImage = (ImageView)v.findViewById(R.id.blank_image);
        printedImage = (ImageView)v.findViewById(R.id.printed_image);

        take1Button = (Button)v.findViewById(R.id.take1_button);
        take1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
                }
            }
        });
        take2Button = (Button)v.findViewById(R.id.take2_button);
        take2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
                    //printed
                    mCamera.takePicture(mShutterCallback2, null, mJpegCallBack2);
                }
            }
        });
        start = (Button)v.findViewById(R.id.start_analysis);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap blank1 = blank.copy(Bitmap.Config.ARGB_8888, true);
                blank.recycle();
                Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.layer);
                Bitmap layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
                layer1.recycle();
                Bitmap printed1 = printed.copy(Bitmap.Config.ARGB_8888, true);
                printed.recycle();
                PictureAnalyzer picture = new PictureAnalyzer(layer, blank1, printed1, 0);
                Log.e("error",picture.subtractImages()+"");
                layer.recycle();
                printed.recycle();
                blankImage.setImageBitmap(blank1);
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
                parameters.setPreviewSize(s.width, s.height);
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