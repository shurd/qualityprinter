package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 6/10/2015.
 */
public class SetupActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        String objectID = getIntent().getStringExtra("userID");

        return SetupFragment.newInstance(objectID);
    }
}
