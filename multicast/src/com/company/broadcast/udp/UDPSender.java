package com.company.broadcast.udp;

import com.company.common.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender extends Thread {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;
    private Context context;

    private String UDPInetHost = "230.0.0.0";

    public UDPSender(Context context) {
        this.context = context;
    }

    public void run() {
        connectAndSend();
    }

    public void connectAndSend(){
        try {
            while(true) {
                multicast("NEW-PLAYER:5001");
                System.out.println("Sent Join me message. Will sleep now for 30 seconds");
                Thread.sleep(30 * 1000);
                if(context.gameStart) {
                    break;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Shutting Down");
        }
    }

    public void multicast(
            String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName(UDPInetHost);
        buf = multicastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
    }
}
