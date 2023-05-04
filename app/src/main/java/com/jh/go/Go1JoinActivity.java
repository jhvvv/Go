package com.jh.go;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Go1JoinActivity extends AppCompatActivity {

    EditText etSignupId, etSignupPw, etSignupName, etSignupHp;
    Button btnSignup, btnIdChk;
    TextView tvMsg;
    boolean idOk = false, check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go1_join);

        getSupportActionBar().hide() ;

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        etSignupId = (EditText) findViewById(R.id.etSignupId);
        etSignupPw = (EditText) findViewById(R.id.etSignupPw);
        etSignupName = (EditText) findViewById(R.id.etSignupName);
        etSignupHp = (EditText) findViewById(R.id.etSignupHp);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnIdChk = (Button) findViewById(R.id.btnIdChk);

        tvMsg.setVisibility(View.INVISIBLE);

        btnIdChk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(etSignupId.getText().toString().equals("")){
                    Toast.makeText(Go1JoinActivity.this,"아이디를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = etSignupId.getText().toString().trim();

                searchId(id);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String id = etSignupId.getText().toString().trim();
                String pw = etSignupPw.getText().toString().trim();
                String name = etSignupName.getText().toString().trim();
                String hp = etSignupHp.getText().toString().trim();

                if(id.equals("")){
                    Toast.makeText(Go1JoinActivity.this,"아이디를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!idOk) {
                    Toast.makeText(Go1JoinActivity.this,"아이디 중복확인을 진행하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pw.equals("")){
                    Toast.makeText(Go1JoinActivity.this,"비밀번호를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.equals("")){
                    Toast.makeText(Go1JoinActivity.this,"이름을 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(hp.equals("")){
                    Toast.makeText(Go1JoinActivity.this,"전화번호를 입력하시오.",Toast.LENGTH_SHORT).show();
                    return;
                }

                dbJoin(id, pw, name, hp);

                finish();
            }
        });
    }

    public void searchId(String joinId) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.d("아이디체크", success + "");
                    if(success) {
                        tvMsg.setText("아이디가 중복되었습니다.");
                        tvMsg.setTextColor(Color.RED);
                        etSignupId.setText("");
                        check = true;
                    } else {
                        tvMsg.setText("사용가능한 아이디입니다.");
                        tvMsg.setTextColor(Color.WHITE);
                        idOk = true;
                    }
                    tvMsg.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        Go1IdCheckRequest idCheckRequest = new Go1IdCheckRequest(joinId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Go1JoinActivity.this);
        queue.add(idCheckRequest);
    }

    public void dbJoin(String member_id, String member_pw, String member_name, String member_hp) {
        Log.d("회원가입정보", member_id + "/" + member_pw + "/" + member_name + "/" + member_hp);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.d("회원가입", success + "");
                    if(success) {
                        Toast.makeText(getApplicationContext(), member_name+" 회원가입 완료.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "회원등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        Go1JoinRequest joinRequest = new Go1JoinRequest(member_id, member_pw, member_name, member_hp, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Go1JoinActivity.this);
        queue.add(joinRequest);

    }
}