package com.example.presentationremote;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    public static String SERVER_IP;
    public static final int SERVER_PORT = 10000;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    public Socket socket;

    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public void stopClient() {

        sendMessage("0");
        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;

    }

    public boolean run(String ip) {

        mRun = true;
        SERVER_IP = ip;

        try {

            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddr, SERVER_PORT);

            try {

                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
                return false;
            } finally {
                socket.close();
            }
            return true;

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            return false;
        }

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }


}