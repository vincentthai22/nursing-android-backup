package com.sevenlogics.babynursing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by cg-mayur on 3/2/17.
 */

public class CameraItemAdapter extends RecyclerView.Adapter<CameraItemAdapter.ViewHolder> implements View.OnClickListener {

    Context context;
    ItemListener mListener;


    public CameraItemAdapter(Context context, ItemListener listener) {
        this.context = context;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.camera_bottom_sheet_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (position) {
            case 0: {
                holder.txtItem.setText(context.getResources().getString(R.string.string_take_photo));
                holder.itemView.setTag(position);
                break;
            }
            case 1: {
                holder.txtItem.setText(context.getResources().getString(R.string.string_choose_from_gallery));
                holder.itemView.setTag(position);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onItemClick((int) view.getTag());
        }
    }

    public interface ItemListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;

        public ViewHolder(View itemView) {
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.txt_item);
            itemView.setOnClickListener(CameraItemAdapter.this);
        }
    }
}
