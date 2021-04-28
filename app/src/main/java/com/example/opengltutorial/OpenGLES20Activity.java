package com.example.opengltutorial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;

public class OpenGLES20Activity extends Activity {

  private static final String TAG = "OpenGLES20Activity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (detectOpenGLES20()) {
      //so we know it a openGL 2.0 and use our extended GLSurfaceView.
      setContentView(new MyGLSurfaceView(this));
    } else {
      // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
      // renderer if you wanted to support both ES 1 and ES 2.
      Log.e(TAG, "OpenGL ES 2.0 not supported on device.  Exiting...");
      finish();
    }
  }

  private boolean detectOpenGLES20() {
    ActivityManager am =
        (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    ConfigurationInfo info = am.getDeviceConfigurationInfo();
    return (info.reqGlEsVersion >= 0x20000);
  }
}
