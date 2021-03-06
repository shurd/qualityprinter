package vandyapps.com.qualityprinter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import us.feras.ecogallery.EcoGallery;

/**
 * Created by Sam on 6/10/2015.
 */
public class SetupFragment extends Fragment {
    static final public String USER_ID="vandyapps.user.id.parse";
    final private String myPrinter = "my_printer_id";
    private SeekBar pixelBar;
    private TextView pixelNumber, iconText,printerID;
    private EditText PLAColor, xmin, xmax, ymin, ymax;
    private Button subtractionButton, analysisButton, viewImage, logout;
    private EcoGallery ecoGal;
    //private ImageAdapter imgAdapter;
    private LinearLayout mLayout;
    private CheckBox insideBox;
    private ImageView img;
    private String xminText, xmaxText, yminText, ymaxText, color, myId, modelID, imgURL;

    public static SetupFragment newInstance(String id, String modelid, String imgurl){
        Bundle args = new Bundle();
        args.putString("userID", id);
        args.putString("modelID", modelid);
        args.putString("imgURL",imgurl);

        SetupFragment fragment = new SetupFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setup_fragment, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        pixelBar = (SeekBar)v.findViewById(R.id.pixel_seek_bar);
        pixelBar.setMax(10);
        pixelBar.setProgress(5);
        pixelBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pixelNumber.setText(progress+" pixels");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pixelNumber = (TextView)v.findViewById(R.id.pixel_text);
        pixelNumber.setText(pixelBar.getProgress() + " pixels");


        xmin = (EditText)v.findViewById(R.id.xmin_text);
        xmin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                xminText = s.toString();
            }
        });
        xminText = "0";
        xmaxText = "200";
        yminText = "0";
        ymaxText = "250";
        xmax = (EditText)v.findViewById(R.id.xmax_text);
        xmax.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                xmaxText = s.toString();
            }
        });
        ymin = (EditText)v.findViewById(R.id.ymin_text);
        ymin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                yminText = s.toString();
            }
        });
        ymax = (EditText)v.findViewById(R.id.ymax_text);
        ymax.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                ymaxText = s.toString();
            }
        });

        //iconText = (TextView)v.findViewById(R.id.icon_text);
        /*
        viewImage = (Button)v.findViewById(R.id.view_image);
        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ImageDialog.class);
                i.putExtra("imagetoload",iconText.getText());
                startActivity(i);
            }
        });*/

        insideBox = (CheckBox)v.findViewById(R.id.insideCheckBox);

        subtractionButton = (Button)v.findViewById(R.id.subtraction_button);
        subtractionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(checkValues()) {
                    Intent i = new Intent(getActivity(), CameraActivity.class);
                    i.putExtra("pixelnumber", pixelBar.getProgress());
                    i.putExtra("searchInside", insideBox.isChecked());
                    i.putExtra("xmin", xminText);
                    i.putExtra("xmax", xmaxText);
                    i.putExtra("ymin", yminText);
                    i.putExtra("ymax", ymaxText);
                    i.putExtra("method", "subtraction");
                    i.putExtra("icon", iconText.getText());
                    i.putExtra("printerid", myId);
                    startActivity(i);
                }
            }
        });

        analysisButton=(Button)v.findViewById(R.id.analysis_button);
        analysisButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(checkValues()) {
                    Intent i = new Intent(getActivity(), CameraActivity.class);
                    i.putExtra("pixelnumber", pixelBar.getProgress());
                    i.putExtra("searchInside", insideBox.isChecked());
                    i.putExtra("xmin", xminText);
                    i.putExtra("xmax", xmaxText);
                    i.putExtra("ymin", yminText);
                    i.putExtra("ymax", ymaxText);
                    i.putExtra("method", "analysis");
                    i.putExtra("icon", iconText.getText());
                    i.putExtra("printerid", myId);
                    startActivity(i);
                }
            }
        });
        img = (ImageView)v.findViewById(R.id.image);
        Picasso.with(getActivity()).load(imgURL).into(img);

/*TODO: ecogal
        imgAdapter = new ImageAdapter(getActivity());
        ecoGal = (EcoGallery)v.findViewById(R.id.gallery);
        ecoGal.setTextView(v, R.id.icon_text);
        ecoGal.setAdapter(imgAdapter);
        ecoGal.setSpacing(10);
*/
        mLayout = (LinearLayout)v.findViewById(R.id.layout);
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(),0);
                return false;
            }
        });

        printerID = (TextView)v.findViewById(R.id.printer_id);
        printerID.setText(myId+" is your printer ID");
        //hide keyboard upon start

        //InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(),0);

        logout = (Button)v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            //logOutSuccessful();
                            Intent i = new Intent(getActivity(), StartActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            //somethingWentWrong();
                        }
                    }
                });
            }
        });

        return v;
    }
    public boolean checkValues(){
        Toast toast;
        if(Double.parseDouble(xminText)<2.31) {//6-2.69
            toast = Toast.makeText(getActivity(), "xmin is too low!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else if(Double.parseDouble(xmaxText)>196.31) {
            toast = Toast.makeText(getActivity(), "xmax is too high!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else if(Double.parseDouble(yminText)<6) {
            toast = Toast.makeText(getActivity(), "ymin is too low!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else if(Double.parseDouble(ymaxText)>250) {
            toast = Toast.makeText(getActivity(), "ymax is too high!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else if(Double.parseDouble(xmaxText)<Double.parseDouble(xminText)){
            toast = Toast.makeText(getActivity(), "xmax is less than xmin!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else if(Double.parseDouble(ymaxText)<Double.parseDouble(yminText)) {
            toast = Toast.makeText(getActivity(), "ymax is less than ymin!", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else
            return true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: may need to uncomment
        //Parse.initialize(getActivity(), "OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "F5QSRuhNYJ9qpiBsVvUOFJbNX2v0TJf0xeF9SCDA");

        myId = getArguments().getString("userID");
        modelID = getArguments().getString("modelID");
        imgURL = getArguments().getString("imgURL");

        /*final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains(myPrinter)){
            myId = sharedPref.getString(myPrinter,"");
        } else {
            final ParseObject newobj = new ParseObject("Printer");
            newobj.put("isPrinting", true);
            newobj.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        myId = newobj.getObjectId();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        //editor.putString(myPrinter, myId);
                        //editor.commit();
                        if (printerID != null)
                            printerID.setText(myId + " is your printer ID");
                    }
                }
            });
        }*/
    }
/* TODO ecogall
    private class ImageAdapter extends BaseAdapter {
        private Context context;

        ImageAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return 5;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(getActivity());
            }
            switch (position) {
                case 0: Picasso.with(context).load(R.drawable.batarang).resize(200,200).centerCrop().into(view);
                    break;
                case 1: Picasso.with(context).load(R.drawable.guitar).resize(200,200).centerCrop().into(view);
                    break;
                case 2: Picasso.with(context).load(R.drawable.square_ruler).resize(200,200).centerCrop().into(view);
                    break;
                case 3: Picasso.with(context).load(R.drawable.cube).resize(200,200).centerCrop().into(view);
                    break;
                case 4: Picasso.with(context).load(R.drawable.standing).resize(200,200).centerCrop().into(view);
                    break;
                default: Picasso.with(context).load(R.drawable.batarang).resize(200,200).centerCrop().into(view);
            }
            return view;
        }
    }*/
}
