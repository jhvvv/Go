package com.jh.go;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Go3E_GroupOutRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동
    final static private String URL = "http://jjjjjh97.ivyro.net/GO/GroupOut.php";
    private Map<String, String> map;

    public Go3E_GroupOutRequest(String member_id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("member_id", member_id);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
