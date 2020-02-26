package com.maedi.soft.ino.gallery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maedi.soft.ino.gallery.R;
import com.maedi.soft.ino.gallery.model.ListObject;
import com.maedi.soft.ino.gallery.utils.CommDiffCallback;
import com.maedi.soft.ino.gallery.view.CircleLayoutLinear;

public class Galley_Adt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int layoutResourceId;
    private ListObject data = null;

    public interface CommGallery_Adt{

        void setImage(String uri, View view);

        void chooseImage(int id, int position, String uri, int sequence, View v1, View v2, ListObject listObject);
    }

    private CommGallery_Adt listener;

    public Galley_Adt(Context context, int layoutResourceId, CommGallery_Adt listener, ListObject data) {
        if(layoutResourceId != 0)this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        if(null != listener)
        {
            this.listener = listener;
        }
        else
        {
            throw new RuntimeException("Exception: You must declare listener first!");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vvholder, final int position) {
        if(data.size() > 0) {
            final ViewHolder vholder = (ViewHolder) vvholder;
            ListObject lo = data.get(position);
            listener.setImage(lo.s1, vholder.image);
            if(lo.i2 > 0)
            {
                vholder.layoutChoosed.setBackgroundColor(context.getColor(R.color.blue_ocean));
                vholder.textChoosed.setTextColor(Color.WHITE);
                vholder.textChoosed.setText(String.valueOf(lo.i2));
            }
            else
            {
                vholder.layoutChoosed.setBackgroundColor(context.getColor(R.color.em_white_54));
                vholder.textChoosed.setTextColor(Color.BLACK);
                vholder.textChoosed.setText("");
            }

            vholder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.chooseImage(lo.i1, position, lo.s1, lo.i2, vholder.layoutChoosed, vholder.textChoosed, lo);
                }
            });

            vholder.layoutChoosed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.chooseImage(lo.i1, position, lo.s1, lo.i2, v, vholder.textChoosed, lo);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        CircleLayoutLinear layoutChoosed;

        TextView textChoosed;

        public ViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.item_image);
            this.layoutChoosed = (CircleLayoutLinear) view.findViewById(R.id.item_layout_choosed);
            this.textChoosed = (TextView) view.findViewById(R.id.item_text_choosed);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(ListObject files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void updateListItems(ListObject lo) {
        final CommDiffCallback diffCallback = new CommDiffCallback(this.data, lo);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
        this.data.clear();
        this.data.addAll(lo);
    }

    public void updateListByIndex(int updateIndex, ListObject lo) {
        data.set(updateIndex, lo);
        notifyItemChanged(updateIndex);
    }

}