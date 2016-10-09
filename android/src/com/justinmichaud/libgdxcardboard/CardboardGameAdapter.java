package com.justinmichaud.libgdxcardboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardBoardAndroidApplication;
import com.badlogic.gdx.backends.android.CardBoardApplicationListener;
import com.badlogic.gdx.graphics.GL20;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.opengles.GL10;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

public class CardboardGameAdapter extends CardBoardAndroidApplication
        implements CardBoardApplicationListener {

    private World world;
    private CameraRenderer cameraRenderer;
    private HeadConnection headConnection;
    public Vibrator vibrator;

    private TextView left, right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(this, config);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        FrameLayout frameLayout = new FrameLayout(this);

        left = new TextView(this);
        left.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        left.setPadding(0, metrics.heightPixels/3, metrics.widthPixels/3*2,0);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(left, params);

        right = new TextView(this);
        right.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        right.setPadding(metrics.widthPixels/3*2,metrics.heightPixels/3,0,0);
        frameLayout.addView(right, params);

        addContentView(frameLayout, params);
    }

    @Override
    public void create() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        world = new World(this);
        cameraRenderer = new CameraRenderer(this, world);
        headConnection = new HeadConnection(world);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        world.dispose();
        cameraRenderer.dispose();
    }

    @Override
    public void onNewFrame(HeadTransform paramHeadTransform) {
        if (world.getPlayerLife() < 0) {
            setHudText("Game Over!");
            return;
        }

        if (!headConnection.isConnected()) world.setGameConnectionPaused(true);

        headConnection.getPosition(world.camera.camera.position);
        if (world.isPaused()) cameraRenderer.update();
        world.update(paramHeadTransform);

        if (world.isPaused()) {
            setHudText("");
        }
    }

    @Override
    public void onDrawEye(Eye eye) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (world.getPlayerLife() < 0) return;

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (world.isPaused()) cameraRenderer.drawEye(eye);
        else world.drawEye(eye);
    }

    @Override
    public void onFinishFrame(Viewport paramViewport) {

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {
        //world.pauseTriggerForUser(); TODO until controller
        world.fire();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) onCardboardTrigger();
        return super.dispatchTouchEvent(event);
    }

    public void setHudText(final String points) {
        if (left == null || right == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                left.setText(points);
                right.setText(points);
            }
        });
    }
}
