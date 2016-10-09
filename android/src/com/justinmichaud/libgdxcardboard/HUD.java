package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.google.vrtoolkit.cardboard.Eye;

public class HUD {

    public static final float iconWidth = 0.2f;
    
    private final World world;
    private final ModelBatch modelBatch;
    private final TexturedCube heartModel;
    private final TexturedCube viewfinderModel;
    private final com.badlogic.gdx.graphics.Camera camera = new com.badlogic.gdx.graphics.OrthographicCamera();

    public HUD(World world) {
        this.world = world;
        this.modelBatch = new ModelBatch();

        Texture tex = world.getOrLoadTexture("life.png");
        heartModel = new TexturedCube("lifeIcon", world, iconWidth,
                iconWidth*tex.getHeight()/tex.getWidth(),0,1,1, tex, true);

        tex = world.getOrLoadTexture("view.png");
        viewfinderModel = new TexturedCube("viewfinder", world, iconWidth,
                iconWidth*tex.getHeight()/tex.getWidth(),0,1,1, tex, true);
    }

    public void update() {
        String points = Integer.toString(world.points);
        while (points.length() < 6) points = "0" + points;
        world.activity.setHudText(points);
    }

    public void drawEye(Eye eye) {
        modelBatch.begin(camera);

        for (int i=0; i<world.getPlayerLife(); i++) {
            heartModel.position.set(i*(iconWidth + 0.05f)
                    - (world.getPlayerLife()*(iconWidth + 0.05f))/2f + 0.05f, 0.7f, 0);
            heartModel.update();
            heartModel.draw(modelBatch);
        }

        Matrix4 eyeView = new Matrix4(eye.getEyeView());
        Vector3 translate = new Vector3();
        eyeView.getTranslation(translate);

        viewfinderModel.position.set(translate);
        viewfinderModel.draw(modelBatch);

        modelBatch.end();
    }

    public void dispose() {
        modelBatch.dispose();
        heartModel.dispose();
    }
}
