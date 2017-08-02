package com.luocj.android.javanet;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class JavaNetActivity extends AppCompatActivity {

    private static final String TAG = JavaNetActivity.class.getSimpleName();

    private final int PERMISSION_REQUEST_CODE_HTTPAPACHE = 1;
    private final int MESSAGE_NETWORK_CONNECT_RESULT = 1;

    private JavaNetThread mThread;

    private Button mButton;
    private TextView mTextView1;
    private TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_net);



        mButton = (Button) findViewById(R.id.button1);
        mTextView1 = (TextView) findViewById(R.id.editText1);
        mTextView2 = (TextView) findViewById(R.id.editText2);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPermissions();
            }
        });

    }


    public void initPermissions() {
        if (checkCallingOrSelfPermission(INTERNET) == PackageManager.PERMISSION_GRANTED) {
            startNetwork();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, INTERNET}, PERMISSION_REQUEST_CODE_HTTPAPACHE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_HTTPAPACHE:
                startNetwork();
                break;
            default:
                break;
        }
    }

    private void startNetwork() {
        if (mThread == null) {
            mThread = new JavaNetThread();
        }
        mThread.start();
    }

    class JavaNetThread extends Thread {
        @Override
        public void run() {
            execute();
        }
    }

    private void execute() {
        String ret = "";
        try {
            URL url = new URL(mTextView1.getText().toString());
            URLConnection connection = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                ret += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message message = mHandler.obtainMessage(MESSAGE_NETWORK_CONNECT_RESULT);

        Bundle bundle = new Bundle();
        bundle.putString("result", ret);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETWORK_CONNECT_RESULT:
                    String result = (String) msg.getData().getString("result");
                    Toast.makeText(JavaNetActivity.this, "Getnetwork OK "+ result, Toast.LENGTH_LONG).show();
                    mTextView2.setText("");
                    mTextView2.setText(result);
            }
        }
    };
}
