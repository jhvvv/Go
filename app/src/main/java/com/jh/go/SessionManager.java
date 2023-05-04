package com.jh.go;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String IDX = "IDX";
    public static final String ID = "ID";
    public static final String PW = "PW";
    public static final String PROFILE = "PROFILE";
    public static final String NAME = "NAME";
    public static final String HP = "HP";
    public static final String CODE = "CODE";
    private static final String GROUP = "GROUP_YN";


    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String idx, String id, String pw, String profile, String name, String hp, String code, Boolean groupTF) {
        editor.putBoolean(LOGIN, true);
        editor.putString(IDX, idx);
        editor.putString(ID, id);
        editor.putString(PW, pw);
        editor.putString(PROFILE, profile);
        editor.putString(NAME, name);
        editor.putString(HP, hp);
        editor.putString(CODE, code);
        editor.putBoolean(GROUP, groupTF);
        editor.apply();

    }

    public void goGroup(String code) {
        editor.putString(CODE, code);
        editor.putBoolean(GROUP, true);
        editor.apply();
    }

    public void imageIn(String profile) {
        Log.e("이미지sp", profile);
        editor.putString(PROFILE, profile);
        editor.apply();
    }

    public boolean isLoggin()
    {
        return sharedPreferences.getBoolean(LOGIN, false);
    }
    public boolean isGroup()
    {
        return sharedPreferences.getBoolean(GROUP, false);
    }

    public void checkLogin() {
        if (!this.isLoggin()) {
            Intent i = new Intent(context, Go1LoginActivity.class);
            context.startActivity(i);
            ((Go2_0MainActivity) context).finish();
        } else if (this.isGroup()) {
            Intent groupMain = new Intent(context, Go3A_FragmentActivity.class);
            context.startActivity(groupMain);
            ((Go2_0MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(IDX, sharedPreferences.getString(IDX, ""));
        user.put(ID, sharedPreferences.getString(ID, ""));
        user.put(PW, sharedPreferences.getString(PW, ""));
        user.put(PROFILE, sharedPreferences.getString(PROFILE, ""));
        user.put(NAME, sharedPreferences.getString(NAME, ""));
        user.put(HP, sharedPreferences.getString(HP, ""));
        user.put(CODE, sharedPreferences.getString(CODE, ""));

        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, Go1LoginActivity.class);
        context.startActivity(i);
//        ((Go2_0MainActivity) context).finish();
    }

    public void noGroup() {
        editor.putString(CODE, "");
        editor.putBoolean(GROUP, false);
        editor.apply();
    }
}