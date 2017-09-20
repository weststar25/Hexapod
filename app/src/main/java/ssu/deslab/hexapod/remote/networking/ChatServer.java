package ssu.deslab.hexapod.remote.networking;

/**
 * Created by critic on 2017. 8. 2..
 */


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;

import ssu.deslab.hexapod.MainActivity;
import ssu.deslab.hexapod.remote.RemoteActivity;

public class ChatServer {
    private InetSocket inetSocket;

    public ChatServer(Handler h, RemoteActivity a) { inetSocket = new InetSocket(h, a); }
    public boolean isAvailable() {
        return inetSocket.isAvailable();
    }
    public boolean connect(String hname, int hport) {
        if (inetSocket.connect(hname, hport) == false) { return false; }
        if (inetSocket.send("APP" + String.valueOf('\n')) == false) { return false; }
        if (inetSocket.send("Hello\n") == false) { return false; }
        return true;
    }
    public boolean send(String msg) {
        return inetSocket.send(msg);
    }
    public String getMessage(Message msg) {
        String string = (String) msg.obj;
        string = string.replaceAll("\u001B\\[[;\\d]*m", "");
        return string;
    }
    public boolean disconnect() {
        String string = "/quit\n";
        if (inetSocket.send(string) == false)
            return false;
        return inetSocket.disconnect();
    }
}
