package com.example.opengltutorial;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Seker on 7/2/2015.
 * <p>
 * <p>
 * Some code is uses from the OpenGL ES 3.0 programming guide second edition book.  used under the MIT license.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private int mWidth;
    private int mHeight;
    private static String TAG = "myRenderer";
    public Cube mCube;
    private float mAngle = 0;
    private float mAngleX = 0;
    private float mAngleY = 0;
    private float mAngleZ = 0;
    private float scale = 1.f;

    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 40f;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    //
    public MyGLRenderer(Context context) {
        //cube can not be instianated here, because of "no egl context"  no clue.
        //do it in onSurfaceCreate and it is fine.  odd, but workable solution.
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    public static int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "Erorr!!!!");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p>
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //set the clear buffer color to light gray.
        //GLES30.glClearColor(0.9f, .9f, 0.9f, 0.9f);
        //set the clear buffer color to a dark grey.
        GLES30.glClearColor(0.1f, .1f, 0.1f, 0.9f);
        //initialize the cube code for drawing.
        mCube = new Cube();
        //if we had other objects setup them up here as well.
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0);

        //move the cube up/down and left/right
        Matrix.translateM(mRotationMatrix, 0, 0, 0, mAngleZ);

        //mangle is how fast, x,y,z which directions it rotates.
        //Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.0f, 1.0f, 1.0f);
        // Matrix.rotateM(mRotationMatrix, 0, mAngleZ, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mRotationMatrix, 0, mAngleY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mRotationMatrix, 0, mAngleX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.0f, 1.0f, 1.0f);
        Matrix.rotateM(mRotationMatrix, 0, scale, 0.0f, 0.0f, 1.0f);
        // combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        mCube.draw(mMVPMatrix);

        //change the angle, so the cube will spin.
        mAngle += .4;
    }

    // /
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);
        float aspect = (float) width / height;

        // this projection matrix is applied to object coordinates
        //no idea why 53.13f, it was used in another example and it worked.
        Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR);
    }

    //used the touch listener to rotate the cube up/down (y) and left/right (x)
    public float getAngleX() {
        return mAngleX;
    }

    public float getAngleY() {
        return mAngleY;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    public void setAngleX(float mAngleX) {
        this.mAngleX = mAngleX;
    }

    public void setAngleY(float mAngleY) {
        this.mAngleY = mAngleY;
    }

}
