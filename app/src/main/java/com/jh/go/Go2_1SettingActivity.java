package com.jh.go;

import static com.jh.go.Go2_0MainActivity.ivMyProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Go2_1SettingActivity extends AppCompatActivity {

    private static final String TAG = Go2_1SettingActivity.class.getSimpleName();

    private TextView name, pw, c_pw, hp;
    private Button btn_logout, btn_photo;
    SessionManager sessionManager;
    String getIdx, getId, getCode, getProfile;
    boolean getGroupTF;

    private static final String URL_HEAD = "http://jjjjjh97.ivyro.net/GO/read_detail.php";
    private static final String URL_EDIT = "http://jjjjjh97.ivyro.net/GO/edit_detail.php";
    private static final String URL_UPLOAD = "http://jjjjjh97.ivyro.net/GO/upload.php";

    private Menu action;
    private Bitmap bitmap;
    CircleImageView profile_image;
    ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go21_setting);

        ab = getSupportActionBar();

        ab.setIcon(R.drawable.logo) ;
        ab.setDisplayUseLogoEnabled(true) ;
        ab.setDisplayShowHomeEnabled(true) ;

        sessionManager = new SessionManager(this);

        name = findViewById(R.id.name);
        pw = findViewById(R.id.pw);
        c_pw = findViewById(R.id.c_pw);
        hp = findViewById(R.id.hp);
        btn_logout = findViewById(R.id.btn_logout);
        btn_photo = findViewById(R.id.btn_photo);
        profile_image = findViewById(R.id.profile_image);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getIdx = user.get(sessionManager.IDX);
        getId = user.get(sessionManager.ID);
        getProfile = user.get(sessionManager.PROFILE);
        getCode = user.get(sessionManager.CODE);
        getGroupTF = sessionManager.isGroup();

        Log.e("getProfile", getProfile);
        if(!getProfile.equals("")) { profile_image.setImageBitmap(Go2_0MainActivity.bitmap); }
        else { profile_image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile)); }

        btn_photo.setVisibility(View.INVISIBLE);
//        btn_photo.setEnabled(false);
//        btn_photo.setTextColor(Color.WHITE);

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseFile();
            }
        });

        name.setFocusableInTouchMode(false);
        pw.setFocusableInTouchMode(false);
        c_pw.setFocusableInTouchMode(false);
        hp.setFocusableInTouchMode(false);
        name.setFocusable(false);
        pw.setFocusable(false);
        c_pw.setFocusable(false);
        hp.setFocusable(false);
    }

    private void getUserDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_HEAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e(TAG, response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("read");

                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String strName = object.getString("member_name").trim();
                            String strPw = object.getString("member_pw").trim();
                            String strHp = object.getString("member_hp").trim();

                            name.setText(strName);
                            pw.setText(strPw);
                            hp.setText(strHp);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(Go2_1SettingActivity.this, "Error Reading Detail : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(Go2_1SettingActivity.this, "Error Reading Detail : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("idx", getIdx);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit :
//                btn_photo.setEnabled(true);
//                btn_photo.setTextColor(Color.BLACK);
                btn_photo.setVisibility(View.VISIBLE);
                name.setFocusableInTouchMode(true);
                pw.setFocusableInTouchMode(true);
                c_pw.setFocusableInTouchMode(true);
                hp.setFocusableInTouchMode(true);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save :
                saveEditDetail();
                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);

                btn_photo.setVisibility(View.INVISIBLE);
                name.setFocusableInTouchMode(false);
                pw.setFocusableInTouchMode(false);
                c_pw.setFocusableInTouchMode(false);
                hp.setFocusableInTouchMode(false);
                name.setFocusable(false);
                pw.setFocusable(false);
                c_pw.setFocusable(false);
                hp.setFocusable(false);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // save
    private void saveEditDetail() {
        final String name = this.name.getText().toString().trim();
        final String pw = this.pw.getText().toString().trim();
        final String hp = this.hp.getText().toString().trim();
        final String idx = getIdx;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        Go2_0MainActivity.tvMyName.setText(name);
                        Toast.makeText(Go2_1SettingActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        sessionManager.createSession(idx, getId, pw, getProfile, name, hp, getCode, getGroupTF);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(Go2_1SettingActivity.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(Go2_1SettingActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("member_name", name);
                params.put("member_pw", pw);
                params.put("member_hp", hp);
                params.put("idx", idx);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Log.e("사진정보", filePath + "");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadPicture(getIdx, getStringImage(bitmap));
        }
    }

    private void uploadPicture(final String idx, final String photo) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        getProfile = "http://jjjjjh97.ivyro.net/GO/profile/" + getIdx + ".jpeg";
                        sessionManager.imageIn(getProfile);
                        HashMap<String, String> user = sessionManager.getUserDetail();
                        String profile = user.get(sessionManager.PROFILE);
                        Log.e("자동로그인프로필", profile);
                        ivMyProfile.setImageBitmap(bitmap);
                        progressDialog.dismiss();
                        Toast.makeText(Go2_1SettingActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(Go2_1SettingActivity.this, "Try Again! error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(Go2_1SettingActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idx", idx);
                params.put("member_profile", photo);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
    }
}