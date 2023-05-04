package com.jh.go;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class Go3E_SetFragment extends Fragment implements View.OnClickListener {

    Animation ani1, ani2;
    LinearLayout LayoutFrSet;
    Button btnLogout, btnNoGroup, btnOut, btnMypage;
    SessionManager sessionManager;
    String getId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_go3_e_set, container, false);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        LayoutFrSet = (LinearLayout) view.findViewById(R.id.LayoutFrSet);

        ani1 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        ani2 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out);

        LayoutFrSet.startAnimation(ani1);

        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnNoGroup = (Button) view.findViewById(R.id.btnNoGroup);
        btnOut = (Button) view.findViewById(R.id.btnOut);
        btnMypage = (Button) view.findViewById(R.id.btnMypage);
        btnLogout.setOnClickListener(this);
        btnNoGroup.setOnClickListener(this);
        btnOut.setOnClickListener(this);
        btnMypage.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLogout:

                // 데이터를 다이얼로그로 보내는 코드

                Bundle args = new Bundle();

                args.putString("Title", "로그아웃");
                args.putString("Message", "로그아웃하시겠습니까?");
                args.putString("Ok", "로그아웃");
                args.putString("Cancel", "취소");

                //---------------------------------------.//

                FragmentDialog dialog = new FragmentDialog();

                dialog.setArguments(args); // 데이터 전달

                dialog.show(getActivity().getSupportFragmentManager(),"Logout");

                break;
            case R.id.btnNoGroup:
                // 데이터를 다이얼로그로 보내는 코드

                Bundle args2 = new Bundle();

                args2.putString("Title", "그룹나가기");
                args2.putString("Message", "정말로 나가시겠습니까?");
                args2.putString("Ok", "네");
                args2.putString("Cancel", "취소");
                args2.putString("id", getId);

                //---------------------------------------.//

                FragmentDialogNoGroup dialog2 = new FragmentDialogNoGroup();

                dialog2.setArguments(args2); // 데이터 전달

                dialog2.show(getActivity().getSupportFragmentManager(),"그룹나가기");
                break;
            case R.id.btnOut:
                // 데이터를 다이얼로그로 보내는 코드

                Bundle args3 = new Bundle();

                args3.putString("Title", "탈퇴하기");
                args3.putString("Message", "정말로 탈퇴하시겠습니까?");
                args3.putString("Ok", "탈퇴하기");
                args3.putString("Cancel", "취소");
                args3.putString("id", getId);

                //---------------------------------------.//

                FragmentDialogOut dialog3 = new FragmentDialogOut();

                dialog3.setArguments(args3); // 데이터 전달

                dialog3.show(getActivity().getSupportFragmentManager(),"탈퇴하기");
                break;
            case R.id.btnMypage:
                Intent intent = new Intent(getActivity().getApplicationContext(), Go2_1SettingActivity.class);
                startActivity(intent);
                break;

        }

    }
}