package com.justinmichaud.libgdxcardboard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Surface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.google.vrtoolkit.cardboard.Eye;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

public class CameraRenderer {

    private android.hardware.Camera camera;
    private Camera.CameraInfo cameraInfo;
    private final SurfaceTexture cameraPreviewTexture;
    private final int cameraTextureUnit;
    private final Mesh mesh;
    private final ShaderProgram externalShader;

    private static String externalFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "\n" +
                    "uniform samplerExternalOES u_Texture;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
                    "}\n";
    private static String externalVertexShader =
                    "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "\n" +
                    "void main() {\n" +
                    "   v_TexCoord = a_texCoord0;\n" +
                    "   gl_Position = a_position;\n" +
                    "}\n";

    private final World world;

    public CameraRenderer(final Activity activity, World world) {
        this.world = world;

        openCamera(activity);

        int[] hTex = new int[1];
        GLES20.glGenTextures ( 1, hTex, 0 );
        cameraTextureUnit = hTex[0];

        cameraPreviewTexture = new SurfaceTexture(cameraTextureUnit);
        cameraPreviewTexture.setDefaultBufferSize(500, 500);

        try {
            camera.setPreviewTexture(cameraPreviewTexture);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]
                {-1.6f*0.7f, -1.0f*0.7f, 0, 1, 1, 1, 1, 0, 1,
                  1.6f*0.7f, -1.0f*0.7f, 0, 1, 1, 1, 1, 1, 1,
                  1.6f*0.7f,  1.0f*0.7f, 0, 1, 1, 1, 1, 1, 0,
                 -1.6f*0.7f,  1.0f*0.7f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        externalShader = new ShaderProgram(externalVertexShader, externalFragmentShader);
    }

    public void openCamera(Activity activity) {
        while (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        }

        camera = android.hardware.Camera.open(0);
        cameraInfo = new Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, cameraInfo);

        int targetWidth = 500;
        android.hardware.Camera.Size size = null;

        android.hardware.Camera.Parameters param = camera.getParameters();

        for (android.hardware.Camera.Size s : param.getSupportedPreviewSizes()) {
            if (size == null || Math.abs(s.width - targetWidth)
                    < Math.abs(size.width - targetWidth)) size = s;
        }
        param.setPreviewSize(size.width,size.height);
        size = null;

        for (android.hardware.Camera.Size s : param.getSupportedPictureSizes()) {
            if (size == null || Math.abs(s.width - targetWidth)
                    < Math.abs(size.width - targetWidth)) size = s;
        }
        param.setPictureSize(size.width,size.height);

        camera.setParameters(param);
    }

    public void update() {

    }

    public void drawEye(Eye eye) {
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //Draw camera
        Gdx.gl.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                cameraTextureUnit);
        cameraPreviewTexture.updateTexImage();

        externalShader.begin();
        mesh.render(externalShader, GL20.GL_TRIANGLES);
        externalShader.end();
    }

    public void dispose() {
        camera.unlock();
    }
}
