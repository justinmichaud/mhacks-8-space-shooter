package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Projectile extends TexturedCube {

    private float roll = 0, rollSpeed = 0;
    private Vector3 speed = new Vector3();

    public Projectile(World world) {
        super("Meteor", world, 1, 1, 0, 1, 1,
                world.getOrLoadTexture("projectile.png"), true);

        position.set(world.camera.camera.position);
        speed.set(world.camera.forwardVector);
        speed.scl(1,-1,-1);
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

        boolean intersection = false;

        for (TexturedCube cube : world.worldObjects) {
            if (!(cube instanceof OncomingObject)) continue;
            OncomingObject m = (OncomingObject) cube;

            Vector3 boundingWidth = new Vector3(width, height, depth);
            boundingWidth.add(speed);
            boundingWidth.scl(1/2f);

            BoundingBox us = new BoundingBox(position.cpy().sub(boundingWidth),
                    position.cpy().add(boundingWidth));

            boundingWidth.set(m.width, m.height, m.depth);
            boundingWidth.add(m.speed);
            boundingWidth.scl(1/2f);

            BoundingBox oncoming = new BoundingBox(m.position.cpy().sub(boundingWidth),
                    m.position.cpy().add(boundingWidth));

            if (oncoming.intersects(us)) {
                intersection = true;
                m.handleCollideProjectile();
            }
        }

        return !intersection;
    }

}
