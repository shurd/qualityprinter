package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 6/10/2015.
 */
public class SetupActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        String objectID = getIntent().getStringExtra("userID");
        String modelID = getIntent().getStringExtra("modelID");
        String imgURL = getIntent().getStringExtra("imgURL");

        return SetupFragment.newInstance(objectID, modelID, imgURL);
    }
}
