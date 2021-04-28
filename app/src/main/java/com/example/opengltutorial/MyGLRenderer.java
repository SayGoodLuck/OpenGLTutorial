package com.example.opengltutorial;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

  private static final String TAG = "myRenderer";
  private Cube mCube;
  private float mAngle = 0;
  private float mAngleX = 0;
  private float mAngleY = 0;
  private float scale = 0.5f;

  private static final float Z_NEAR = 1f;
  private static final float Z_FAR = 40f;

  // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
  private final float[] mMVPMatrix = new float[16];
  private final float[] mProjectionMatrix = new float[16];
  private final float[] mViewMatrix = new float[16];
  private final float[] mRotationMatrix = new float[16];

  public MyGLRenderer(Context context) {
    // cube can not be instigated here, because of "no egl context"  no clue.
    // do it in onSurfaceCreate and it is fine.  odd, but workable solution.
  }

  ///
  // Create a shader object, load the shader source, and
  // compile the shader.
  //
  public static int loadShader(int type, String shaderSrc) {
    int shader;
    int[] compiled = new int[1];

    // Create the shader object
    shader = GLES20.glCreateShader(type);

    if (shader == 0) {
      return 0;
    }

    // Load the shader source
    GLES20.glShaderSource(shader, shaderSrc);

    // Compile the shader
    GLES20.glCompileShader(shader);

    // Check the compile status
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

    if (compiled[0] == 0) {
      Log.e(TAG, "Erorr!!!!");
      Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      return 0;
    }

    return shader;
  }

  /*
   * Utility method for debugging OpenGL calls. Provide the name of the call
   * just after making it:
   *
   * <pre>
   * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
   * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
   * <p>
   * If the operation is not successful, the check throws an error.
   *
   * @param glOperation - Name of the OpenGL call to check.
   */

  /*
   * Checks to see if a GLES20 error has been raised.
   */
  public static void checkGlError(String op) {
    int error = GLES20.glGetError();
    if (error != GLES20.GL_NO_ERROR) {
      String msg = op + ": glError 0x" + Integer.toHexString(error);
      Log.e(TAG, msg);
      throw new RuntimeException(msg);
    }
  }

  ///
  // Initialize the shader and program object
  //
  public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
    // set the clear buffer color to a dark grey.
    GLES20.glClearColor(0.1f, .1f, 0.1f, 0.9f);
    // initialize the cube code for drawing.
    mCube = new Cube();
    // if we had other objects setup them up here as well.
  }

  // 
  // Draw a triangle using the shader pair created in onSurfaceCreated()
  //
  public void onDrawFrame(GL10 glUnused) {
    // Clear the color buffer  set above by glClearColor.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    // need this otherwise, it will over right stuff and the cube will look wrong!
    android.opengl.GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
    Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

    // Create a rotation and translation for the cube
    Matrix.setIdentityM(mRotationMatrix, 0);

    // mAngle is how fast, x,y,z which directions it rotates.
    // mAngleX & mAngleY taken from user
    Matrix.rotateM(mRotationMatrix, 0, mAngleY, 1.0f, 0.0f, 0.0f);
    Matrix.rotateM(mRotationMatrix, 0, mAngleX, 0.0f, 1.0f, 0.0f);
    // mAngle is constant rotation
    Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.0f, 1.0f, 1.0f);

    // scaling
    Matrix.scaleM(mRotationMatrix, 0, scale, scale, scale);

    // combine the model with the view matrix
    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);

    // combine the model-view with the projection matrix
    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

    // draw cube
    mCube.draw(mMVPMatrix);

    //change the angle, so the cube will spin.
    mAngle += .4;
  }

  //
  // Handle surface changes
  //
  public void onSurfaceChanged(GL10 glUnused, int width, int height) {
    // Set the viewport
    GLES20.glViewport(0, 0, width, height);
    float aspect = (float) width / height;

    // this projection matrix is applied to object coordinates
    // no idea why 53.13f, it was used in another example and it worked.
    Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR);
  }

  // used the touch listener to rotate the cube up/down (y) and left/right (x)
  public float getAngleX() {
    return mAngleX;
  }

  public float getAngleY() {
    return mAngleY;
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
