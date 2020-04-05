package com.sheikh.hussein.abdallah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterVideoRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Video> items = new ArrayList<>();
    private DAO dao;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterVideoRecent(Context context, List<Video> items) {
        this.items = items;
        ctx = context;
        dao = AppDatabase.getDb(ctx).getDAO();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView duration;
        public ImageView image;
        public TextView watched;
        public View lyt_parent;

        public MainViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            duration = v.findViewById(R.id.duration);
            image = v.findViewById(R.id.image);
            watched = v.findViewById(R.id.watched);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_recent, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MainViewHolder) {
            final Video video = items.get(position);
            MainViewHolder vItem = (MainViewHolder) holder;
            vItem.title.setText(Tools.convertTextNumbersToArabic(video.Name));
            vItem.duration.setText(Tools.convertTextNumbersToArabic(video.Duration));
            Tools.displayImage(ctx, vItem.image, Constant.getImageURL(video));
            boolean watched = dao.countWatched(video.VideoId) > 0;
            vItem.watched.setVisibility(watched ? View.VISIBLE : View.GONE);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(view, video, position);
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