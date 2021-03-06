package vandyapps.com.qualityprinter;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Sam on 6/11/2015.
 */
public class CameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        int pixelError = getIntent().getIntExtra("pixelnumber", 5);
        boolean inside = getIntent().getBooleanExtra("searchInside", true);
        String xmin = getIntent().getStringExtra("xmin");
        String xmax = getIntent().getStringExtra("xmax");
        String ymin = getIntent().getStringExtra("ymin");
        String ymax = getIntent().getStringExtra("ymax");
        String method = getIntent().getStringExtra("method");
        String icon = getIntent().getStringExtra("icon");
        String id = getIntent().getStringExtra("printerid");

        return CameraFragment.newInstance(id, pixelError,inside,xmin,xmax,ymin,ymax,method, icon);
    }
}

