package com.jh.go;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FragmentDialogNoGroup extends DialogFragment {

    private Fragment fragment;
    SessionManager sessionManager;

    public FragmentDialogNoGroup() {
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
        String id = args.getString("id");

        // 다이얼로그 안 내용 입력
        ((TextView)view.findViewById(R.id.textTitle)).setText(Title);
        ((TextView)view.findViewById(R.id.textMessage)).setText(Message);
        ((Button)view.findViewById(R.id.btnOk)).setText(Ok);
        ((Button)view.findViewById(R.id.btnCancel)).setText(Cancel);

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("그룹나가기");

        // 나가기 버튼 클릭시
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 나갈 아이디 불러오기
                Log.d("그룹탈퇴", id);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("그룹나감", success + "");
                            // 탈퇴 완료
                            if(success) {

                                sessionManager.noGroup();
                                Intent intent = new Intent(getActivity().getApplicationContext(), Go2_0MainActivity.class);
                                HashMap<String, String> user = sessionManager.getUserDetail();
                                String profile = user.get(sessionManager.PROFILE);
                                Log.e("자동로그인프로필", profile);
                                startActivity(intent);

                                DialogFragment dialogFragment = (DialogFragment) fragment;
                                dialogFragment.dismiss();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),id + "를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                // 서버로 내 아이디 전송해서 그룹 삭제 요청
                Go3E_GroupOutRequest groupOutRequest = new Go3E_GroupOutRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(groupOutRequest);
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