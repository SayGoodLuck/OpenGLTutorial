package com.example.opengltutorial;

import android.opengl.GLES20;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {

  private static final String TAG = "Cube";

  private int mProgramObject;
  private final FloatBuffer mVertices;

  //initial size of the cube.  set here, so it is easier to change later.
  private static final float SIZE = 0.4f;

  //this is the initial data, which will need to translated into the mVertices variable in the constructor.
  float[] mVerticesData = new float[]{
      ////////////////////////////////////////////////////////////////////
      // FRONT
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      -SIZE, SIZE, SIZE, // top-left
      -SIZE, -SIZE, SIZE, // bottom-left
      SIZE, -SIZE, SIZE, // bottom-right
      // Triangle 2
      SIZE, -SIZE, SIZE, // bottom-right
      SIZE, SIZE, SIZE, // top-right
      -SIZE, SIZE, SIZE, // top-left
      ////////////////////////////////////////////////////////////////////
      // BACK
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      -SIZE, SIZE, -SIZE, // top-left
      -SIZE, -SIZE, -SIZE, // bottom-left
      SIZE, -SIZE, -SIZE, // bottom-right
      // Triangle 2
      SIZE, -SIZE, -SIZE, // bottom-right
      SIZE, SIZE, -SIZE, // top-right
      -SIZE, SIZE, -SIZE, // top-left

      ////////////////////////////////////////////////////////////////////
      // LEFT
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      -SIZE, SIZE, -SIZE, // top-left
      -SIZE, -SIZE, -SIZE, // bottom-left
      -SIZE, -SIZE, SIZE, // bottom-right
      // Triangle 2
      -SIZE, -SIZE, SIZE, // bottom-right
      -SIZE, SIZE, SIZE, // top-right
      -SIZE, SIZE, -SIZE, // top-left
      ////////////////////////////////////////////////////////////////////
      // RIGHT
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      SIZE, SIZE, -SIZE, // top-left
      SIZE, -SIZE, -SIZE, // bottom-left
      SIZE, -SIZE, SIZE, // bottom-right
      // Triangle 2
      SIZE, -SIZE, SIZE, // bottom-right
      SIZE, SIZE, SIZE, // top-right
      SIZE, SIZE, -SIZE, // top-left

      ////////////////////////////////////////////////////////////////////
      // TOP
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      -SIZE, SIZE, -SIZE, // top-left
      -SIZE, SIZE, SIZE, // bottom-left
      SIZE, SIZE, SIZE, // bottom-right
      // Triangle 2
      SIZE, SIZE, SIZE, // bottom-right
      SIZE, SIZE, -SIZE, // top-right
      -SIZE, SIZE, -SIZE, // top-left
      ////////////////////////////////////////////////////////////////////
      // BOTTOM
      ////////////////////////////////////////////////////////////////////
      // Triangle 1
      -SIZE, -SIZE, -SIZE, // top-left
      -SIZE, -SIZE, SIZE, // bottom-left
      SIZE, -SIZE, SIZE, // bottom-right
      // Triangle 2
      SIZE, -SIZE, SIZE, // bottom-right
      SIZE, -SIZE, -SIZE, // top-right
      -SIZE, -SIZE, -SIZE // top-left
  };

  float[] colorCyan = MyColor.cyan();
  float[] colorBlue = MyColor.blue();
  float[] colorRed = MyColor.red();
  float[] colorGray = MyColor.gray();
  float[] colorGreen = MyColor.green();
  float[] colorYellow = MyColor.yellow();

  //vertex shader code
  String vShaderStr =
      "#version 300 es \n"
          + "uniform mat4 uMVPMatrix;\n"
          + "in vec4 vPosition;\n"
          + "void main()\n"
          + "{\n"
          + "   gl_Position = uMVPMatrix * vPosition;\n"
          + "}\n";
  //fragment shader code.
  String fShaderStr =
      "#version 300 es\n"
          + "precision mediump float;\n"
          + "uniform vec4 vColor;\n"
          + "out vec4 fragColor;\n"
          + "void main()\n"
          + "{\n"
          + "fragColor = vColor;\n"
          + "}\n";

  //finally some methods
  //constructor
  public Cube() {
    //first setup the mVertices correctly.
    mVertices = ByteBuffer
        .allocateDirect(mVerticesData.length * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(mVerticesData);
    mVertices.position(0);

    //setup the shaders
    int vertexShader;
    int fragmentShader;
    int programObject;
    int[] linked = new int[1];

    // Load the vertex/fragment shaders
    vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vShaderStr);
    fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderStr);

    // Create the program object
    programObject = GLES20.glCreateProgram();

    if (programObject == 0) {
      Log.e(TAG, "So some kind of error, but what?");
      return;
    }

    GLES20.glAttachShader(programObject, vertexShader);
    GLES20.glAttachShader(programObject, fragmentShader);

    // Bind vPosition to attribute 0
    GLES20.glBindAttribLocation(programObject, 0, "vPosition");

    // Link the program
    GLES20.glLinkProgram(programObject);

    // Check the link status
    GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

    if (linked[0] == 0) {
      Log.e(TAG, "Error linking program:");
      Log.e(TAG, GLES20.glGetProgramInfoLog(programObject));
      GLES20.glDeleteProgram(programObject);
      return;
    }

    // Store the program object
    mProgramObject = programObject;

    //now everything is setup and ready to draw.
  }

  public void draw(float[] mvpMatrix) {

    // Use the program object
    GLES20.glUseProgram(mProgramObject);

    // get handle to shape's transformation matrix
    int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramObject, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");

    // get handle to fragment shader's vColor member
    int mColorHandle = GLES20.glGetUniformLocation(mProgramObject, "vColor");

    // Apply the projection and view transformation
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");

    int vertexPosIndex = 0;
    mVertices.position(vertexPosIndex);  //just in case.  We did it already though.

    //add all the points to the space, so they can be correct by the transformations.
    //would need to do this even if there were no transformations actually.
    GLES20.glVertexAttribPointer(vertexPosIndex, 3, GLES20.GL_FLOAT,
        false, 0, mVertices);
    GLES20.glEnableVertexAttribArray(vertexPosIndex);

    //Now we are ready to draw the cube finally.
    int startPos = 0;
    int verticesPerface = 6;

    //draw front face
    GLES20.glUniform4fv(mColorHandle, 1, colorBlue, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    startPos += verticesPerface;

    //draw back face
    GLES20.glUniform4fv(mColorHandle, 1, colorCyan, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    startPos += verticesPerface;

    //draw left face
    GLES20.glUniform4fv(mColorHandle, 1, colorRed, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    startPos += verticesPerface;

    //draw right face
    GLES20.glUniform4fv(mColorHandle, 1, colorGray, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    startPos += verticesPerface;

    //draw top face
    GLES20.glUniform4fv(mColorHandle, 1, colorGreen, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    startPos += verticesPerface;

    //draw bottom face
    GLES20.glUniform4fv(mColorHandle, 1, colorYellow, 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, startPos, verticesPerface);
    //last face, so no need to increment.

  }
}
