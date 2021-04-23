package com.example.opengltutorial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;

/*
 *  This is a example of a cube that rotates and moves.
 *
 *  The code here, simply makes sure that opengl 3.0 is available and starts up
 *  the the surfaceview class I extended.
 */

public class OpenGLES20Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (detectOpenGLES30()) {
            //so we know it a opengl 3.0 and use our extended GLsurfaceview.
            setContentView(new MyGLSurfaceView(this));
        } else {
            // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();
        }
    }

    private boolean detectOpenGLES30() {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x30000);
    }
}
