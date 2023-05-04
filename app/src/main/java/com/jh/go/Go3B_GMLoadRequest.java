package com.jh.go;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Go3B_GMLoadRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동
    final static private String URL = "http://jjjjjh97.ivyro.net/GO/Groupmember.php";
    private Map<String, String> map;

    public Go3B_GMLoadRequest(Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
