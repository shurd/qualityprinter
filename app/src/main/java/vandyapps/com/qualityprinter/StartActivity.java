package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 6/10/2015.
 */
public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new StartFragment();
    }
}
