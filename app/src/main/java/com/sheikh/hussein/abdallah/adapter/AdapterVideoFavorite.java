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
import com.sheikh.hussein.abdallah.room.table.VideoEntity;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterVideoFavorite extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;

    private List<VideoEntity> items = new ArrayList<>();

    private DAO dao;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, VideoEntity obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterVideoFavorite(Context context, List<VideoEntity> items) {
        this.items = items;
        ctx = context;
        dao = AppDatabase.getDb(ctx).getDAO();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public TextView duration;
        public TextView featured;
        public TextView watched;
        public ImageView image;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            featured = v.findViewById(R.id.featured);
            watched = v.findViewById(R.id.watched);
            duration = v.findViewById(R.id.duration);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final VideoEntity p = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            vItem.title.setText(Tools.convertTextNumbersToArabic(p.getName()));
            vItem.duration.setText(Tools.convertTextNumbersToArabic(p.getDuration()));
            vItem.date.setText(Tools.getFormattedDateSimple2(p.getSavedDate()));
            Tools.displayImage(ctx, vItem.image, Constant.getImageURL(new Video(p.getImage(),p.getUrl())));
            vItem.featured.setVisibility(View.GONE);
            boolean watched = dao.countWatched(p.getId()) > 0;
            vItem.watched.setVisibility(watched ? View.VISIBLE : View.GONE);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, position);
                    }
                }
            });
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    public void setItems(List<VideoEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

}