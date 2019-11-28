package com.example.filetransfer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

public class SendTask extends AsyncTask {
    String ipprefix;
    ListView socketListView;
    Context context;
    SocketListAdapter socketListAdapter;
    ArrayList<sockets> socketsArrayList;
    TextView searchtag;
    ImageView imageview;
    ConstraintLayout constraintLayout;
    LottieAnimationView lottieAnimationView1;

    SendTask(String ipprefix,
             ListView socketListView,
             Context context, SocketListAdapter socketListAdapter,
             ArrayList<sockets> socketsArrayList,
             TextView searchtag,
             ImageView imageview,
             ConstraintLayout constraintLayout,
             LottieAnimationView lottieAnimationView1) {
        this.ipprefix = ipprefix;
        this.socketListView = socketListView;
        this.context = context;
        this.socketListAdapter = socketListAdapter;
        this.socketsArrayList = socketsArrayList;
        this.searchtag = searchtag;
        this.imageview = imageview;
        this.constraintLayout = constraintLayout;
        this.lottieAnimationView1 = lottieAnimationView1;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        socketsArrayList.clear();
        ArrayList<SendTaskThread> threadies = new ArrayList<>();
        ArrayList<Socket> socketslist = new ArrayList<>();
        for (int i = 0; i < 255; i++) {
            SendTaskThread sendTaskThread = new SendTaskThread(i, ipprefix, socketslist);
            sendTaskThread.start();
            threadies.add(sendTaskThread);
        }
        for (int i = 0; i < 255; i++) {
            try {
                threadies.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < socketslist.size(); i++) {
            socketsArrayList.add(new sockets(socketslist.get(i), String.valueOf(socketslist.get(i).getInetAddress())));
        }


        Log.i("Socketlist", String.valueOf(socketslist.size()));
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        socketListAdapter.notifyDataSetChanged();
        if (socketsArrayList.size() < 1) {
            imageview.animate().y((float) 0.50 * constraintLayout.getMeasuredHeight()).setDuration(1000).start();
            lottieAnimationView1.setVisibility(View.INVISIBLE);
            Toast.makeText(context, "No Device Found", Toast.LENGTH_LONG).show();

        }
        searchtag.setVisibility(View.INVISIBLE);
        socketListView.setVisibility(View.VISIBLE);
    }
}
