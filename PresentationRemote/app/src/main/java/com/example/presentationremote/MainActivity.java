package com.example.presentationremote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TcpClient mTcpClient;
    Button btnPrevious;
    Button btnNext;
    EditText txtIP;
    Button btnConnect;
    Button btnDisconnect;
    TextView txtInfo;
    //ListView listView;
    String ipAddress;
    TextView txtEnter;
    ImageView img;
    static ArrayList<String> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        txtIP = findViewById(R.id.txtIP);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnConnect = findViewById(R.id.btnConnect);
        txtInfo = findViewById(R.id.txtInfo);
        txtEnter = findViewById(R.id.txtEnter);
        img = findViewById(R.id.imageView);

       //new NetworkSniffTask(this.getApplicationContext()).execute();


        img.setVisibility(View.GONE);
        txtInfo.setVisibility(View.GONE);
        btnDisconnect.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnPrevious.setVisibility(View.GONE);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTcpClient.sendMessage("1");
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTcpClient.sendMessage("2");
            }
        });

      /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = arrayList.get(position);
                ipAddress = s.replace("/","");
                connect();
            }
        });*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTcpClient.stopClient();
    }

    public void connect(View view) {
        new ConnectTask().execute("");
        try {
            ipAddress = txtIP.getText().toString();
            Thread.sleep(500);
            if(mTcpClient == null || mTcpClient.socket == null) Toast.makeText(this,"Nije uspela konekcija",Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this,"Uspesna konekcija",Toast.LENGTH_SHORT).show();
                txtIP.setVisibility(View.GONE);
                btnConnect.setVisibility(View.GONE);
                txtEnter.setVisibility(View.GONE);

                txtInfo.setVisibility(View.VISIBLE);
                txtInfo.setText("Konektovani ste na " + ipAddress);
                btnDisconnect.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);



            }
        }catch (Exception e){
            Log.e("??????", e.getMessage());
        }

    }

    public void disconnect(View view) {
       try {
           mTcpClient.stopClient();
           mTcpClient = null;
           if (mTcpClient == null || mTcpClient.socket == null)
               Toast.makeText(this, "Diskonektovali ste se", Toast.LENGTH_SHORT).show();
           else Toast.makeText(this, "Nije uspela diskonekcija", Toast.LENGTH_SHORT).show();

           txtIP.setVisibility(View.VISIBLE);
           btnConnect.setVisibility(View.VISIBLE);
           txtEnter.setVisibility(View.VISIBLE);

           img.setVisibility(View.GONE);
           txtInfo.setVisibility(View.GONE);
           btnDisconnect.setVisibility(View.GONE);
           btnNext.setVisibility(View.GONE);
           btnPrevious.setVisibility(View.GONE);

       }catch (Exception e){
           Log.e("????", e.getMessage());
       }
    }


    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {

            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            mTcpClient.run(ipAddress);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
    /*private static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "NetworkSniffTask";
        private WeakReference<Context> mContextRef;

      private NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e(TAG, "Let's sniff the network");
            try {
                Context context = mContextRef.get();
                if (context != null) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = wm.getConnectionInfo();
                    int ipAddress = connectionInfo.getIpAddress();
                    String ipString = Formatter.formatIpAddress(ipAddress);
                    Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                    Log.d(TAG, "ipString: " + String.valueOf(ipString));
                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.d(TAG, "prefix: " + prefix);
                    for (int i = 0; i < 255; i++) {
                        String testIp = prefix + String.valueOf(i);
                        InetAddress name = InetAddress.getByName(testIp);
                        String hostName = name.getCanonicalHostName();
                        if (name.isReachable(1000)) {
                            Log.d(TAG, "Host:" + name);
                            arrayList.add(name.toString());
                        }
                    }
                }

            } catch (Throwable t) {
                Log.e(TAG, "Well that's not good.", t);
            }
            return null;
        }
    }*/


}
