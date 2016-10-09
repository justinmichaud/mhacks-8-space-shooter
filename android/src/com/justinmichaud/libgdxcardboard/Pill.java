package com.justinmichaud.libgdxcardboard;


public class Pill extends OncomingObject {

    public Pill(World world) {
        super(world, world.getOrLoadTexture("pill_green.png"));
        generousHitbox = true;
    }

    @Override
    protected void onCollidePlayer() {
        world.playerLifeBonus();
        world.activity.vibrator.vibrate(World.SHORT_VIBRATION/2);
    }

    @Override
    protected void onCollideProjectile() {

    }
}
