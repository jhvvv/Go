package com.jh.go;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Go3B_MainFragment extends Fragment implements Go3B_GroupMemberCustomAdapter.MyRecyclerViewClickListener {

    static RecyclerView groupRecycler;
    static ArrayList<Go3B_GroupMemberListItem> dataList = new ArrayList<>();
    static final Go3B_GroupMemberCustomAdapter adapter = new Go3B_GroupMemberCustomAdapter(dataList);

    Button btnChat;

    SessionManager sessionManager;
    String getName, getCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_go3_b_main, container, false);

        sessionManager = new SessionManager(getActivity());

        HashMap<String, String> user = sessionManager.getUserDetail();
        getName = user.get(sessionManager.NAME);
        getCode = user.get(sessionManager.CODE);

        btnChat = (Button) v.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Go3C_ChatActivity.class);
                startActivity(intent);
            }
        });

        groupRecycler = (RecyclerView) v.findViewById(R.id.groupRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        groupRecycler.setLayoutManager(layoutManager);

        groupRecycler.setAdapter(adapter);
        adapter.setOnClickListener(this);

        selectAll();
        return v;
    }

    public void selectAll() {
        dataList.clear();
        String groupCode = getCode;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("members");
                     for(int i = 0; i < jsonArray.length(); i++) {
                          JSONObject member = jsonArray.getJSONObject(i);
                           Go3B_MemberData.member_profile[i] = member.getString("member_profile");
                           Go3B_MemberData.member_name[i] = member.getString("member_name");
                           Go3B_MemberData.member_hp[i] = member.getString("member_hp");
                           Go3B_MemberData.group_code[i] = member.getString("group_code");
                           Log.e("url", Go3B_MemberData.member_profile[i]);

                     }

                     int cnt = 0;
                     for(int i = 0; i < jsonArray.length(); i++) {
                         if(Go3B_MemberData.group_code[i].equals(groupCode) && Go3B_MemberData.member_name[i].equals(getName)) {
                             if(!Go3B_MemberData.member_profile[i].equals("")) {
                                 Go3B_MemberData.bitmaps_profile[i] = stringToBitmap(Go3B_MemberData.member_profile[i]);
                             } else {
                                 Go3B_MemberData.bitmaps_profile[i] = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.profile);
                             }
                             dataList.add(new Go3B_GroupMemberListItem(Go3B_MemberData.bitmaps_profile[i], Go3B_MemberData.member_name[i], Go3B_MemberData.member_hp[i]));
                             break;
                         }
                     }
                     for(int i = 0; i < jsonArray.length(); i++) {
                         if(Go3B_MemberData.group_code[i].equals(groupCode) && !Go3B_MemberData.member_name[i].equals(getName)) {
                             if(!Go3B_MemberData.member_profile[i].equals("")) {
                                 Go3B_MemberData.bitmaps_profile[i] = stringToBitmap(Go3B_MemberData.member_profile[i]);
                             } else {
                                 Go3B_MemberData.bitmaps_profile[i] = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.profile);
                             }
                             dataList.add(new Go3B_GroupMemberListItem(Go3B_MemberData.bitmaps_profile[i], Go3B_MemberData.member_name[i], Go3B_MemberData.member_hp[i]));
                         }
                     }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            groupRecycler.setAdapter(adapter);
                        }
                    },1300);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("그룹멤버목록", "서버 접속 에러 e : " + e.getMessage());
                }
            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        Go3B_GMLoadRequest gmLoadRequest = new Go3B_GMLoadRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(gmLoadRequest);

    }

    Bitmap stringToBitmap(String imageUrl) {
        Log.e("url확인", imageUrl);
        final Bitmap[] bitmapProfile = new Bitmap[1];

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

                    bitmapProfile[0] = BitmapFactory.decodeStream(is);
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
            return bitmapProfile[0];
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }

    }

    // 나 눌렀을 때만 마이페이지로 이동 후 수정가능하게 함
    @Override
    public void onItemClicked(int position) {
        Log.e("position", position + "");
        if(position == 0) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Go2_1SettingActivity.class);
            startActivity(intent);
        } else{
            String hp = "tel:" + dataList.get(position).getHp();
            Intent intentCall = new Intent(Intent.ACTION_VIEW, Uri.parse(hp));
            startActivity(intentCall);
        }
    }

    @Override
    public void onNameClicked(int position) {
        Log.e("position", position + "");
        if(position == 0) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Go2_1SettingActivity.class);
            startActivity(intent);
        } else{
            String hp = "tel:" + dataList.get(position).getHp();
            Intent intentCall = new Intent(Intent.ACTION_VIEW, Uri.parse(hp));
            startActivity(intentCall);
        }
    }

    @Override
    public void onHpClicked(int position) {
        Log.e("position", position + "");
        if(position == 0) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Go2_1SettingActivity.class);
            startActivity(intent);
        } else{
            String hp = "tel:" + dataList.get(position).getHp();
            Intent intentCall = new Intent(Intent.ACTION_VIEW, Uri.parse(hp));
            startActivity(intentCall);
        }
    }

    @Override
    public void onItemLongClicked(int position) {

    }

    @Override
    public void onImageViewClicked(int position) {
        Log.e("position", position + "");
        if(position == 0) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Go2_1SettingActivity.class);
            startActivity(intent);
        } else{
            String hp = "tel:" + dataList.get(position).getHp();
            Intent intentCall = new Intent(Intent.ACTION_VIEW, Uri.parse(hp));
            startActivity(intentCall);
        }
    }
}