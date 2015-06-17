package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 6/15/2015.
 */
public class Camera2Activity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new Camera2Fragment();
    }
}
