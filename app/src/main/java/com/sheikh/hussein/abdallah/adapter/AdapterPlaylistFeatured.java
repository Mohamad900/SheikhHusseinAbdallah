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


public class AdapterPlaylistFeatured extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Playlist> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Playlist obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView date;
        public TextView video_count;
        public ImageView image;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            date = v.findViewById(R.id.date);
            video_count = v.findViewById(R.id.video_count);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterPlaylistFeatured(Context ctx, List<Playlist> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_featured, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Playlist p = items.get(position);
            vItem.name.setText(Tools.convertTextNumbersToArabic(p.Name));
            vItem.date.setText(Tools.getFormattedDate(p.CreatedOn));
            String nbOfVideos = "";
            if(p.NumberOfVideos == 1)
                 nbOfVideos = "فيديو واحد";//String.format(ctx.getString(R.string.home_playlist_video_count_1), p.NumberOfVideos);
            else if(p.NumberOfVideos == 2)
                 nbOfVideos = "فيديوهان";
            else
                nbOfVideos = String.format(ctx.getString(R.string.home_playlist_video_count), p.NumberOfVideos);

            vItem.video_count.setText(Tools.convertTextNumbersToArabic(nbOfVideos));
            Tools.displayImageThumb(ctx, vItem.image, Constant.getURLimgPlaylist(p.Image), 0.5f);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, p);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Playlist> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}