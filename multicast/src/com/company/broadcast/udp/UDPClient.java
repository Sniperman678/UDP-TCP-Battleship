package com.company.broadcast.udp;

import com.company.common.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPClient extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    private Context context;
    public int portNumber = 0;

    public UDPClient(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
            Thread.sleep(5 * 1000);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            System.out.println("Received Message" + received);
            portNumber = Integer.parseInt(received.split(":")[1]);
            System.out.println("Host:" + address.getHostAddress() + " Port:" + portNumber);
            synchronized (context.lock) {
                context.gameStart = true;
                context.hostIP = address.getHostAddress();
                context.portNumber = portNumber;
                context.lock.notifyAll();
            }
            socket.leaveGroup(group);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        socket.close();
    }
}
