package com.jh.go;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentDialog extends DialogFragment {

    private Fragment fragment;
//    private SharedPreferences prefs;
    SessionManager sessionManager;

    public FragmentDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_fragment, container, false);

        sessionManager = new SessionManager(getActivity());

        Bundle args = getArguments();
        String Title = args.getString("Title");
        String Message = args.getString("Message");
        String Ok = args.getString("Ok");
        String Cancel = args.getString("Cancel");

        // 다이얼로그 안 내용 입력
        ((TextView)view.findViewById(R.id.textTitle)).setText(Title);
        ((TextView)view.findViewById(R.id.textMessage)).setText(Message);
        ((Button)view.findViewById(R.id.btnOk)).setText(Ok);
        ((Button)view.findViewById(R.id.btnCancel)).setText(Cancel);

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Logout");

        // 로그아웃 버튼 클릭시
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager.logout();

//                prefs = fragment.getActivity().getSharedPreferences(Go1LoginActivity.MyPreferences, Activity.MODE_PRIVATE);
//
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.clear();
//                editor.commit();

                // 로그인 화면으로 이동
//                Intent intentLogout = new Intent(fragment.getActivity(), Go1LoginActivity.class);
//                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intentLogout);
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        });

        // 취소 버튼 클릭시
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀다이얼로그 종료
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        });

        return view;
    }
}