package ssu.deslab.hexapod.remote.networking;

/**
 * Created by critic on 2017. 8. 2..
 */


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;

import ssu.deslab.hexapod.MainActivity;

public class ChatServer {
    private String myNickName, peerNickName;
    private static InetSocket inetSocket;

    public ChatServer(Handler h, MainActivity a) { inetSocket = new InetSocket(h, a); }
    public boolean isAvailable() {
        return inetSocket.isAvailable();
    }
    public boolean isAcknowledged() {
        return inetSocket.isAcknowledged();
    }
    public boolean connect(String hname, int hport, String myName, String peerName) {
        myNickName = myName;
        peerNickName = peerName;
        if (inetSocket.connect(hname, hport, "/who\n", peerName) == false) {
            inetSocket.getRingProgressDialog().dismiss();
            return false;
        }
        if (inetSocket.send("/nick " + myNickName + String.valueOf('\n')) == false) {
            inetSocket.getRingProgressDialog().dismiss();
            return false;
        }
        if (inetSocket.send("/who\n") == false) {
            inetSocket.getRingProgressDialog().dismiss();
            return false;
        }
        return true;
    }
    public boolean send(String msg) {
        return inetSocket.send(msg);
    }
    public char getchar(Message msg) {
        String string = (String) msg.obj; // string delivered from peer
        string = string.replaceAll("\u001B\\[[;\\d]*m", ""); // remove color codes in the line
        String peerNickNameP2 = peerNickName + ": ";
        String nicknameP2 = string.substring(0, peerNickNameP2.length());
        if (peerNickNameP2.compareTo(nicknameP2) != 0) {
            Log.d("ChatServer", "not my peer (" + nicknameP2 + ")");
            return 'Q';
        }
        return string.charAt(nicknameP2.length());
    }
    public boolean disconnect() {
        String string = "/quit\n";
        if (inetSocket.send(string) == false)
            return false;
        return inetSocket.disconnect();
    }

    public InetSocket getInetSocket() {
        return inetSocket;
    }
}
