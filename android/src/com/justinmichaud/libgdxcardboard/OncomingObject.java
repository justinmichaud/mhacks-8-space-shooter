package com.justinmichaud.libgdxcardboard;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class OncomingObject extends TexturedCube {

    public float speed = 0;
    private float roll = 0, rollSpeed = 0;
    protected boolean generousHitbox = false;

    private boolean toDestory = false;

    public OncomingObject(World world, Texture texture) {
        super("Interactive", world, 1, 1, 0, 1, 1, texture, true);
        float x = (float) Math.random()*4f-2f;
        float y = (float) Math.random()*4f-2f;
        float z = (float) Math.random()-0.5f + 200;

        position.set(x,y,z);
        speed = (float) Math.random()+0.3f;
        roll = (float) Math.random()*3-1.5f;
        rollSpeed = (float) Math.random()*2;
    }

    @Override
    public boolean update() {
        if (toDestory) {
            return false;
        }

        position.add(0,0,-speed);
        rotation.setEulerAngles(0,0,roll);
        roll += rollSpeed;

        if (!super.update() || position.z < -5) return false;

        Vector3 boundingWidth = new Vector3(width, height, depth+0.1f);
        boundingWidth.add(speed*2f);
        if (!generousHitbox) boundingWidth.scl(1/2f);

        BoundingBox us = new BoundingBox(position.cpy().sub(boundingWidth),
                position.cpy().add(boundingWidth));

        boundingWidth.set(1, 1, 1);
        boundingWidth.scl(1/3f);
        Vector3 playerPos = world.camera.camera.position;

        BoundingBox player = new BoundingBox(playerPos.cpy().sub(boundingWidth),
                playerPos.cpy().add(boundingWidth));

        if (player.intersects(us)) {
            onCollidePlayer();
            return false;
        }

        return true;
    }

    protected abstract void onCollidePlayer();
    protected abstract void onCollideProjectile();

    public void handleCollideProjectile() {
        for (int i=0; i<4; i++) {
            float angle = (float) Math.random()*360f;
            world.newWorldObjects.add(new ParticlePiece(world, texture, position,
                    new Vector3((float) Math.cos(angle), (float) Math.sin(angle), 0)));
        }
        toDestory = true;

        onCollideProjectile();
    }

}
