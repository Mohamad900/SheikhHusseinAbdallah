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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class AdapterVideoFeatured extends PagerAdapter {

    private List<Video> items;
    private Context ctx;
    private DAO dao;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterVideoFeatured(Context ctx, List<Video> items) {
        this.ctx = ctx;
        this.items = items;
        dao = AppDatabase.getDb(ctx).getDAO();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Video video = items.get(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_video_featured, container, false);
        ((TextView) view.findViewById(R.id.duration)).setText(Tools.convertTextNumbersToArabic(video.Duration));
        ImageView image = view.findViewById(R.id.image);
        Tools.displayImage(ctx, image, Constant.getImageURL(video));
        boolean watched = dao.countWatched(video.VideoId) > 0;
        (view.findViewById(R.id.watched)).setVisibility(watched ? View.VISIBLE : View.GONE);

        ((View) view.findViewById(R.id.lyt_parent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, video, position);
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }
}
