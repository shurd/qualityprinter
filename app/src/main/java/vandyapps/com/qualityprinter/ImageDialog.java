package vandyapps.com.qualityprinter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Sam on 6/15/2015.
 */
public class ImageDialog extends Activity {

    private ImageView mDialog;
    private String imageNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageNumber = getIntent().getStringExtra("imagetoload");

        setContentView(R.layout.picture_dialog);

        mDialog = (ImageView)findViewById(R.id.image_dialog);
        mDialog.setClickable(true);

        if(imageNumber.equals("Batarang"))
            Picasso.with(this).load(R.drawable.batarang).into(mDialog);
        else if(imageNumber.equals("Guitar"))
            Picasso.with(this).load(R.drawable.guitar).into(mDialog);
        else if(imageNumber.equals("Square Ruler"))
            Picasso.with(this).load(R.drawable.square_ruler).into(mDialog);
        else
            Picasso.with(this).load(R.drawable.batarang).into(mDialog);



        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WindowManager.LayoutParams lp=getWindow().getAttributes();
        //set transparency of background
        lp.dimAmount=0.6f;  // dimAmount between 0.0f and 1.0f, 1.0f is completely dark
        //lp.width = 200;
        //lp.height =  300;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }
}