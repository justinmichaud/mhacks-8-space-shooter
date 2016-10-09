package com.justinmichaud.libgdxcardboard;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class ParticlePiece extends TexturedCube {

    private Vector3 velocity;
    private int ticks = 0;

    public ParticlePiece(World world, Texture texture, Vector3 position,
                         Vector3 velocity) {
        super("Particle Piece", world, 0.5f, 0.5f, 0, 1, 1, texture, true);
        this.position.set(position);
        this.velocity = velocity.cpy();
    }

    @Override
    public boolean update() {
        ticks++;
        if (ticks > 3*60) return false;

        position.add(velocity);

        return !(super.update() && Math.abs(position.z) > 90
                && Math.abs(position.x) > 10
                && Math.abs(position.y) > 10);
    }

}
