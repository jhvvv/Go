package com.jh.go;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.jh.go.Go2_0MainActivity.bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Go3D_PicFragment extends Fragment {

    private GridView m_grid;
    static ArrayList<GridItem> photoList = new ArrayList<>();
    static GridAdapter m_gridAdt;
    Button btnPhotoAdd;
    Bitmap bitmap;
    String getCode;
    SessionManager sessionManager;

    private static final String URL_GP_UPLOAD = "http://jjjjjh97.ivyro.net/GO/Pic/Photo_upload.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_go3_d_pic, container, false);

        sessionManager = new SessionManager(getActivity());

        m_grid = (GridView) v.findViewById(R.id.grid_test);
        m_gridAdt = new GridAdapter(getActivity(), photoList);
        m_grid.setAdapter(m_gridAdt);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getCode = user.get(sessionManager.CODE);

        btnPhotoAdd = (Button) v.findViewById(R.id.btnPhotoAdd);
        btnPhotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseFile();
            }
        });

//        for (int i = 0 ; i < 100 ; i++ ) {
//            String strNo = "Num : " + i;
//            m_gridAdt.setItem(strNo);
//        }

        photoLoad();

        return v;
    }

    void photoLoad() {
        photoList.clear();
        String groupCode = getCode;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("photo");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject photo = jsonArray.getJSONObject(i);
                        Go3C_PhotoData.group_code[i] = photo.getString("group_code");
                        Go3C_PhotoData.image[i] = photo.getString("image");

                        Log.e("사진", Go3C_PhotoData.group_code[i] + "/" + Go3C_PhotoData.image[i]);

                    }

                    int cnt = 0;
                    for(int i = 0; i < jsonArray.length(); i++) {
                        if(Go3C_PhotoData.group_code[i].equals(groupCode)) {
                            Go3C_PhotoData.bitmaps_image[i] = stringToBitmap(Go3C_PhotoData.image[i]);

                            photoList.add(new GridItem(Go3C_PhotoData.bitmaps_image[i]));
                        }
                    }
                    m_grid.setAdapter((m_gridAdt));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("사진목록", "서버 접속 에러 e : " + e.getMessage());
                }
            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        Go3D_PhotoLoadRequest photoLoadRequest = new Go3D_PhotoLoadRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(photoLoadRequest);
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Log.e("사진정보", filePath + "");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadPicture(getStringImage(bitmap));
        }
    }

    private void uploadPicture(final String photo) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GP_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        photoList.add(new GridItem(bitmap));
                        m_grid.setAdapter((m_gridAdt));
//                        photoLoad();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Try Again! error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("group_code", getCode);
                params.put("image", photo);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
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
}