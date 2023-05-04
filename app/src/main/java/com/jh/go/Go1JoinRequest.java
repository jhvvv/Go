package com.jh.go;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Go1JoinRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동
    final static private String URL = "http://jjjjjh97.ivyro.net/GO/Register.php";
    private Map<String, String> map;

    public Go1JoinRequest(String member_id, String member_pw, String member_name, String member_hp, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        Log.d("리퀘스트정보", member_id + "/" + member_pw + "/" + member_name + "/" + member_hp);
        map = new HashMap<>();
        map.put("member_id", member_id);
        map.put("member_pw", member_pw);
        map.put("member_name", member_name);
        map.put("member_hp", member_hp);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
