package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;

/**
 * Created by Sam on 8/3/2015.
 */
public class RegisterActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new RegisterFragment();
    }
}