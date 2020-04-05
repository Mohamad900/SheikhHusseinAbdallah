package com.sheikh.hussein.abdallah.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.activity.ActivityVideoDetails;
import com.sheikh.hussein.abdallah.adapter.AdapterVideoFavorite;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.room.table.VideoEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentFavorite extends Fragment {

    private View root_view;

    private DAO dao;
    private ShimmerFrameLayout shimmer_fav;
    private RecyclerView recycler_view;
    private AdapterVideoFavorite mAdapter;

    private ThisApplication application;
    private List<VideoEntity> items;
    private int click_position = -1;


    public FragmentFavorite() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_favorite, container, false);
        application = ThisApplication.getInstance();
        dao = AppDatabase.getDb(getActivity()).getDAO();
        initComponent();

        initShimmerLoading();

        return root_view;
    }

    private void initComponent() {
        shimmer_fav = root_view.findViewById(R.id.shimmer_fav);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterVideoFavorite(getActivity(), new ArrayList<VideoEntity>());
        recycler_view.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterVideoFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View v, VideoEntity obj, int position) {
                Intent i = new Intent(getActivity(), ActivityVideoDetails.class);
                i.putExtra("key.EXTRA_OBJECT_ID", obj.getId());
                i.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(i);
                click_position = position;
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed);
        ((ImageView) root_view.findViewById(R.id.failed_icon)).setImageResource(R.drawable.img_no_item);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(R.string.no_item);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        (root_view.findViewById(R.id.failed_retry)).setVisibility(View.GONE);
    }

    private void initShimmerLoading() {
        LinearLayout lyt_shimmer_content = root_view.findViewById(R.id.lyt_shimmer_content);
        for (int h = 0; h < 10; h++) {
            LinearLayout row_item = new LinearLayout(getActivity());
            row_item.setOrientation(LinearLayout.HORIZONTAL);
            View item = getLayoutInflater().inflate(R.layout.loading_fragment_video, null);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 1;
            item.setLayoutParams(p);
            row_item.addView(item);
            lyt_shimmer_content.addView(row_item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleProgress(true);
        if (application.isFavoriteChange()) {
            items = dao.getAllVideo();
        } else {
            items = application.getFavorites();
            displayData(items);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayData(items);
            }
        }, 500);

        if (mAdapter != null && 0 <= click_position && click_position < mAdapter.getItemCount()) {
            mAdapter.notifyItemChanged(click_position);
        }
    }

    private void displayData(List<VideoEntity> entities) {
        toggleProgress(false);
        mAdapter.setItems(entities);
        application.setFavorites(entities);
        application.setFavoriteChange(false);
        if (mAdapter.getItemCount() == 0) {
            showNoItemView(true);
        } else {
            showNoItemView(false);
        }
    }

    private void toggleProgress(final boolean show) {
        if (!show) {
            shimmer_fav.setVisibility(View.GONE);
            shimmer_fav.stopShimmer();
            recycler_view.setVisibility(View.VISIBLE);
            return;
        }
        shimmer_fav.setVisibility(View.VISIBLE);
        shimmer_fav.startShimmer();
        recycler_view.setVisibility(View.INVISIBLE);
    }

}
