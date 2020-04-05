package com.sheikh.hussein.abdallah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterPlaylistSimple extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Playlist> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, Playlist obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterPlaylistSimple(Context context, List<Playlist> items) {
        this.items = items;
        ctx = context;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView image;
        public View lyt_parent;

        public MainViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_simple, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MainViewHolder) {
            final Playlist pl = items.get(position);
            MainViewHolder v = (MainViewHolder) holder;
            v.title.setText(Tools.convertTextNumbersToArabic(pl.Name));
            Tools.displayImage(ctx, v.image, Constant.getURLimgPlaylist(pl.Image));

            v.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(view, pl, position);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}