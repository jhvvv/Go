package com.jh.go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.InputStream;
import java.util.HashMap;

public class Go2GroupAddActivity extends AppCompatActivity {

    EditText etNewCode, etNewName;
    TextView tvCodeOverlap;

    boolean codeCheck = false;
    SessionManager sessionManager;
    String getId, getIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go2_group_add);

        getSupportActionBar().hide() ;

        sessionManager = new SessionManager(this);

        etNewCode = (EditText) findViewById(R.id.etNewCode);
        etNewName = (EditText) findViewById(R.id.etNewName);
        tvCodeOverlap = (TextView) findViewById(R.id.tvCodeOverlap);
        tvCodeOverlap.setVisibility(View.INVISIBLE);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        getIdx = user.get(sessionManager.IDX);

        findViewById(R.id.btnNewCode).setOnClickListener(mClickListener);
        findViewById(R.id.btnNewAdd).setOnClickListener(mClickListener);

    }

    Button.OnClickListener mClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNewCode:
                    // 공백체크
                    if(etNewCode.getText().toString().equals("")) {
                        Toast.makeText(Go2GroupAddActivity.this,"코드를 입력하세요",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String group_code = etNewCode.getText().toString();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                Log.d("그룹코드중복체크", success + "");
                                if(success) {
                                    etNewCode.setText("");
                                    tvCodeOverlap.setText("코드가 중복되었습니다.");
                                    tvCodeOverlap.setVisibility(View.VISIBLE);
                                    tvCodeOverlap.setTextColor(Color.RED);
                                    return;
                                } else {
                                    tvCodeOverlap.setVisibility(View.VISIBLE);
                                    tvCodeOverlap.setText(group_code + "는 사용가능합니다.");
                                    tvCodeOverlap.setTextColor(Color.WHITE);
                                    etNewCode.setEnabled(false);
                                    codeCheck = true;
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    // 서버로 Volley를 이용해서 요청을 함
                    Go2GAddGroupCodeRequest groupCodeRequest = new Go2GAddGroupCodeRequest(group_code, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Go2GroupAddActivity.this);
                    queue.add(groupCodeRequest);

                    break;
                case R.id.btnNewAdd:
                    if(!codeCheck) {
                        Toast.makeText(Go2GroupAddActivity.this, "코드 중복확인을 진행하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(etNewName.getText().toString().equals("")) {
                        Toast.makeText(Go2GroupAddActivity.this, "그룹 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // db에 등록
                    dbGroupAdd();

                    break;
            }
        }
    };

    public void dbGroupAdd() {
        String member_id = getId;
        String group_code = etNewCode.getText().toString().trim();
        String group_name = etNewName.getText().toString().trim();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.d("그룹등록", success + "");
                    if(success) {
                        sessionManager.goGroup(group_code);
                        Intent intentMain = new Intent(getApplicationContext(), Go3A_FragmentActivity.class);
                        intentMain.putExtra("group_code", group_code);
                        startActivity(intentMain);
                        finish();
                    } else {
                        Log.d("그룹등록실패", success + " 실패");
                        Toast.makeText(Go2GroupAddActivity.this, "db에 등록 실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        Go2GAddRequest groupAddRequest = new Go2GAddRequest(member_id, group_code, group_name, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(Go2GroupAddActivity.this);
        queue1.add(groupAddRequest);
    }

}