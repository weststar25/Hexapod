package ssu.deslab.hexapod.remote;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import ssu.deslab.hexapod.MainActivity;
import ssu.deslab.hexapod.R;
import ssu.deslab.hexapod.databinding.ActivityDestinationBinding;
import ssu.deslab.hexapod.databinding.ActivityRemoteBinding;
import ssu.deslab.hexapod.remote.networking.ChatServer;

import static java.lang.Thread.sleep;

/**
 * Created by critic on 2017. 7. 17..
 */

public class RemoteActivity extends AppCompatActivity{
    private boolean camState = false;
    private final int reqCode4MainActivity = 0;
    private int oriMapHeight = 0;
    private int robotPort;
    private String robotIP;
    private String connectResult = "Connection Success";
    private ViewGroup.LayoutParams camLayout;
    private ViewGroup.LayoutParams mapLayout;
    ActivityRemoteBinding remoteBinding;
    ActivityDestinationBinding destinationBinding;


    protected ChatServer chatServer;
    protected Handler myHandler;
    protected Handler recvHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (chatServer.isAvailable() == false) return;
            String recvMsg = chatServer.getMessage(msg);
            Log.d("recvMsg", recvMsg);
            if(recvMsg.compareTo("close") == 0)
                onBackPressed();
        }
    };

    public enum UserCommand {
        Fail(0), Start(1);
        private final int value;
        private UserCommand(int value) { this.value = value; }
        public int value() { return value; }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_remote);
        camLayout = remoteBinding.camView.getLayoutParams();
        mapLayout = remoteBinding.mapView.getLayoutParams();
        oriMapHeight = mapLayout.height;
        remoteBinding.destBtn.setOnClickListener(onDestBtnClick);
        remoteBinding.camBtn.setOnClickListener(onCamBtnClick);
        remoteBinding.saveBtn.setOnClickListener(onSaveBtnClick);

        Intent intent = getIntent();
        robotIP = intent.getStringExtra("ip");
        robotPort = intent.getIntExtra("port", 9000);
        myHandler = new Handler();
        chatServer = new ChatServer(recvHandler, RemoteActivity.this);
        executeUserCommand(UserCommand.Start);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(chatServer.isAvailable())
            chatServer.disconnect();
    }

    public View.OnClickListener onCamBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(camState == false) {
                remoteBinding.camView.setVisibility(View.VISIBLE);
                camLayout.height = oriMapHeight / 2;
                mapLayout.height = oriMapHeight / 2;
                remoteBinding.camView.setLayoutParams(camLayout);
                remoteBinding.mapView.setLayoutParams(mapLayout);
                camState = true;
            } else {
                remoteBinding.camView.setVisibility(View.GONE);
                mapLayout.height = oriMapHeight;
                remoteBinding.mapView.setLayoutParams(mapLayout);
                camState = false;
            }
        }
    };

    public View.OnClickListener onSaveBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_saved, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(RemoteActivity.this);
            builder.setTitle("Saved History"); //Dialog 제목
            builder.setIcon(android.R.drawable.ic_menu_save); // 제목옆의 아이콘 이미지(원하는 이미지 설정)
            builder.setView(dialogView);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                     그림 저장 코드 추가
                }
            }); // setPositiveButton
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(RemoteActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }); // setNegativeButton
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
            dialog.show();
        }
    };

    public View.OnClickListener onDestBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_destination, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(RemoteActivity.this);
            builder.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
            ArrayAdapter<CharSequence> directionAdapter = ArrayAdapter.createFromResource(RemoteActivity.this, R.array.directions, android.R.layout.simple_spinner_item);
            directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner dSpinner = (Spinner) dialogView.findViewById(R.id.directionSpinner);
            dSpinner.setAdapter(directionAdapter);
            builder.setTitle("Destination Robot"); //Dialog 제목
            builder.setIcon(R.drawable.ic_open_with_black_24dp); // 제목옆의 아이콘 이미지(원하는 이미지 설정)
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }); // setPositiveButton
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(RemoteActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
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
                    destinationBinding = DataBindingUtil.bind(dialogView);
                    String distance = destinationBinding.distanceTxt.getText().toString();
                    String direction = "";
                    switch (destinationBinding.directionSpinner.getSelectedItemPosition()) {
                        case 0: direction = "e"; break;
                        case 1: direction = "w"; break;
                        case 2: direction = "s"; break;
                        case 3: direction = "n"; break;
                    }
                    if(distance.length() == 0) {
                        Toast.makeText(builder.getContext(), "거리 값을 입력하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("dest", distance + ", " + direction);
                        wantToCloseDialog = true;
                        if(chatServer.send(distance + " d") == true && chatServer.send(direction + " r") == true) {
//                            remoteBinding.destBtn.setVisibility(View.GONE);
                        }
                    }
                    if(wantToCloseDialog)
                        dialog.dismiss();
                }
            });
        } // onClick
    }; // onDestBtnClick

    public void returnMainActivity(int reqCode) {
        Intent intent = new Intent(RemoteActivity.this, MainActivity.class);
        startActivityForResult(intent,reqCode);
    }

    private Runnable runnableConnect = new Runnable() {
        @Override
        public void run() {
            if(chatServer.connect(robotIP, robotPort) == false) {
                connectResult = "Connection Error!";
                executeUserCommand(UserCommand.Fail);
                return;
            }
            sleep(500);
            Toast.makeText(RemoteActivity.this, connectResult, Toast.LENGTH_LONG).show();
        }
    };

    private Runnable runnableFail = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(RemoteActivity.this, connectResult, Toast.LENGTH_LONG).show();
            returnMainActivity(reqCode4MainActivity);
        }
    };

    private void executeUserCommand(UserCommand cmd) {
        switch(cmd.value()) {
            case 0 : myHandler.post(runnableFail); break;
            case 1 : myHandler.post(runnableConnect); break;
            default : Log.d("MainActivity","Unknown User Command!"); break;
        }
    }
    public void sleep(int time) {
        try { Thread.sleep(time); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
