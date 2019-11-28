package com.example.filetransfer;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RecieveTask extends AsyncTask {
    RecieveTask(){

    }
    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            ServerSocket serverSocket = new ServerSocket(9002);
            serverSocket.setReuseAddress(true);
            Socket socket = serverSocket.accept();
            InputStream inputStream = new BufferedInputStream(socket.getInputStream());
            String FileName = "one.jpg";
            Log.i("Status", FileName);
            Log.i("Status", String.valueOf(inputStream.available()));

            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/FileTransfer/"+FileName);

            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int size = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte b[] =new byte[bufferSize];
            int bytesRead;
            do{
                fileOutputStream.write(b, 0, bufferSize);
                bytesAvailable = inputStream.available();
                float x1 =(float) size-bytesAvailable;
                float x2 = (float) size;
                float x3 = (x1/x2)*100;
                System.out.println(x3);
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = inputStream.read(b, 0, bufferSize);
                Log.i("bytes",String.valueOf(x3));
            }while(bytesRead > 0);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Log.i("Status",sStackTrace);
        }
        return null;
    }
}
