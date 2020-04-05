package com.sheikh.hussein.abdallah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;


import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class AdapterPlaylistPick extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Playlist> filtered_items = new ArrayList<>();
    private List<Playlist> original_items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    private ItemFilter mFilter = new ItemFilter();

    public interface OnItemClickListener {
        void onItemClick(View view, Playlist obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterPlaylistPick(Context ctx, List<Playlist> items) {
        this.ctx = ctx;
        this.filtered_items = items;
        this.original_items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_pick, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Playlist p = filtered_items.get(position);
            vItem.name.setText(Tools.convertTextNumbersToArabic(p.Name));
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
        return filtered_items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Playlist> items) {
        this.filtered_items = items;
        this.original_items = items;
        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Playlist> list = original_items;
            final List<Playlist> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).Name;
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Playlist>) results.values;
            notifyDataSetChanged();
        }
    }

}