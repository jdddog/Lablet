package nz.ac.auckland.lablet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import nz.ac.auckland.lablet.views.VideoFrameView;

/** A temporary activity to test integration of OpenCV and Lablet */

public class OpenCVTestActivity extends Activity {

    private VideoFrameView videoFrameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_cvtest);
        videoFrameView = (VideoFrameView)findViewById(R.id.videoFrameViewOpenCV);
        assert videoFrameView != null;
        //File storageDir = videoData.getStorageDir();
        File videoFile = new File("/sdcard/DCIM/Camera/", "20150721_180134.mp4");
        int videoRotation = 0;
        videoFrameView.setVideoFilePath(videoFile.getPath(), videoRotation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_cvtest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
