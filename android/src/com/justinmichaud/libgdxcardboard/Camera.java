package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

public class Camera {

    public final CardboardCamera camera;

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 120f;

    public final Matrix4 currentMatrix = new Matrix4();
    public final Matrix4 headMatrixInv = new Matrix4();

    private final float[] forward = new float[3];
    public final Vector3 forwardVector = new Vector3();

    public Camera() {
        camera = new CardboardCamera();
        camera.position.set(0f, 0f, 0);
        camera.lookAt(0,0,1);
        camera.near = Z_NEAR;
        camera.far = Z_FAR;
    }

    public void update(HeadTransform paramHeadTransform) {
        paramHeadTransform.getHeadView(headMatrixInv.getValues(), 0);
        headMatrixInv.inv();

        paramHeadTransform.getForwardVector(forward, 0);
        forwardVector.set(forward);

    }

    public void updateEye(Eye eye) {
        currentMatrix.set(eye.getEyeView());
        camera.setEyeViewAdjustMatrix(currentMatrix);

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        camera.setEyeProjection(new Matrix4(perspective));
        camera.update();
    }
}
