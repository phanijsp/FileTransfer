package com.example.filetransfer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {
    private String TAG = "TAG";
    View view;
    int sendcount = 0, receivecount = 0;
    ListView socketlistview;
    private ViewGroup mainLayout;
    ImageView imageView, sendicon, receiveicon;
    ConstraintLayout.LayoutParams layoutParams;
    ConstraintLayout constraintLayout;
    LottieAnimationView lottieanimation1, lottieanimation2;
    SocketListAdapter socketListAdapter;
    ArrayList<sockets> socketsArrayList;
    TextView searchtag, percentage;
    private int ACTIVITY_CHOOSE_FILE = 12345;
    Socket selectedSocket;
    ServerSocket scrserverSocket;
    Socket scrSocket;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar_layout);

        mainLayout = (ConstraintLayout) findViewById(R.id.cl);
        imageView = (ImageView) findViewById(R.id.imageView);
        sendicon = (ImageView) findViewById(R.id.send);
        receiveicon = (ImageView) findViewById(R.id.receive);
        lottieanimation1 = (LottieAnimationView) findViewById(R.id.loading_animation);
        lottieanimation2 = (LottieAnimationView) findViewById(R.id.loading_animation2);
        view = (View) findViewById(R.id.view);
        layoutParams = new ConstraintLayout.LayoutParams(imageView.getLayoutParams());
        constraintLayout = (ConstraintLayout) findViewById(R.id.cl);
        socketlistview = (ListView) findViewById(R.id.socketlistview);
        socketsArrayList = new ArrayList<>();
        socketListAdapter = new SocketListAdapter(getApplicationContext(), socketsArrayList);
        socketlistview.setAdapter(socketListAdapter);
        searchtag = (TextView) findViewById(R.id.searchtag);
        percentage = (TextView) findViewById(R.id.percent);

        try {
            scrserverSocket = new ServerSocket(6969);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConstraintSet set = new ConstraintSet();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                imageView.animate().y((float) 0.50 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
                sendicon.animate().y((float) 0.30 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
                receiveicon.animate().y((float) 0.70 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
                lottieanimation1.animate().y((float) 0.30 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
                lottieanimation2.animate().y((float) 0.70 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
            }
        }, 500);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.i("ACTION", String.valueOf(constraintLayout.getMeasuredHeight()));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        receiveicon.setImageResource(R.drawable.whitereceive);
                        sendicon.setImageResource(R.drawable.whitesend);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                receiveicon.setImageResource(R.drawable.receive);
                                sendicon.setImageResource(R.drawable.iconfinder_send_1608828);
                            }
                        }, 2000);
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        float yy = event.getRawY() + dY;
                        float zz = constraintLayout.getMeasuredHeight();

                        Log.i("ACTION_DOWN ", String.valueOf(yy / zz));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() + dX;
                        float y = event.getRawY() + dY;
                        float z = constraintLayout.getMeasuredHeight();

                        if (y / z > 0.67) {
                            receiveicon.setImageResource(R.drawable.receive);
                            view.animate()
                                    .y((float) 0.70 * constraintLayout.getMeasuredHeight())
                                    .setDuration(0)
                                    .start();
                            lottieanimation2.setVisibility(View.VISIBLE);
                            startRecieve();
                            socketlistview.setVisibility(View.INVISIBLE);
                            searchtag.setVisibility(View.INVISIBLE);


                        } else if (y / z < 0.33) {
                            sendicon.setImageResource(R.drawable.iconfinder_send_1608828);
                            view.animate()
                                    .y((float) 0.30 * constraintLayout.getMeasuredHeight())
                                    .setDuration(0)
                                    .start();
                            lottieanimation1.setVisibility(View.VISIBLE);
                            try{
                                scrserverSocket.close();
                                scrSocket.close();
                            }catch (Exception e){
////                                e.printStackTrace();
                            }
                            startSend();
                            searchtag.setVisibility(View.VISIBLE);
                            socketlistview.setVisibility(View.INVISIBLE);
                        } else {
                            view.animate()
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            lottieanimation1.setVisibility(View.INVISIBLE);
                            lottieanimation2.setVisibility(View.INVISIBLE);
                            receivecount = 0;
                            sendcount = 0;
                            try{
                                scrserverSocket.close();
                                scrSocket.close();
                            }catch (Exception e){
//                                e.printStackTrace();
                            }
                            socketlistview.setVisibility(View.INVISIBLE);
                            searchtag.setVisibility(View.INVISIBLE);
                        }
                        Log.i("ACTION_MOVE", x + " " + y);

                        break;
                    case MotionEvent.ACTION_CANCEL:

                    default:
                        return false;
                }
                return true;
            }
        });
        socketlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), socketsArrayList.get(position).getIp(), Toast.LENGTH_SHORT).show();
                selectedSocket = socketsArrayList.get(position).getSocket();
                SelectFile("*/*");
            }
        });

        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();
        Log.i("IPADDRESS", getLocalIpAddress());
        File filex = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FileTransfer");
        filex.mkdirs();
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            Uri uri = data.getData();
            String FilePath = uri.getPath();// should the path be here in this string
            String Filename = getFileName(uri);
            Toast.makeText(this, Filename, Toast.LENGTH_LONG).show();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                clientoserverTransmit ctst = new clientoserverTransmit(inputStream, selectedSocket, Filename);
                ctst.execute();
                percentage.setVisibility(View.VISIBLE);
            } catch (Exception e) {

            }
        }
    }

    public class clientoserverTransmit extends AsyncTask {
        private InputStream inputStream;
        private Socket socket;
        private String Filename;

        clientoserverTransmit(InputStream inputStream, Socket socket, String Filename) {
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
                        break;
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
            return null;
        }

        public void senddatatoserver(DataOutputStream dataOutputStream, InputStream inputStream) {
            try {
                int count;
                int FileSize = inputStream.available();
                int size = 0;
                float percent = 0;
                byte[] bytes = new byte[600 * 1024];
                while ((count = inputStream.read(bytes)) > 0) {
                    System.out.println("LOl im writing");
                    dataOutputStream.write(bytes, 0, count);
                    size = size + count;
                    percent = (float) size / FileSize;
                    System.out.println(percent);

                    final float finalPercent = percent;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            float value = finalPercent;
                            percentage.setText(String.valueOf((int) (value * 100)) + "%");
                        }
                    });
                }
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception e) {
//                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Object o) {
            percentage.setVisibility(View.INVISIBLE);
            imageView.animate().y((float) 0.50 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
            lottieanimation1.setVisibility(View.INVISIBLE);
            searchtag.setVisibility(View.INVISIBLE);
            socketlistview.setVisibility(View.INVISIBLE);
        }
    }
    public class serverclientreceive extends AsyncTask {
        ServerSocket serverSocket;
        Socket socket;
        serverclientreceive(ServerSocket serverSocket,Socket socket){
            this.serverSocket = serverSocket;
            this.socket = socket;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                try {
                    serverSocket = new ServerSocket(6969);
                }catch (Exception e){

                }
                socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                while(true){
                    System.out.println("I am here");
                    String message = dataInputStream.readUTF();
                    System.out.println(message);
                    if(message.equals("Im cco_client & When u are ready")){
                        dataOutputStream.writeUTF("Send File Name And Size");
                    }
                    if(message.contains("FileName")){
                        String FileName =getFileName(message);
                        dataOutputStream.writeUTF("Okay, Now you can send me data");
                        String s = recievedatafromclient(dataInputStream,getFileName(message),Integer.parseInt(getFileSize(message)));
                        if(s.equals("Done")){
                            System.out.println("Saved File");
                        }
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public String getFileName(String message){
            String[] arr = message.split(":");
            return arr[1].trim();
        }
        public String getFileSize(String message){
            String[] arr = message.split(":");
            return arr[2].trim();
        }

        public String  recievedatafromclient(DataInputStream dataInputStream, String FileName, int FileSize){

            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        percentage.setVisibility(View.VISIBLE);
                    }
                });
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FileTransfer/"+FileName);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[600*1024];
                int size=0;
                int count;

                while ((count = dataInputStream.read(bytes)) > 0) {
                    fileOutputStream.write(bytes, 0, count);
                    size = size+count;
                    final double per = ((double) size/FileSize) *100;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            percentage.setText(String.valueOf((int)per + "%"));
                            System.out.println(per);
                        }
                    });
                }
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";
        }
        @Override
        protected void onPostExecute(Object o) {
            percentage.setVisibility(View.INVISIBLE);
            imageView.animate().y((float) 0.50 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
            lottieanimation1.setVisibility(View.INVISIBLE);
            lottieanimation2.setVisibility(View.INVISIBLE);
            searchtag.setVisibility(View.INVISIBLE);
            socketlistview.setVisibility(View.INVISIBLE);
        }
    }


    public void SelectFile(String FileType) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType(FileType);
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    public String getFileName(Uri uri) {
        String fileName = "default_file_name";
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        try {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
        } catch (Exception e) {
            //handle the failure cases here
        } finally {
            returnCursor.close();
        }
        return fileName;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else {
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                } else {
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                } else {
                }
                break;
        }
    }

    public void startRecieve() {
        receivecount++;
        if (receivecount == 1) {
            Log.i("Status", "In receive");
            serverclientreceive scr = new serverclientreceive(scrserverSocket,scrSocket);
            scr.execute();
        } else {
        }

    }

    public void startSend() {
        sendcount++;
        if (sendcount == 1) {
            String ip = getLocalIpAddress();
            String[] octarr = ip.split("\\.");
            ip = octarr[0] + "." + octarr[1] + "." + octarr[2] + ".";
            Log.i("IPAD", ip);
            SendTask sendTask = new SendTask(ip, socketlistview,
                    getApplicationContext(),
                    socketListAdapter,
                    socketsArrayList,
                    searchtag, imageView,
                    constraintLayout,
                    lottieanimation1);
            sendTask.execute();
        } else {

        }
    }

}
