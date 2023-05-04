package com.jh.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Go1LoginActivity extends AppCompatActivity {

    EditText etId, etPw;
    Button btnLogin;

//    public static final String MyPreferences = "spTestLastbbcc";
//    static String loginId, loginPw, loginName, groupCode;
    boolean idCheck = false;
    boolean spId = false, spPw = false;
    boolean groupTF = false;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go1_login);

        getSupportActionBar().hide() ;

        sessionManager = new SessionManager(this);

        etId = (EditText) findViewById(R.id.etId);
        etPw = (EditText) findViewById(R.id.etPw);
        btnLogin = (Button) findViewById(R.id.btnLogin);

//        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, Activity.MODE_PRIVATE);
//
//        loginId = sharedPreferences.getString("inputId", "");
//        loginPw = sharedPreferences.getString("inputPw", "");


        findViewById(R.id.btnJoin).setOnClickListener(mClickListener);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 공백확인
                if(etId.getText().toString().trim().equals("")) {
                    Toast.makeText(Go1LoginActivity.this,"아이디를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String member_id = etId.getText().toString().trim();
                if(etPw.getText().toString().trim().equals("")) {
                    Toast.makeText(Go1LoginActivity.this,"비밀번호를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String member_pw = etPw.getText().toString().trim();
                Log.d("아디비번확인", member_id + " / " + member_pw);

                // db에서 아이디 비밀번호 확인
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("로그인", success + "");
                            if(success) {
                                String profile = "";
                                String idx = jsonObject.getString("idx").trim();
                                String id = jsonObject.getString("member_id").trim();
                                String pw = jsonObject.getString("member_pw").trim();
                                String profileTF = jsonObject.getString("member_profile");
                                String name = jsonObject.getString("member_name").trim();
                                String hp = jsonObject.getString("member_hp").trim();
                                String groupCode = jsonObject.getString("group_code").trim();
                                Log.d("로그인정보", id + " / " + pw + " / " + name + " / " + profileTF);

                                if(!profileTF.equals("")) {
                                    profile = jsonObject.getString("profile");
                                }

                                if(groupCode.equals("")) {
                                    groupTF = false;
                                } else { groupTF = true; }

                                sessionManager.createSession(idx, id, pw, profile, name, hp, groupCode, groupTF);

                                if(groupTF) {
                                    Intent intentGroup = new Intent(getApplicationContext(), Go3A_FragmentActivity.class);
                                    startActivity(intentGroup);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), Go2_0MainActivity.class);
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                    finish();
                                }
//                                spLogin(loginId, pw, member_pw, groupTF);
                            } else {
                                etId.setText("");
                                etPw.setText("");
                                Toast.makeText(getApplicationContext(), "ID, PW 확인하세요", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                Go1LoginRequest loginRequest = new Go1LoginRequest(member_id, member_pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Go1LoginActivity.this);
                queue.add(loginRequest);
//                Go1LoginSPIdCheckRequest idCheckRequest1 = new Go1LoginSPIdCheckRequest(member_id, responseListener);
//                RequestQueue queue = Volley.newRequestQueue(Go1LoginActivity.this);
//                queue.add(idCheckRequest1);

            }
        });

//        // 아이디비번 db연동 ( + 가입그룹유무 확인 )
//        if(!loginId.equals("") && !loginPw.equals("")) {
//
//            Response.Listener<String> responseListener = new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean success = jsonObject.getBoolean("success");
//                        Log.d("자동로그인", success + "");
//                        if(success) {
//                            String pw = jsonObject.getString("member_pw");
//                            groupCode = jsonObject.getString("group_code");
//                            loginName = jsonObject.getString("member_name");
//                            Log.d("로그인정보", loginId + " / " + pw + " / " + loginName + " / " + groupCode);
//                            spId = true;
//                            if(groupCode.equals("")) {
//                                groupTF = false;
//                            } else { groupTF = true; }
//
//                            if(pw.equals(loginPw)) { spPw =  true; }
//                        } else { spId = false; }
//
//                        spLoad();
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            };
//
//            // 서버로 Volley를 이용해서 요청을 함
//            Go1LoginSPLoadRequest loginSPLoadRequest = new Go1LoginSPLoadRequest(loginId, responseListener);
//            RequestQueue queue = Volley.newRequestQueue(Go1LoginActivity.this);
//            queue.add(loginSPLoadRequest);
//
//        } else if(loginId.equals("") && loginPw.equals("")) {
//            btnLogin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // 공백확인
//                    if(etId.getText().toString().equals("")) {
//                        Toast.makeText(Go1LoginActivity.this,"아이디를 입력하시오.",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    String member_id = etId.getText().toString();
//                    if(etPw.getText().toString().equals("")) {
//                        Toast.makeText(Go1LoginActivity.this,"비밀번호를 입력하시오.",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    String member_pw = etPw.getText().toString();
//
//                    // db에서 아이디 찾고, 비밀번호 불러오기
//                    Response.Listener<String> responseListener = new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                boolean success = jsonObject.getBoolean("success");
//                                Log.d("아이디체크", success + "");
//                                if(success) {
//                                    loginId = jsonObject.getString("member_id");
//                                    String pw = jsonObject.getString("member_pw");
//                                    loginName = jsonObject.getString("member_name");
//                                    groupCode = jsonObject.getString("group_code");
//                                    Log.d("로그인정보", loginId + " / " + pw + " / " + loginName);
//                                    idCheck = true;
//                                    if(groupCode.equals("")) {
//                                        groupTF = false;
//                                    } else { groupTF = true; }
//                                    spLogin(loginId, pw, member_pw, groupTF);
//                                } else {
//                                    idCheck = false;
//                                    spLogin("", "", member_pw, groupTF);
//                                }
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    };
//
//                    // 서버로 Volley를 이용해서 요청을 함
//                    Go1LoginSPIdCheckRequest idCheckRequest1 = new Go1LoginSPIdCheckRequest(member_id, responseListener);
//                    RequestQueue queue = Volley.newRequestQueue(Go1LoginActivity.this);
//                    queue.add(idCheckRequest1);
//
//                }
//            });
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10 :
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) { //사용자가 허가 했다면
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 사용 가능", Toast.LENGTH_SHORT).show();
                } else {//거부했다면
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 제한", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Button.OnClickListener mClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnJoin:
                    Intent intent2 = new Intent(Go1LoginActivity.this, Go1JoinActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    };

//    public void spLoad() {
//        if(spId && spPw) {
//            Log.d("group", groupTF + "");
//            if(groupTF) {
//                Intent intentGroup = new Intent(getApplicationContext(), Go3A_FragmentActivity.class);
//                startActivity(intentGroup);
//                finish();
//            } else {
//                Intent intentMain = new Intent(getApplicationContext(), Go2_0MainActivity.class);
//                intentMain.putExtra("id", loginId);
//                startActivity(intentMain);
//                finish();
//            }
//        } else {
//            loginId = "";
//            loginPw = "";
//            Intent intentLogin = new Intent(getApplicationContext(), Go1LoginActivity.class);
//            startActivity(intentLogin);
//            finish();
//        }
//    }

//    public void spLogin(String id, String pw, String member_pw, boolean groupTF) {
//        // 아이디가 존재한다면 비밀번호 비교 후 자동로그인
//        // 아이디가 존재하지 않는다면 초기화, 아이디가 존재하지 않는다는 토스트 띄우기
//        // 아이디가 존재하나, 자동로그인이 되지 않았다면 비밀번호를 확인하라는 토스트 띄우기
//        if(idCheck && member_pw.equals(pw)) {
//            SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, Activity.MODE_PRIVATE);
//
//            SharedPreferences.Editor autoLogin = sharedPreferences.edit();
//
//            autoLogin.putString("inputId", id);
//            autoLogin.putString("inputPw", pw);
//
//            autoLogin.commit();
//
//            Toast.makeText(getApplicationContext(), etId.getText().toString()+"님 환영합니다.", Toast.LENGTH_SHORT).show();
//
//            if(groupTF) {
//                Intent intentGroup = new Intent(getApplicationContext(), Go3A_FragmentActivity.class);
//                startActivity(intentGroup);
//                finish();
//            } else {
//                Intent intent = new Intent(getApplicationContext(), Go2_0MainActivity.class);
//                intent.putExtra("id", id);
//                startActivity(intent);
//                finish();
//            }
//
//        } else if(!idCheck) {
//            etId.setText("");
//            etPw.setText("");
//            Toast.makeText(getApplicationContext(), "아이디를 확인하세요", Toast.LENGTH_SHORT).show();
//        } else {
//            etId.setText("");
//            etPw.setText("");
//            Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
//        }
//    }
}