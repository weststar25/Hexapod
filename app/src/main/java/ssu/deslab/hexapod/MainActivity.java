package ssu.deslab.hexapod;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding mainBinding;
    ActivityConnectBinding connectBinding;
    private String robotIP;
    private int robotPort;
    private final int reqCode4HistoryActivity = 0;
    private final int reqCode4RemoteActivity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.connectBtn.setOnClickListener(onConnectBtnClick);
        mainBinding.historyBtn.setOnClickListener(onHistoryBtnClick);
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
                        startRemoteActivity(reqCode4RemoteActivity);
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
        intent.putExtra("ip", robotIP);
        intent.putExtra("port", robotPort);
        startActivityForResult(intent,reqCode);
    }
}
