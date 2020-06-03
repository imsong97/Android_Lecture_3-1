package com.example.bluetoothlist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class MyBluetoothService {

    private static final String NAME_SECURE = "BluetoothSecure";

    private static final UUID MY_UUID_SECURE = UUID.fromString("");
    private final BluetoothAdapter adapter;
    private int mState;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private ConnectingThread mConnectingThread;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public MyBluetoothService(Context context, Handler handler){
        adapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    public synchronized int getState(){
        return mState;
    }

    public synchronized void start(){
        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        // 현재 연결되어 동작중인 스레드가 있다면 취소
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // 연결중인 스레드가 있다면 취소
        if(mConnectingThread != null){
            mConnectingThread.cancel();
            mConnectingThread = null;
        }
    }

    public synchronized void stop(){
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if(mConnectingThread != null){
            mConnectingThread.cancel();
            mConnectingThread = null;
        }

        mState = STATE_NONE;
    }

    public synchronized void manageConnectedSocket(BluetoothSocket socket, BluetoothDevice device){
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    public synchronized void connect(BluetoothDevice device){
        // 새로운 연결을 위 서버로 연결중인 스레드 종료 필요
        if(mState == STATE_CONNECTING){
            if(mConnectedThread != null){
                mConnectingThread.cancel();
                mConnectingThread = null;
            }
        }
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectingThread = new ConnectingThread(device);
        mConnectingThread.start();
    }

    public void write(byte[] out){
        ConnectedThread r;
        synchronized (this){
            if(mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;

            while(mState != STATE_CONNECTED){
                try {
                    socket = mmServerSocket.accept();
                    mState = STATE_CONNECTED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(socket != null){
                switch (mState){
                    case STATE_LISTEN:
                        break;
                    case STATE_CONNECTED:
                        manageConnectedSocket(socket, socket.getRemoteDevice());
                        break;
                }
            }
        }

        public void cancel(){
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(mState == STATE_CONNECTED){
                try {
                    bytes = mmInStream.read(buffer);

                    // mHandler를 통해 값을 출력
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mState = STATE_NONE;
        }

        public void write(byte[] data){
            try {
                mmOutStream.write(data);

                // mHandler를 통해 UI에 적용 호출
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectingThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectingThread(BluetoothDevice device){
            mmDevice = device;

            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        @Override
        public void run() {
            adapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            // 기존 연결 해제
            synchronized (MyBluetoothService.this){
                mConnectingThread = null;
            }

            // 새로운 연결
            manageConnectedSocket(mmSocket, mmDevice);
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
