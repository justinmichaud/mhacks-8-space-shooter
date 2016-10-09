package com.justinmichaud.libgdxcardboard;

public class Enemy extends OncomingObject {

    public Enemy(World world) {
        super(world, world.getOrLoadTexture("enemy.png"));
    }

    @Override
    public boolean update() {
        if (canShoot()) world.newWorldObjects.add(new EnemyProjectile(world, position));
        return super.update();
    }

    private boolean canShoot() {
        for (TexturedCube cube : world.worldObjects) {
            if (cube instanceof EnemyProjectile) return false;
        }
        return true;
    }

    @Override
    protected void onCollidePlayer() {
        world.playerHit();
        world.activity.vibrator.vibrate(World.LONG_VIBRATION);
    }

    @Override
    protected void onCollideProjectile() {
        world.points+=500;
    }
}
