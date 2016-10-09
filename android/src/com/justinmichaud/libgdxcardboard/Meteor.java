package com.justinmichaud.libgdxcardboard;

public class Meteor extends OncomingObject {

    public Meteor(World world) {
        super(world, world.getOrLoadTexture("meteor_" + ((int) (Math.random() * 8) + 1) + ".png"));
    }

    @Override
    protected void onCollidePlayer() {
        world.playerHit();
        world.activity.vibrator.vibrate(World.LONG_VIBRATION);
    }

    @Override
    protected void onCollideProjectile() {
        world.points+=100;
    }
}
