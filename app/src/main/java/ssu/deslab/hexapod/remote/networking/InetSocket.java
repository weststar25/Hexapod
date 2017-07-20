package ssu.deslab.hexapod.remote.networking;

/**
 * Created by critic on 2017. 7. 17..
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class InetSocket {
    private String servName;
    private int servPort;
    private String reqAck, resAck;
    private Socket socket;
    private Handler hSendThread, hMainThread;
    private DataOutputStream oStream;
    private ProgressDialog ringProgressDialog;
    private Activity baseActivity; // needed for showing a ringProgressDialog
    private final int maxTimeToJoin = 3000; // 3 seconds
    private final int maxTimeToClose = 1000; // 1 second
    private final int maxPollingInterval = 1000; // 1 second
    private final int maxTimeToWait = 3000;
    private final int sleepDuration = 100; // 100 ms
    private final int maxSleepCount = maxTimeToWait / sleepDuration;
    private int serverState = 0;
    private final int flagConnecting = 1; // indicate that ConnectThread is running
    private final int flagConnected = 2; // indicate that socket is connected
    private final int flagSendRunning = 4; // indicate that SendThread is running
    private final int flagRecvRunning = 8; // indicate that RecvThread is running
    private final int ServerAvailable = (flagConnected | flagSendRunning | flagRecvRunning);
    private final int ServerUnavailable = 0;
    public int nMsgsSent = 0;
    public int nMsgsRecv = 0;

    public InetSocket(Handler h, Activity a) {
        hMainThread = h;
        baseActivity = a;
    }
    public boolean isAvailable() {
        return ((serverState & ServerAvailable) == ServerAvailable);
    }
    public boolean isAcknowledged() { // progress bar 중단지점을 알기위해 acknowledged 수신 하는 함수
        if (reqAck == null) return true;
        else return false;
    }
    public boolean connect(String hname, int hport, String req, String ack) {
        String dialog;
        Log.d("InetSocket", "connect() called in serverState = " + serverState);
        if ((serverState & flagConnecting) == flagConnecting || (serverState & flagConnected) == flagConnected)
            return false;
        if (waitForServerState(ServerUnavailable, "MainThread") == false) {
            Log.d("InetSocket", "waitForServerState(Unavailable) timed out!");
            return false;
        }
        // At this point, serverState == ServerUnavailable
        setServerStateFlag(flagConnecting);
        servName = hname;
        servPort = hport;
        reqAck = req;
        resAck = ack;
        if (reqAck == null)
            dialog = "Connecting to " + hname + ":" + hport;
        else
            dialog = "Waiting to be acknowledged from " + resAck;
        ringProgressDialog = ProgressDialog.show(baseActivity, "Please wait ...", dialog, true);
        ringProgressDialog.setCancelable(true);
        startThread(runnableConnect);
        // ConnectThread is terminated as soon as it establishes a connection to the server.
        nMsgsSent = 0;
        nMsgsRecv = 0;
        return true;
    }
    public boolean disconnect() {
        Log.d("InetSocket", "disconnect() called in serverState = " + serverState);
        if ((serverState & (flagConnecting | flagConnected)) == 0)
            return false;
        if (waitForServerState(flagConnected, "MainThread") == false) {
            Log.d("InetSocket", "waitForServerState(Connected) timed out!");
            return false;
        }
        sleep(maxTimeToClose); // we have to wait for any last string to be delivered to the server.
        //  At this point, serverState == ServerConnected or ServerAvailable.
        if ((serverState & flagConnected) == flagConnected) {
            try { socket.close(); } // as a side effect, RecvThread & SendThread are terminated.
            catch (Exception e) { e.printStackTrace(); }
        }
        return true;
    }
    public boolean send(String string) { // main thread -> send thread에 전달할 때
        if ((serverState & (flagConnecting | flagConnected)) == 0)
            return false;
        if (waitForServerState(ServerAvailable, "MainThread") == false) {
            Log.d("InetSocket", "waitForServerState(Available) timed out!");
            return false;
        }  //  At this point, serverState == ServerAvailable

        Message msg = Message.obtain();
        msg.obj = string; // 기존의 arg1은 정수형으로서 String 객체를 담을 수 없으므로 object 객체에 실어서 보냄
        // 화면 회전 시 새로 peerView를 복구 할 때 이 중간에 보내진 key값에 대한 정확한 정보를 받을 수 없어 문제가 발생하게 된다
        // 그래서 객체를 주고 받아서 저러한 문제를 해결할 수 있다
        // 그러나 현재 우리 테트리스는 자바언어로 구성되어있고 서버의 모델은 C++로 구성되어있기 때문에 객체 단위로 전달이 불가
        // Google -> Protobuf라는 클래스를 사용해서 다른 언어간의 통신을 도와주는 객체가 존재
        // 각각의 언어에서 객체를 바이트 레프리젠테이션을 할 때 공통적으로 한다면 각각 언어 간의 객체를 부를 수 있어 Protobuf가 유용하다
        // 그래서 protobuf에 맞춰서 객체를 만들어 전송하면 된다
        msg.setTarget(hSendThread);
        msg.sendToTarget();
        return true;
    }
    private Runnable runnableConnect = new Runnable() {
        @Override
        public void run() {
            try {
                SocketAddress socketAddress = new InetSocketAddress(servName, servPort);
                socket = new Socket();
                socket.connect(socketAddress, maxTimeToJoin); // If this fails, then it will raise an exception
                setServerStateFlag(flagConnected);
                startThread(runnableSend);
                startThread(runnableRecv);
            } catch (Exception e) {
                Log.d("InetSocket", "ConnectThread : connect() fails!");
                e.printStackTrace();
            }
            resetServerStateFlag(flagConnecting);
            if (reqAck == null)
                ringProgressDialog.dismiss();
        }
    };
    private Runnable runnableSend = new Runnable() {
        @Override
        public void run() {
            setServerStateFlag(flagSendRunning);
            try { oStream = new DataOutputStream(socket.getOutputStream()); }
            catch (Exception e) {  e.printStackTrace();  }
            Looper.prepare(); // The message loop starts
            hSendThread = new Handler() { // defined here to be available after the loop starts
                public void handleMessage(Message msg) {
                    try {
                        String string = (String) msg.obj;
                        oStream.writeBytes(string);
                        nMsgsSent++;
                        Log.d("SendThread", "[" + nMsgsSent + "]th message sent : " + string);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            Looper.loop(); // The message loop ends
            resetServerStateFlag(flagSendRunning);
            Log.d("InetSocket", "SendThread terminated");

            if ((serverState & flagConnected) == flagConnected) {
                try { socket.close(); }
                catch (Exception e) { e.printStackTrace(); }
                resetServerStateFlag(flagConnected);
                Log.d("InetSocket", "Socket closed");
            }
        }
    };
    private Runnable runnableRecv = new Runnable() {
        @Override
        public void run() {
            setServerStateFlag(flagRecvRunning);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String string;
                while (true) {
                    do { string = br.readLine(); } while (string.length() == 0);
                    if (reqAck != null) {
                        if (string.contains(resAck) == true) {
                            ringProgressDialog.dismiss();
                            reqAck = null;
                        }
                        else {
                            sleep(maxPollingInterval);
                            send(reqAck);
                        }
                        continue;
                    }
                    Message msg = Message.obtain();
                    msg.obj = string;
                    msg.setTarget(hMainThread);
                    msg.sendToTarget();
                    nMsgsRecv++;
                    Log.d("RecvThread", "[" + nMsgsRecv + "]th message received : " + string);
                }
            } catch (Exception e) { // abnormal close
                Log.d("InetSocket", "Socket closed abnormally");
            }
            resetServerStateFlag(flagConnected);
            hSendThread.getLooper().quit(); // to terminate SendThread
            resetServerStateFlag(flagRecvRunning);
            Log.d("InetSocket", "RecvThread terminated");
        }
    };
    private void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
    public void sleep(int time) {
        try { Thread.sleep(time); }
        catch (Exception e) { e.printStackTrace(); }
    }
    synchronized private void setServerStateFlag(int flag) { serverState = (serverState | flag); }
    synchronized private void resetServerStateFlag(int flag) { serverState = (serverState & ~flag); }
    private boolean waitForServerState(int flag, String who) {
        //Log.d("InetSocket", who + " : waitForServerState(" + flag + ") called");
        int count = 0;
        while (((serverState & flag) != flag) && count < maxSleepCount) {
            Log.d("InetSocket", who + " : waitForServerState(" + flag + "&" + serverState + ") waiting...");
            sleep(sleepDuration);
            count++;
        }
        if (((serverState & flag) == flag)) return true;
        else return false;
    }
}


