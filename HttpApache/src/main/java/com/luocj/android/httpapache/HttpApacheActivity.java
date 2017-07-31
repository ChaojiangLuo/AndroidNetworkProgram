package com.luocj.android.httpapache;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class HttpApacheActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE_HTTPAPACHE = 1;

    private final int MESSAGE_NETWORK_CONNECT_RESULT = 1;

    private HttpApacheThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_apache);

        Button button = (Button) findViewById(R.id.botton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPermissions();
            }
        });

    }

    class HttpApacheThread extends Thread {
        @Override
        public void run() {
            execute();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETWORK_CONNECT_RESULT:
                    String result = (String) msg.obj;
                    Toast.makeText(HttpApacheActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    };

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
            mThread = new HttpApacheThread();
        }
        mThread.start();
    }

    private void execute() {
        try {
            HttpApacheApplication app = (HttpApacheApplication) getApplication();

            HttpClient client = app.getHttpClient();
            HttpGet get = new HttpGet("http://192.168.1.57:8080/web/TestServlet?id=1001&name=john&age=60");
            HttpResponse response = client.execute(get);

            String result = "Network connect failed";
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                result = inStream2String(is);
            }

            Message message = mHandler.obtainMessage(MESSAGE_NETWORK_CONNECT_RESULT);
            message.obj = result;
            mHandler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            bos.write(buf, 0, len);
        }
        return new String(bos.toByteArray());
    }
}
