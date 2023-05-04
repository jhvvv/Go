package com.jh.go;

import static com.jh.go.Go2_0MainActivity.bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class Go3A_FragmentActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Go3B_MainFragment fragmentMain;
    private Go3D_PicFragment fragmentPic;
    private Go3E_SetFragment fragmentSet;
    private FragmentTransaction transaction;

    ActionBar ab;
    private Menu action;

    SessionManager sessionManager;
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go3_a_fragment);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        ab = getSupportActionBar();

        ab.setIcon(R.drawable.logo) ;
        ab.setDisplayUseLogoEnabled(true) ;
        ab.setDisplayShowHomeEnabled(true) ;


        fragmentManager = getSupportFragmentManager();

        fragmentMain = new Go3B_MainFragment();
        fragmentPic = new Go3D_PicFragment();
        fragmentSet = new Go3E_SetFragment();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentMain).commitAllowingStateLoss();

        findViewById(R.id.btn_main).setOnClickListener(mClick);
        findViewById(R.id.btn_img).setOnClickListener(mClick);
//        findViewById(R.id.btn_set).setOnClickListener(mClick);
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        public void onClick(View v) {
            transaction = fragmentManager.beginTransaction();

            switch(v.getId()) {
                case R.id.btn_main:
                    transaction.replace(R.id.frameLayout, fragmentMain).commitAllowingStateLoss();
                    break;
                case R.id.btn_img:
                    transaction.replace(R.id.frameLayout, fragmentPic).commitAllowingStateLoss();
                    break;
//                case R.id.btn_set:
//                    transaction.replace(R.id.frameLayout, fragmentSet).commitAllowingStateLoss();
//                    break;
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu2, menu);
        action = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mypage :
                Intent intent = new Intent(this.getApplicationContext(), Go2_1SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout :
                memberLogout();
                return true;
            case R.id.groupOut :
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

                dialog2.show(this.getSupportFragmentManager(),"그룹나가기");
                return true;
            case R.id.withdrawal :
                memberDel();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void memberLogout() {
        // 커스텀다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(Go3A_FragmentActivity.this
                , R.style.AlertDialogTheme);

        View view = LayoutInflater.from(Go3A_FragmentActivity.this).inflate(
                R.layout.layout_dialog_indigo,
                (LinearLayout)findViewById(R.id.layoutDialog));

        builder.setView(view);
        // 다이얼로그 안 내용 입력
        ((TextView)view.findViewById(R.id.textTitle)).setText("로그아웃");
        ((TextView)view.findViewById(R.id.textMessage)).setText("로그아웃하시겠습니까?");
        ((Button)view.findViewById(R.id.btnOk)).setText("로그아웃");
        ((Button)view.findViewById(R.id.btnCancel)).setText("취소");

        AlertDialog alertDialog = builder.create();

        // 로그아웃 버튼 클릭시
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logout();
                alertDialog.dismiss();
                finish();
            }
        });

        // 취소 버튼 클릭시
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀다이얼로그 종료
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    // 탈퇴
    public void memberDel() {
        // 커스텀다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(Go3A_FragmentActivity.this
                , R.style.AlertDialogTheme);

        View view = LayoutInflater.from(Go3A_FragmentActivity.this).inflate(
                R.layout.layout_dialog_indigo,
                (LinearLayout)findViewById(R.id.layoutDialog));

        builder.setView(view);

        // 다이얼로그 안 내용 입력
        ((TextView)view.findViewById(R.id.textTitle)).setText("  탈  퇴  ");
        ((TextView)view.findViewById(R.id.textMessage)).setText("정말로 탈퇴하시겠습니까?");
        ((Button)view.findViewById(R.id.btnOk)).setText("탈퇴하기");
        ((Button)view.findViewById(R.id.btnCancel)).setText("취소");

        AlertDialog alertDialog = builder.create();

        // 탈퇴 버튼 클릭시
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 탈퇴할 아이디 불러오기
                Log.d("탈퇴아이디", getId);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("멤버탈퇴", success + "");
                            // 탈퇴 완료
                            if(success) {
                                sessionManager.logout();
                                alertDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(Go3A_FragmentActivity.this,getId + "를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                // 서버로 내 아이디 전송해서 삭제 요청
                Go2_0MemberDelRequest memberDelRequest = new Go2_0MemberDelRequest(getId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Go3A_FragmentActivity.this);
                queue.add(memberDelRequest);
            }
        });

        // 취소 버튼 클릭시
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀다이얼로그 종료
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

}