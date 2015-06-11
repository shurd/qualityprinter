package vandyapps.com.qualityprinter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import us.feras.ecogallery.EcoGallery;

/**
 * Created by Sam on 6/10/2015.
 */
public class SetupFragment extends Fragment {
    private SeekBar pixelBar;
    private TextView pixelNumber;
    private EditText PLAColor, xmin, xmax, ymin, ymax;
    private Button subtractionButton, analysisButton;
    private EcoGallery ecoGal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setup_fragment, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        pixelBar = (SeekBar)v.findViewById(R.id.pixel_seek_bar);

        pixelNumber = (TextView)v.findViewById(R.id.pixel_text);

        PLAColor = (EditText)v.findViewById(R.id.color_edit);

        xmin = (EditText)v.findViewById(R.id.xmin_text);

        xmax = (EditText)v.findViewById(R.id.xmax_text);

        ymin = (EditText)v.findViewById(R.id.ymin_text);

        ymax = (EditText)v.findViewById(R.id.ymax_text);

        subtractionButton = (Button)v.findViewById(R.id.subtraction_button);

        analysisButton=(Button)v.findViewById(R.id.analysis_button);

        ecoGal = (EcoGallery)v.findViewById(R.id.gallery);
        ecoGal.setAdapter(new ImageAdapter(getActivity()));


        return v;
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;

        ImageAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return 3;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Not using convertView for simplicity. You should probably use it in real application to get better performance.
            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(getActivity());
            }
            switch (position) {
                case 0: Picasso.with(context).load(R.drawable.one).resize(200,200).into(view);
                    break;
                case 1: Picasso.with(context).load(R.drawable.two).resize(200,200).into(view);
                    break;
                case 2: Picasso.with(context).load(R.drawable.three).resize(200,200).into(view);
                    break;
                default: Picasso.with(context).load(R.drawable.one).resize(200,200).into(view);
            }
            //imageView.setImageResource(resId);
            return view;
        }
    }
}
