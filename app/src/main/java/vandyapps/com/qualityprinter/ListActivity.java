package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 8/24/2015.
 */
public class ListActivity  extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        String objectID = getIntent().getStringExtra("userID");

        return ListFragment.newInstance(objectID);
    }
}