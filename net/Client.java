package net;

import simulation.Entity;
import system.Input;
import system.Renderer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by molotov on 10/29/14.
 */
public class Client extends Network implements Runnable {

    private Renderer renderer;
    private InetAddress host;
    private int remotePort;

    protected Client(Renderer renderer, String host, int localPort, int remotePort) {
        super(localPort);

        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.remotePort = remotePort;
        this.renderer = renderer;

        new Thread(this).start();
        send("N");
    }

    @Override
    public void run() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(Input.isMouseDown()) {
                    send("I");
                }
            }
        }, 0, 10);

        super.run();
    }

    @Override
    public void handleData(DatagramPacket data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data.getData());
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            ArrayList<Entity> list = (ArrayList<Entity>) ois.readObject ();
            renderer.render(list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String dataString) {
        byte[] byteArray = dataString.getBytes();

        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, host, remotePort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}