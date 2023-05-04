package com.jh.go;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Context m_context;
    private ArrayList<GridItem> m_array_item;

    public GridAdapter(Context context, ArrayList<GridItem> m_array_item) {
        this.m_context = context;
        this.m_array_item = m_array_item;
    }

    @Override
    public int getCount() {
        return this.m_array_item.size();
    }

    @Override
    public Object getItem(int position) {
        return this.m_array_item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 실제 화면을 배치할때 호출되는 메서드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.grid_item, parent, false);

        ImageView ivGroupPhoto = convertView.findViewById(R.id.ivGroupPhoto);
        String imageURL = m_array_item.get(position).getItemString();
        ivGroupPhoto.setImageBitmap(m_array_item.get(position).getPhoto());

//        ivGroupPhoto.setOnClickListener(new View.OnClickListener() {
//            // 이미지 클릭하면 뭐할건ㅈㅣ
//            @Override
//            public void onClick(View v) {
//                String str = getItemString(position);
//                Toast.makeText(m_context, str, Toast.LENGTH_SHORT).show();
//            }
//        });

        return convertView;
    }

//    public void setItem(String strItem) {
//        String strGet = strItem;
//
//    }
//
//    public String getItemString(int position) {
//        return this.m_array_item.get(position).getItemString();
//    }


}
