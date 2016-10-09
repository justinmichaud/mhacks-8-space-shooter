package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class EnemyProjectile extends TexturedCube {

    private float roll = 0, rollSpeed = 0;
    private Vector3 speed = new Vector3();

    public EnemyProjectile(World world, Vector3 position) {
        super("Meteor", world, 1, 1, 0, 1, 1,
                world.getOrLoadTexture("enemy_projectile.png"), true);

        this.position.set(position);
        speed.set(world.camera.camera.position);
        speed.sub(position);
        speed.nor();
        speed.scl((float) Math.random()+0.5f);
        rollSpeed = (float) Math.random()*10-5f;
    }

    @Override
    public boolean update() {
        position.add(speed);
        rotation.setFromAxis(speed, roll);
        roll += rollSpeed;

        if (!super.update() || Math.abs(position.z) > 300
                || Math.abs(position.x) > 10
                || Math.abs(position.y) > 10) return false;

        Vector3 boundingWidth = new Vector3(width, height, depth);
        boundingWidth.add(speed);
        boundingWidth.scl(1/2f);

        BoundingBox us = new BoundingBox(position.cpy().sub(boundingWidth),
                position.cpy().add(boundingWidth));

        boundingWidth.set(1, 1, 1);
        boundingWidth.scl(1/3f);
        Vector3 playerPos = world.camera.camera.position;

        BoundingBox player = new BoundingBox(playerPos.cpy().sub(boundingWidth),
                playerPos.cpy().add(boundingWidth));

        if (player.intersects(us)) {
            world.playerHit();
            world.activity.vibrator.vibrate(World.LONG_VIBRATION);
            return false;
        }

        return true;
    }
}
