package com.sheikh.hussein.abdallah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterVideo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_HEAD = 2;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    private List<Video> items = new ArrayList<>();
    private Playlist playlist;
    private DAO dao;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterVideo(Context context, RecyclerView view, List<Video> items) {
        this.items = items;
        ctx = context;
        dao = AppDatabase.getDb(ctx).getDAO();
        lastItemViewDetector(view);
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView brief;
        public TextView date;
        public TextView video_count;
        public View featured;

        public HeaderViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            brief = v.findViewById(R.id.brief);
            date = v.findViewById(R.id.date);
            video_count = v.findViewById(R.id.video_count);
            featured = v.findViewById(R.id.featured);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            vh = new OriginalViewHolder(v);
        } else if (viewType == VIEW_HEAD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_header, parent, false);
            vh = new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Video p = items.get(position);
            OriginalViewHolder v = (OriginalViewHolder) holder;
            v.title.setText(Tools.convertTextNumbersToArabic(p.Name));
            v.duration.setText(Tools.convertTextNumbersToArabic(p.Duration));
            v.date.setText(Tools.getFormattedDateSimple(p.CreatedOn));
            Tools.displayImage(ctx, v.image, Constant.getImageURL(p));
            v.featured.setVisibility(p.Featured == true ? View.VISIBLE : View.GONE);
            boolean watched = dao.countWatched(p.VideoId) > 0;
            v.watched.setVisibility(watched ? View.VISIBLE : View.GONE);
            v.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, position);
                    }
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            if (playlist == null) return;
            HeaderViewHolder v = (HeaderViewHolder) holder;
            v.title.setText(Tools.convertTextNumbersToArabic(playlist.Name));
            v.brief.setText(Tools.convertTextNumbersToArabic(playlist.Description));
            Tools.displayImage(ctx, v.image, Constant.getURLimgPlaylist(playlist.Image));
            if(playlist.CreatedOn != null)v.date.setText(Tools.getFormattedDateFull(playlist.CreatedOn));
            String nbOfVideos = String.format(ctx.getString(R.string.playlist_details_video_count), playlist.NumberOfVideos);
            v.video_count.setText(Tools.convertTextNumbersToArabic(nbOfVideos));
            if (playlist.Featured) {
                v.featured.setVisibility(View.VISIBLE);
            } else {
                v.featured.setVisibility(View.GONE);
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).list_type;
    }

    public void insertData(List<Video> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i).list_type == VIEW_PROG) {
                items.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            Video l = new Video();
            l.list_type = VIEW_PROG;
            this.items.add(l);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / Constant.VIDEO_PER_REQUEST;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}