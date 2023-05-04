package com.jh.go;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Go3C_ChattingRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동
    final static private String URL = "http://jjjjjh97.ivyro.net/GO/Chat/Chatting.php";
    private Map<String, String> map;

    public Go3C_ChattingRequest(String group_code, String member_id, String member_name, String msg, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("group_code", group_code);
        map.put("member_id", member_id);
        map.put("member_name", member_name);
        map.put("msg", msg);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
