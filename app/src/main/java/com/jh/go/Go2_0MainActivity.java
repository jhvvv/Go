package com.jh.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Go2_0MainActivity extends AppCompatActivity {

    EditText etGroupCode;
    static TextView tvMyName;
    static ImageView ivMyProfile;
    LinearLayout layoutMypage;

    boolean groupOX = false;
    String getIdx, id, group_name;

    SessionManager sessionManager;

    String getProfile;
    static Bitmap bitmap;
    String imageUrl;
    ActionBar ab;
    private Menu action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go2_main);

        sessionManager = new SessionManager(this);

        ab = getSupportActionBar();

//        getSupportActionBar().hide() ;

        ab.setIcon(R.drawable.logo) ;
        ab.setDisplayUseLogoEnabled(true) ;
        ab.setDisplayShowHomeEnabled(true) ;

//        Intent getData = getIntent();
//        id = getData.getStringExtra("id");

        layoutMypage = (LinearLayout) findViewById(R.id.layoutMypage);
        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        etGroupCode = (EditText) findViewById(R.id.etGroupCode);
        tvMyName = (TextView) findViewById(R.id.tvMyName);

        // 자동로그인 체크, 저장되어있지않을시에 로그인화면으로 이동
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getIdx = user.get(sessionManager.IDX);
        id = user.get(sessionManager.ID);
        getProfile = user.get(sessionManager.PROFILE);
        tvMyName.setText(user.get(sessionManager.NAME));

        findViewById(R.id.btnNewGroup).setOnClickListener(mClickListener);
        findViewById(R.id.btnEnter).setOnClickListener(mClickListener);
//        findViewById(R.id.btnLogout).setOnClickListener(mClickListener);
//        findViewById(R.id.btnWithdrawal).setOnClickListener(mClickListener);

        layoutMypage.setOnClickListener(viewClickListener);

        Log.e("getProfile", getProfile);
        if(getProfile.equals("")) {
//            ivMyProfile.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile));
        } else {
            imageUrl = getProfile;
            Log.e("이미지url", getProfile);

            Thread Thread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(imageUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //     HttpURLConnection의 인스턴스가 될 수 있으므로 캐스팅해서 사용한다
                        //     conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        //inputStream 값 가져오기

                        bitmap = BitmapFactory.decodeStream(is);
                        // Bitmap으로 반환
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread.start();

            try{
                //join() 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다림
                Thread.join();
                ivMyProfile.setImageBitmap(bitmap);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //이미지뷰 원형으로 표시
        ivMyProfile.setBackground(new ShapeDrawable(new OvalShape()));
        ivMyProfile.setClipToOutline(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        action = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout :
                memberLogout();

                return true;

            case R.id.withdrawal :
                memberDel();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layoutMypage:
                    Intent setIntent = new Intent(Go2_0MainActivity.this, Go2_1SettingActivity.class);
                    startActivity(setIntent);
                    break;
            }
        }
    };

    Button.OnClickListener mClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.btnLogout:
//                    memberLogout();
//                    break;
//                case R.id.btnWithdrawal:
//                    memberDel();
//                    break;
                case R.id.btnNewGroup: // 그룹만들기
                    Intent intent = new Intent(Go2_0MainActivity.this, Go2GroupAddActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnEnter: // 그룹가입
                    // 공백체크
                    if(etGroupCode.getText().toString().equals("")) {
                        Toast.makeText(Go2_0MainActivity.this,"그룹코드를 입력하시오.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String group_code = etGroupCode.getText().toString();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                Log.d("그룹코드", success + "");

                                // 그룹이 존재한다면
                                if(success) {
                                    group_name = jsonObject.getString("group_name");
                                    groupOX = true;
                                }

                                if(groupOX) {
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                boolean success = jsonObject.getBoolean("success");
                                                Log.d("그룹입장", success + "");
                                                // 서버 내 정보에 그룹이 추가되었다면 그룹 입장
                                                if(success) {
                                                    sessionManager.goGroup(group_code);
                                                    Intent intentMain = new Intent(getApplicationContext(), Go3A_FragmentActivity.class);
//                                                    intentMain.putExtra("group_code", group_code);
                                                    startActivity(intentMain);
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };

                                    // 서버 내 정보에 그룹 추가 요청
                                    Go2MainGroupInRequest groupInRequest = new Go2MainGroupInRequest(id, group_code, group_name, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(Go2_0MainActivity.this);
                                    queue.add(groupInRequest);

                                } else { // 입력한 그룹코드에 해당하는 그룹이 없을경우
                                    etGroupCode.setText("");
                                    Toast.makeText(Go2_0MainActivity.this,"그룹코드를 확인하세요",Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    // 서버에 해당 그룹이 존재하는지 확인 요청
                    Go2MainGroupCodeRequest groupCodeRequest = new Go2MainGroupCodeRequest(group_code, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Go2_0MainActivity.this);
                    queue.add(groupCodeRequest);
            }
        }
    };

    // 로그아웃
    public void memberLogout() {
        // 커스텀다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(Go2_0MainActivity.this
                , R.style.AlertDialogTheme);

        View view = LayoutInflater.from(Go2_0MainActivity.this).inflate(
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
//                // 자동로그인 정보 삭제
//                SharedPreferences spLogout = getSharedPreferences(Go1LoginActivity.MyPreferences, Activity.MODE_PRIVATE);
//                SharedPreferences.Editor logOut = spLogout.edit();
//                logOut.clear();
//                logOut.commit();
//                // 로그인 화면으로 이동
//                Intent intentLogout = new Intent(Go2_0MainActivity.this, Go1LoginActivity.class);
//                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intentLogout);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Go2_0MainActivity.this
                , R.style.AlertDialogTheme);

        View view = LayoutInflater.from(Go2_0MainActivity.this).inflate(
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
                Log.d("탈퇴아이디", id);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("멤버탈퇴", success + "");
                            // 탈퇴 완료
                            if(success) {
                                // 자동로그인 정보 삭제
//                                SharedPreferences spWithdrawal = getSharedPreferences(Go1LoginActivity.MyPreferences, Activity.MODE_PRIVATE);
//                                SharedPreferences.Editor withdrawal = spWithdrawal.edit();
//                                withdrawal.clear();
//                                withdrawal.commit();
//                                // 로그인 화면으로 이동
//                                Intent intentWithdrawal = new Intent(Go2_0MainActivity.this, Go1LoginActivity.class);
//                                intentWithdrawal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intentWithdrawal);
                                sessionManager.logout();
                                alertDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(Go2_0MainActivity.this,id + "를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                // 서버로 내 아이디 전송해서 삭제 요청
                Go2_0MemberDelRequest memberDelRequest = new Go2_0MemberDelRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Go2_0MainActivity.this);
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