package ssu.deslab.hexapod;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import ssu.deslab.hexapod.databinding.ActivityMainBinding;
import ssu.deslab.hexapod.databinding.ActivityConnectBinding;
import ssu.deslab.hexapod.history.HistoryActivity;
import ssu.deslab.hexapod.remote.RemoteActivity;
import ssu.deslab.hexapod.remote.networking.InetSocket;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    ActivityConnectBinding connectBinding;
    private String robotIP;
    private String connectResult;
    private int robotPort;
    private final int reqCode4HistoryActivity = 0;
    protected InetSocket inetSocket;
    protected Handler myHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.connectBtn.setOnClickListener(onConnectBtnClick);
        mainBinding.historyBtn.setOnClickListener(onHistoryBtnClick);

        inetSocket = new InetSocket(myHandler, MainActivity.this);
    }

    public View.OnClickListener onConnectBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_connect, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Connected Robot"); //Dialog 제목
            builder.setIcon(R.drawable.link_icon); // 제목옆의 아이콘 이미지(원하는 이미지 설정)
            builder.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    connectBinding = DataBindingUtil.bind(dialogView);
                    myHandler.post(runnableStart);
                }
            }); // setPositiveButton
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }); // setNegativeButton
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
            dialog.show();
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

    private Runnable runnableStart = new Runnable() {
        @Override
        public void run() {
//            if(inetSocket.connect(robotIP, robotPort, "app", "robot") == false ||
//                    inetSocket.send("connect") == false) {
//                connectResult = "Connection Error!";
//            }
            startRemoteActivity(reqCode4HistoryActivity);
            Toast.makeText(MainActivity.this, "Connection Success", Toast.LENGTH_SHORT).show();
        }
    };

    private Runnable runnableStop = new Runnable() {
        @Override
        public void run() {
            if(inetSocket.isAvailable()) {
                inetSocket.disconnect();
            }
        }
    };
}
