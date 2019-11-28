package com.example.filetransfer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class SendTaskThread extends Thread {
    private int i;
    private String ipprefix;
    ArrayList<Socket> socketslist;

    SendTaskThread(int i, String ipprefix, ArrayList<Socket> socketslist) {
        this.i = i;
        this.ipprefix = ipprefix;
        this.socketslist = socketslist;
    }

    @Override
    public void run() {
        String ip = ipprefix + i;
        try {
            SocketAddress socketAddress = new InetSocketAddress(ip, 6969);
            Socket socket = new Socket();
            socket.connect(socketAddress, 10000);
            socketslist.add(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
