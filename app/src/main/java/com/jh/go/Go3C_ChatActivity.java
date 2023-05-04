package com.jh.go;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Go3C_ChatActivity extends AppCompatActivity {

    boolean isConnect = false;
    EditText edit1;
    Button btn1;
    LinearLayout container;
    ScrollView scroll;
    ProgressDialog pro;

    // 어플 종료시 스레드 종료
    boolean isRunning = false;
    // 소켓서버와 연결 소켓
    Socket member_socket;
    // 사용자 닉네임
    String user_nickname;
    // 소켓서버 ip
    String ip = "192.168.0.225";
    SessionManager sessionManager;
    String getName, getCode, getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go3_c_chat);

        getSupportActionBar().hide() ;

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getName = user.get(sessionManager.NAME);
        getCode = user.get(sessionManager.CODE);
        getId = user.get(sessionManager.ID);
        Log.e("check", getCode + " / " + getName);

        edit1 = (EditText) findViewById(R.id.editText);
        btn1 = (Button) findViewById(R.id.button);
        container = (LinearLayout) findViewById(R.id.container);
        scroll = (ScrollView) findViewById(R.id.scroll);

        // 이전채팅 불러오기
        Log.e("check2", getCode + " / " + getName);
        chatLoad(getCode, getName);

        // 소켓서버 접속
        if(!isConnect) {
            String nickName = getName;
            if(nickName.length() > 0 && nickName != null) {
                pro = ProgressDialog.show(this, null, "접속중입니다");
                ConnectionThread thread = new ConnectionThread();
                thread.start();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("닉네임을 입력해주세요");
                builder.setPositiveButton("확인", null);
                builder.show();
            }
        }
    }

    public void btnMethod(View v) {
        String msg = edit1.getText().toString();
        SendToServerThread thread = new SendToServerThread(member_socket, msg);
        thread.start();
    }

    class ConnectionThread extends Thread {

        // 소켓서버에 그룹코드와 이름을 전송
        // 같은 포트를 사용해도 중복되지 않는 그룹코드를 통해 각 그룹끼리 채팅 가능
        @Override
        public void run() {
            try {
                final Socket socket = new Socket(ip, 11111);

                member_socket = socket;

                String nickName = getName;
                user_nickname = nickName;

                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                // 그룹코드 송신
                dos.writeUTF(getCode);

                // 닉네임 송신
                dos.writeUTF(nickName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pro.dismiss();
                        edit1.setText("");
                        edit1.setHint("message");
                        btn1.setText("전송");

                        isConnect = true;
                        isRunning = true;

                        MessageThread thread = new MessageThread(socket);
                        thread.start();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MessageThread extends Thread {
        Socket socket;
        DataInputStream dis;

        public MessageThread(Socket socket) {
            try {
                this.socket = socket;
                InputStream is = socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    // 소켓서버로부터 데이터 수신
                    final String msg = dis.readUTF();
                    // 출력
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = new TextView(Go3C_ChatActivity.this);
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                            String[] name = msg.split("\\n");

                            if(name[0].equals(user_nickname)) {
                                // 내 대화 오른쪽정렬
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                            }
//                            if(msg.startsWith(user_nickname)) {
//                                // 내 대화 오른쪽정렬
//                                tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
////                                tv.setBackgroundColor(Color.parseColor("#FFFBCE"));
////                                tv.setBackgroundResource(R.drawable.me);
//                            }
////                            else {
////                                tv.setBackgroundColor(Color.parseColor("#D5CEFF"));
////                                tv.setBackgroundResource(R.drawable.you);
////                            }
                            tv.setText(msg);

                            container.addView(tv);

                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
                                    scroll.invalidate();
                                }
                            }, 100);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 소켓서버에 데이터를 전달하는 스레드
    class SendToServerThread extends Thread{
        Socket socket;
        String msg;
        DataOutputStream dos;

        public SendToServerThread(Socket socket, String msg){
            try{
                this.socket = socket;
                this.msg = msg;
                OutputStream os = socket.getOutputStream();
                dos = new DataOutputStream(os);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                // 소켓서버로 데이터를 보낸다.
                dos.writeUTF(msg);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edit1.setText("");
                    }
                });

                // 서버DB 저장
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("채팅저장", success + "");
                            if(success) {
                                Log.d("그룹채팅", "저장성공");
                            } else {
                                Log.d("그룹채팅", "저장실패");
                                return;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                Go3C_ChattingRequest chattingRequest = new Go3C_ChattingRequest(getCode, getId, getName, msg, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Go3C_ChatActivity.this);
                queue.add(chattingRequest);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            member_socket.close();
            isRunning = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void chatLoad(String groupCode, String myName) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("chat_load");

                    for(int i = 0; i < jsonArray.length(); i++) {

                        JSONObject chat = jsonArray.getJSONObject(i);

                        String group_code = chat.getString("group_code");

                        // 내 그룹 대화만 불러오기
                        if(group_code.equals(groupCode)) {
                            String msg = chat.getString("member_name") + "\n" + chat.getString("msg");

                            TextView tv = new TextView(Go3C_ChatActivity.this);
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                            String[] name = msg.split("\\n");

                            if(name[0].equals(myName)) {
                                // 내 대화 오른쪽정렬
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                            }

                            tv.setText(msg);

                            container.addView(tv);

                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
                                    scroll.invalidate();
                                }
                            }, 100);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("채팅로드", "서버 접속 에러 e : " + e.getMessage());
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함.
        Go3C_ChatLoadRequest chatLoadRequest = new Go3C_ChatLoadRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(Go3C_ChatActivity.this);
        queue.add(chatLoadRequest);

    }

    @Override
    public void onBackPressed() {
        // 여기다가 원하는 처리를 작성하면 된다.
        // 스레드 종료를 해야하는데
        finish();
    }
}