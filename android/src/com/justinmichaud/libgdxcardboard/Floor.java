package com.justinmichaud.libgdxcardboard;

import java.util.ArrayList;

public class Floor {

    ArrayList<TexturedCube> section1 = new ArrayList<TexturedCube>();
    ArrayList<TexturedCube> section2 = new ArrayList<TexturedCube>();

    private float z = 0; //Location of boundary

    public Floor(World world) {
        {
            TexturedCube floor = new TexturedCube("floor-base", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(0, -3, 0);
            section1.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-left", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.rotation.setEulerAngles(0,0,90);
            floor.position.set(3f, 0, 0);
            section1.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-right", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(-3f, 0, 0);
            floor.rotation.setEulerAngles(0,0,90);
            section1.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-top", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(0, 3, 0);
            section1.add(floor);
        }

        {
            TexturedCube floor = new TexturedCube("floor-base", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(0, -3, 0);
            section2.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-left", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.rotation.setEulerAngles(0,0,90);
            floor.position.set(3f, 0, 0);
            section2.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-right", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(-3f, 0, 0);
            floor.rotation.setEulerAngles(0,0,90);
            section2.add(floor);
        }
        {
            TexturedCube floor = new TexturedCube("floor-top", world, 400, 0, 400, 20f, 20f,
                    world.getOrLoadTexture("floor.png"), false);
            floor.position.set(0, 3, 0);
            section2.add(floor);
        }

        world.worldObjects.addAll(section1);
        world.worldObjects.addAll(section2);
    }

    public void update() {
        z-=1.5f;
        if (z < -10) z = 390;

        System.out.println(z);

        for (TexturedCube cube : section1) {
            cube.position.z = z - 200;
            cube.update();
        }
        for (TexturedCube cube : section2) {
            cube.position.z = z + 200;
            cube.update();
        }
    }

}
