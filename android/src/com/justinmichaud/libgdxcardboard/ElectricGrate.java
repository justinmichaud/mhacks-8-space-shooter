package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.math.Vector3;

public class ElectricGrate extends TexturedCube {

    private final float speed = 0.5f;
    private int openX, openY;

    public ElectricGrate(World world) {
        this(world, (Math.random() < 0.5f? -1 : 1), (Math.random() < 0.5f? -1 : 1));
    }

    // 1 open left/top, -1 open right/bottom
    public ElectricGrate(World world, int openX, int openY) {
        super("Grate", world, 6, 6, 0, 1, 1,
                world.getOrLoadTexture("grate.png"), true);

        this.openX = openX;
        this.openY = openY;

        float angle;
        if (openX<0 && openY<0) angle=0;
        else if (openX>0 && openY<0) angle=90;
        else if (openX>0 && openY>0) angle=180;
        else angle=270;

        rotation.setEulerAngles(0,0,angle);
        position.set(0,0,100);
    }

    @Override
    public boolean update() {
        position.add(0,0,-speed);

        if (!super.update() || position.z < -5) return false;

        Vector3 playerPos = world.camera.camera.position;

        System.out.println(Math.round(playerPos.x/2f) + " " + Math.round(playerPos.y/2f));

        if ((Math.abs(Math.round(playerPos.x/2f) - openX) > 0.1f
                || Math.abs(Math.round(playerPos.y/2f) - openY) > 0.1f)
                && Math.abs(playerPos.z - position.z) < 4*speed) {
            System.out.println("Player collided with " + openX + " " + openY);
            onCollidePlayer();
            return false;
        }

        return true;
    }

    protected void onCollidePlayer() {
        world.playerHit();
        world.activity.vibrator.vibrate(World.LONG_VIBRATION);
    }
}
