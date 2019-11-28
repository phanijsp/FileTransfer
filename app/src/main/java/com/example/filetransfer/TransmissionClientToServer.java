package com.example.filetransfer;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

public class TransmissionClientToServer extends AsyncTask {
    private InputStream inputStream;
    private Socket socket;
    private String Filename;
    TransmissionClientToServer(InputStream inputStream, Socket socket, String Filename){
        this.inputStream = inputStream;
        this.socket = socket;
        this.Filename = Filename;
    }
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("Im cco_client & When u are ready");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                String message = dataInputStream.readUTF();
                if (message.equals("Send File Name And Size")) {
                    dataOutputStream.writeUTF("FileName : " + Filename + " : " + inputStream.available());
                }
                if (message.equals("Okay, Now you can send me data")) {
                    senddatatoserver(dataOutputStream, inputStream);
                    dataOutputStream.writeUTF("Im cco_client & When u are ready");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void senddatatoserver(DataOutputStream dataOutputStream,InputStream inputStream){
        try {
            int count;
            byte[] bytes = new byte[16*1024];
            while ((count = inputStream.read(bytes)) > 0) {
                System.out.println("LOl im writing");
                dataOutputStream.write(bytes, 0, count);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
