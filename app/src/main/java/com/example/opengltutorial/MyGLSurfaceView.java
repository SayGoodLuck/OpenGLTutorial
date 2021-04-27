package com.example.opengltutorial;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/*
 * simple extention of the GLsurfaceview.  basically setup to use opengl 3.0
 * and set some configs.  This would be where the touch listener is setup to do something.
 *
 * It also declares and sets the render.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "MyGLSurfaceView";

    private MyGLRenderer renderer;
    private ScaleGestureDetector mScaleDetector;
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mScaleFactor = 1.0f;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3);

        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        // create listener for touches and scaling
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        Log.e(TAG, "message from MyGLSurfaceView()");
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.0f, Math.min(mScaleFactor, 4.0f));
            Log.e(TAG, "message from ScaleListener()");
            renderer.setScale(mScaleFactor);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        mScaleDetector.onTouchEvent(e);

        float x = e.getX();
        float y = e.getY();

        Log.e(TAG, "tap on point (x,y): " + x + " " + y);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                renderer.setAngleX(renderer.getAngleX() + dx * TOUCH_SCALE_FACTOR);
                renderer.setAngleY(renderer.getAngleY() + dy * TOUCH_SCALE_FACTOR);

                requestRender();

        }

        previousX = x;
        previousY = y;
        return true;
    }
}
