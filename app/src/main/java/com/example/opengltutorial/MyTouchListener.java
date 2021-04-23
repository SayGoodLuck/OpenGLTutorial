package com.example.opengltutorial;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

class MyTouchListener extends View implements ScaleGestureDetector.OnScaleGestureListener {

    private final static String TAG = "MyTouchListener: ";
    public MyTouchListener(Context context) {
        super(context);
        Log.e(TAG, "message from MyTouchListener");
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.e(TAG, "message from onScale");
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.e(TAG, "message from onScaleBegin");
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.e(TAG, "message from onScaleEnd");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e(TAG, "message from onTouchEvent");
        return true;
    }
}
