package com.example.filetransfer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

public class posthandshake extends AppCompatActivity {
    Socket socket;
    ImageView fileselect;
    private int ACTIVITY_CHOOSE_FILE = 12345;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posthandshake);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar_layout);

        socket = SendTaskListSelectHelper.socket;
        fileselect = (ImageView) findViewById(R.id.fileSelect);
        fileselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFile("*/*");
            }
        });

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

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            Uri uri = data.getData();
            String FilePath = uri.getPath();// should the path be here in this string
            String Filename = getFileName(uri);
            Toast.makeText(this, Filename, Toast.LENGTH_LONG).show();
            try {
                File f= new File(uri.toString());
                InputStream inputStream = getContentResolver().openInputStream(uri);
                TransmissionClientToServer transmissionClientToServer = new TransmissionClientToServer(inputStream,socket,Filename);
                transmissionClientToServer.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public void senddatatoserver(DataOutputStream dataOutputStream,InputStream inputStream){
        byte[] bytearr = new byte[1024];
        int tyre=1024;
        try {
            int filesize = inputStream.available();
            int count = 0 ;
            while(inputStream.available()!=0){
                tyre = inputStream.read(bytearr, 0, Math.min(1024, tyre));
                System.out.println((float)(filesize-inputStream.available())/filesize*100);
                dataOutputStream.write(bytearr);
                count = count +tyre;
                System.out.println(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    }
