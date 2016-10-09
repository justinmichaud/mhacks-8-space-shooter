package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import java.util.Iterator;
import java.util.LinkedList;

public class World {

    public static final int SHORT_VIBRATION = 250;
    public static final int LONG_VIBRATION = 500;
    private static final int PLAYER_MAX_LIFE = 3;

    public final Camera camera;
    public final LinkedList<TexturedCube> worldObjects = new LinkedList<>();
    public final LinkedList<TexturedCube> newWorldObjects = new LinkedList<>();

    private final ModelBatch batch;
    private final Environment environment;
    private boolean paused = true;
    private boolean userPaused = false;
    private boolean shotsFired = false;

    private final HUD hud;
    private final Floor floor;

    public AssetManager assetManager;
    public CardboardGameAdapter activity;

    private int playerLife = PLAYER_MAX_LIFE;
    private long playerLastHit = 0;
    public int points = 0;

    public World(CardboardGameAdapter activity) {
        this.activity = activity;
        batch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new Camera();
        camera.camera.position.set(0,1,0);

        assetManager = new AssetManager();
        floor = new Floor(this);

        for (int i=0; i<5; i++) worldObjects.add(new Meteor(this));

        hud = new HUD(this);
    }

    public void update(HeadTransform paramHeadTransform) {
        assetManager.update();

        camera.update(paramHeadTransform);
        if (isPaused()) return;

        if (Math.random() < 0.05f) worldObjects.add(new Meteor(this));
        if (Math.random() < 0.05f) worldObjects.add(new Pill(this));
        if (Math.random() < 0.005f) worldObjects.add(new ElectricGrate(this));
        if (Math.random() < 0.005f) worldObjects.add(new Enemy(this));

        if (shotsFired) {
            shotsFired = false;
            worldObjects.add(new Projectile(this));
        }

        worldObjects.addAll(newWorldObjects);
        newWorldObjects.clear();

        Iterator<TexturedCube> cubeIterator = worldObjects.iterator();
        while (cubeIterator.hasNext()) {
            TexturedCube cube = cubeIterator.next();
            if (!cube.update()) {
                cubeIterator.remove();
                cube.dispose();
            }
        }

        floor.update();
        hud.update();

//        HashMap<String, Integer> objects = new HashMap<>();
//        for (TexturedCube cube : worldObjects) {
//            int amount = 0;
//            if (objects.containsKey(cube.getClass().toString()))
//                amount = objects.get(cube.getClass().toString());
//            objects.put(cube.getClass().toString(), amount + 1);
//        }
//
//        for (String s : objects.keySet()) {
//            System.out.println(s + "->" + objects.get(s));
//        }
    }

    public void drawEye(Eye eye) {
        camera.updateEye(eye);
        batch.begin(camera.camera);
        for (TexturedCube cube : worldObjects) cube.draw(batch);
        batch.end();

        hud.drawEye(eye);
    }

    public void dispose() {
        batch.dispose();
        for (TexturedCube cube : worldObjects) cube.dispose();
        assetManager.dispose();
        hud.dispose();
    }

    public boolean isPaused() {
        return paused || userPaused;
    }

    public void pauseTriggerForUser() {
        if (isPaused()) {
            if (userPaused) userPaused = false;
        }
        else userPaused = true;

        activity.vibrator.vibrate(SHORT_VIBRATION);
    }

    public void setGameConnectionPaused(boolean paused) {
        if (paused != this.paused) activity.vibrator.vibrate(SHORT_VIBRATION);
        this.paused = paused;
    }

    public Texture getOrLoadTexture(String s) {
        if (!assetManager.isLoaded(s))
            assetManager.load(s, Texture.class);

        assetManager.finishLoading();

        return assetManager.get(s);
    }

    public void fire() {
        activity.vibrator.vibrate(SHORT_VIBRATION);
        shotsFired = true;
    }

    public void playerHit() {
        if (System.nanoTime() - playerLastHit < 1e+9) return;
        playerLife--;
        playerLastHit = System.nanoTime();
    }

    public void playerLifeBonus() {
        playerLife++;
        if (playerLife > PLAYER_MAX_LIFE) playerLife = PLAYER_MAX_LIFE;
    }

    public int getPlayerLife() {
        return playerLife;
    }
}
