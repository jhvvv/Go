package com.jh.go;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Go3B_GroupMemberCustomAdapter extends RecyclerView.Adapter<Go3B_GroupMemberCustomAdapter.ViewHolder> {
    private ArrayList<Go3B_GroupMemberListItem> itemData;

    public Go3B_GroupMemberCustomAdapter(ArrayList<Go3B_GroupMemberListItem> itemData) {
        this.itemData = itemData;
    }

    public interface MyRecyclerViewClickListener {
        void onItemClicked(int position);
        void onNameClicked(int position);
        void onHpClicked(int position);
        void onItemLongClicked(int position);
        void onImageViewClicked(int position);
    }

    private Go3B_GroupMemberCustomAdapter.MyRecyclerViewClickListener mListener;

    public void setOnClickListener(Go3B_GroupMemberCustomAdapter.MyRecyclerViewClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public Go3B_GroupMemberCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_go3_b_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    //============

    @Override
    public void onBindViewHolder(@NonNull final Go3B_GroupMemberCustomAdapter.ViewHolder holder, int position) {
        Go3B_GroupMemberListItem item = itemData.get(position);
        holder.tvName.setText(item.getName());
        holder.tvHp.setText(item.getHp());
        holder.ivProfile.setImageBitmap(item.getProfile());

        // 프로필작업 필요
//        holder.tvHp.setText(item.getHp());
        // Glide.with(context).load(item.getImgPath()).into(vh.iv);

        if (mListener != null) {
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos);
                }
            });
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNameClicked(pos);
                }
            });
            holder.tvHp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onHpClicked(pos);
                }
            });
            holder.ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImageViewClicked(pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onItemLongClicked(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvHp;
        ImageView ivProfile;
        /***
         * DB에서 데이터가져와서  holder에 저장
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvHp = itemView.findViewById(R.id.tvHp);
            ivProfile = itemView.findViewById(R.id.ivProfile);

            //이미지뷰 원형으로 표시
            ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
            ivProfile.setClipToOutline(true);
        }
    }

    //리스트 삭제 이벤트
    public void remove(int position){
        try {
            itemData.remove(position);
            notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

}