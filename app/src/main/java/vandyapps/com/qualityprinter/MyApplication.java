package vandyapps.com.qualityprinter;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Sam on 8/3/2015.
 */
public class MyApplication extends Application {
    public void onCreate(){
        Parse.initialize(this,getString(R.string.parse_app_id),getString(R.string.parse_client_id));
    }
}
