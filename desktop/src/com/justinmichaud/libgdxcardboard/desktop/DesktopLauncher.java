package com.justinmichaud.libgdxcardboard.desktop;

import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DesktopLauncher {
	public static void main (String[] arg) throws IOException {
		BoofProcessor processor = new BoofProcessor();

        ServerSocket welcomeSocket = new ServerSocket(6789);
        Vector3 target = new Vector3();

        while(true)
        {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Connected to client!");
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream());

                while (!connectionSocket.isClosed() && connectionSocket.isConnected()) {
                    String output;
                    if (!processor.isHeadVisible()) output = "null";
                    else {
                        processor.getPosition(target);
                        output = target.toString();
                    }
                    outToClient.println(output);
                    outToClient.flush();
                    Thread.sleep(25);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Disconnected from client");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}
