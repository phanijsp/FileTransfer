package com.example.filetransfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SocketListAdapter extends ArrayAdapter {
private Context context;
private ArrayList<sockets> list;
    public SocketListAdapter(@NonNull Context context, @LayoutRes ArrayList<sockets> list){
        super(context,0,list);
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.socketlistview, parent, false);
        sockets currentSocket = list.get(position);
        TextView ipaddr = (TextView) listItem.findViewById(R.id.title);
        ipaddr.setText(currentSocket.getIp());
        return listItem;
    }
}
