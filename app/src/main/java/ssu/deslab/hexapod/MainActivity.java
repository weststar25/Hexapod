package ssu.deslab.hexapod;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

import ssu.deslab.hexapod.databinding.ActivityMainBinding;
import ssu.deslab.hexapod.databinding.ActivityConnectBinding;
import ssu.deslab.hexapod.history.HistoryActivity;
import ssu.deslab.hexapod.remote.RemoteActivity;
import ssu.deslab.hexapod.remote.networking.ChatServer;

public class MainActivity extends AppCompatActivity implements Serializable {
    ActivityMainBinding mainBinding;
    ActivityConnectBinding connectBinding;
    private String robotIP;
    private String connectResult = "Connection Success";
    private int robotPort;
    private final int reqCode4HistoryActivity = 0;
    private final int reqCode4RemoteActivity = 1;
    protected static ChatServer chatServer;
    protected Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.connectBtn.setOnClickListener(onConnectBtnClick);
        mainBinding.historyBtn.setOnClickListener(onHistoryBtnClick);
        myHandler = new Handler();
        chatServer = new ChatServer(myHandler, MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chatServer.getInetSocket().getRingProgressDialog() != null)
            chatServer.getInetSocket().getRingProgressDialog().dismiss();
    }

    public enum UserCommand {
        NOP(-1), Fail(0), Start(1);
        private final int value;
        private UserCommand(int value) { this.value = value; }
        public int value() { return value; }
    }

    public View.OnClickListener onConnectBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_connect, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Connected Robot"); //Dialog 제목
            builder.setIcon(R.drawable.link_icon); // 제목옆의 아이콘 이미지(원하는 이미지 설정)
            builder.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }); // setPositiveButton
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }); // setNegativeButton
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    connectBinding = DataBindingUtil.bind(dialogView);
                    String ip = connectBinding.ipTxt.getText().toString();
                    String port = connectBinding.portTxt.getText().toString();
                    if(ip.length() == 0) {
                        Toast.makeText(builder.getContext(), "IP 주소를 입력하세요", Toast.LENGTH_SHORT).show();
                    } else if(port.length() == 0) {
                        Toast.makeText(builder.getContext(), "Port 번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        wantToCloseDialog = true;
                        robotIP = connectBinding.ipTxt.getText().toString();
                        robotPort = Integer.parseInt(connectBinding.portTxt.getText().toString());
                        executeUserCommand(UserCommand.Start);
                    }
                    if(wantToCloseDialog)
                        dialog.dismiss();
                }
            });
        } // onClick
    }; // onConnectBtnClick

    public View.OnClickListener onHistoryBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startHistoryActivity(reqCode4HistoryActivity);
        } // onClick
    }; // onHistoryBtnClick

    public void startHistoryActivity(int reqCode) {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivityForResult(intent,reqCode);
    }

    public void startRemoteActivity(int reqCode) {
        Intent intent = new Intent(MainActivity.this, RemoteActivity.class);
        startActivityForResult(intent,reqCode);
    }

    private Runnable runnableConnect = new Runnable() {
        @Override
        public void run() {
            if(chatServer.connect(robotIP, robotPort, "App", "Robot") == false || chatServer.send("Hi") == false) {
                connectResult = "Connection Error!";
                executeUserCommand(UserCommand.Fail);
                return;
            }
            startRemoteActivity(reqCode4RemoteActivity);
            Toast.makeText(MainActivity.this, connectResult, Toast.LENGTH_LONG).show();
        }
    };

    private Runnable runnableFail = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, connectResult, Toast.LENGTH_LONG).show();
        }
    };

    private void executeUserCommand(UserCommand cmd) {
        switch(cmd.value()) {
            case 0 : myHandler.post(runnableFail); break;
            case 1 : myHandler.post(runnableConnect); break;
            default : Log.d("MainActivity","Unknown User Command!"); break;
        }
    }

    public static ChatServer getChatServer() {
        return chatServer;
    }
}
