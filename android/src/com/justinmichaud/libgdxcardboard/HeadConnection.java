package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HeadConnection extends Thread {

    public static String ip = "192.168.44.182";
    public static int port = 6789;

    private volatile boolean running = true;
    private volatile boolean requestStop = false;
    private volatile boolean isConnected = false;

    private final Vector3 position = new Vector3();
    private final World world;

    public HeadConnection(World world) {
        this.world = world;
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        while (!requestStop && running) {
            Socket conn = null;
            try {
                conn = new Socket(ip, port);
                isConnected = true;
                System.out.println("Connected to head tracker");

                BufferedReader in =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                PrintWriter out = new PrintWriter(conn.getOutputStream());

                world.setGameConnectionPaused(false);

                while (!requestStop && conn.isConnected() && !conn.isClosed()) {
                    String line = in.readLine();
                    if (line.equals("null")) {
                        world.setGameConnectionPaused(true);
                        continue;
                    }
                    world.setGameConnectionPaused(false);

                    String[] parts = line.substring(1, line.length()-1).split(",");

                    synchronized (position) {
                        position.set(Float.parseFloat(parts[0]),
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]));
                    }
                }

                if (!conn.isClosed()) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (conn != null && !conn.isClosed()) try {
                    conn.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            isConnected = false;
            System.out.println("Disconnected from head tracker");
            world.setGameConnectionPaused(true);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

    public void getPosition(Vector3 target) {
        synchronized (position) {
            target.set(position);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

}
