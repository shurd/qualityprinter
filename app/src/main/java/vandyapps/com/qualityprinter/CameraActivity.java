package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 6/11/2015.
 */
public class CameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CameraFragment();
    }
}

