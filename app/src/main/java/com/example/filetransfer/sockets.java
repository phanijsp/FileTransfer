package com.example.filetransfer;

import java.net.Socket;

public class sockets {
    private Socket socket;
    private String ip;
    public sockets(Socket socket,String ip){
        this.socket = socket;
        this.ip = ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
