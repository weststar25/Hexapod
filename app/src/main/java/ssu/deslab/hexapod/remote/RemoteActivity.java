package ssu.deslab.hexapod.remote;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
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
import ssu.deslab.hexapod.databinding.ActivityRemoteBinding;

/**
 * Created by critic on 2017. 7. 17..
 */

public class RemoteActivity extends AppCompatActivity{
    private boolean camState = false;
    private int oriMapHeight = 0;
    private ViewGroup.LayoutParams camLayout;
    private ViewGroup.LayoutParams mapLayout;
    private String savedResult = "Success Saved";
    ActivityRemoteBinding arb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arb = DataBindingUtil.setContentView(this, R.layout.activity_remote);
        camLayout = arb.camView.getLayoutParams();
        mapLayout = arb.mapView.getLayoutParams();
        oriMapHeight = mapLayout.height;
        arb.destBtn.setOnClickListener(onDestBtnClick);
        arb.camBtn.setOnClickListener(onCamBtnClick);
        arb.saveBtn.setOnClickListener(onSaveBtnClick);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // disConnect Code
    }

    public View.OnClickListener onCamBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(camState == false) {
                arb.camView.setVisibility(View.VISIBLE);
                camLayout.height = oriMapHeight / 2;
                mapLayout.height = oriMapHeight / 2;
                arb.camView.setLayoutParams(camLayout);
                arb.mapView.setLayoutParams(mapLayout);
                camState = true;
            } else {
                arb.camView.setVisibility(View.GONE);
                mapLayout.height = oriMapHeight;
                arb.mapView.setLayoutParams(mapLayout);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RemoteActivity.this);
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
//                    거리와 방향 전송 코드
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
        } // onClick
    }; // onDestBtnClick
}
