package vandyapps.com.qualityprinter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 8/24/2015.
 */
public class ListFragment extends Fragment{
    private String myId;
    private ArrayList<ParseObject> list;
    private ListView listview;


    public static ListFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putString("userID", id);

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: may need to uncomment
        //Parse.initialize(getActivity(), "OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "F5QSRuhNYJ9qpiBsVvUOFJbNX2v0TJf0xeF9SCDA");

        myId = getArguments().getString("userID");
    }

    public void stuff(){
        list = new ArrayList<ParseObject>();

        ParseQuery innerQuery = new ParseQuery("_User");
        innerQuery.whereEqualTo("objectId",myId); //inner query retrieves object

        ParseQuery query = new ParseQuery("SVGfile");
        query.whereMatchesQuery("user", innerQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objList, ParseException e) {
                // comments now contains the comments for posts without images.
                if (e == null) {
                    for(int i =0; i<objList.size();i++){
                        list.add(objList.get(i));
                    }
                    final ModelAdapter adapter = new ModelAdapter(list);
                    listview.setAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.listview_fragment, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        listview = (ListView) v.findViewById(R.id.listview);
        stuff();
        /*String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }*/
            /*
        final ModelAdapter adapter = new ModelAdapter(list);
        listview.setAdapter(adapter);

        */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                ParseObject item = (ParseObject) parent.getItemAtPosition(position);

                Log.e("item", item.getObjectId());

                Intent i = new Intent(getActivity(), SetupActivity.class);//was SetupActivity.class
                i.putExtra("userID", myId);
                i.putExtra("modelID", item.getObjectId());
                i.putExtra("imgURL",item.getParseFile("img").getUrl());
                startActivity(i);
            }

        });
        return v;
    }


    private class ModelAdapter extends ArrayAdapter<ParseObject> {

        public ModelAdapter(ArrayList<ParseObject> models){
                super(getActivity(), 0, models);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_element, null);//change this
            }

            ParseObject o = getItem(position);

            ImageView img = (ImageView)convertView.findViewById(R.id.icon);
            Picasso.with(getActivity()).load(o.getParseFile("img").getUrl()).into(img);

            TextView txt = (TextView)convertView.findViewById(R.id.firstLine);
            txt.setText(o.getString("name"));

            return convertView;
        }
    }
}
