package com.sheikh.hussein.abdallah.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.activity.ActivityPlaylistDetails;
import com.sheikh.hussein.abdallah.adapter.AdapterPlaylist;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseCategory;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPlaylist extends Fragment {

    private View root_view;

    private SwipeRefreshLayout swipe_refresh;
    private ShimmerFrameLayout shimmer_playlist;
    private Call<ResponseCategory> callbackCall = null;

    private RecyclerView recycler_view;
    private AdapterPlaylist mAdapter;

    private int count_total = 0;
    private int failed_page = 0;

    private ThisApplication application;

    public FragmentPlaylist() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_playlist, container, false);
        application = ThisApplication.getInstance();
        initComponent();

        initShimmerLoading();

        List<Playlist> playlists = application.getPlaylists();
        int _count_total = application.getCountTotalPlaylist();
        if (playlists.size() == 0) {
            requestAction(1);
        } else {
            count_total = _count_total;
            displayApiResult(playlists);
        }
        return root_view;
    }

    private void initComponent() {
        shimmer_playlist = root_view.findViewById(R.id.shimmer_playlist);
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterPlaylist(getActivity(), recycler_view, new ArrayList<Playlist>());
        recycler_view.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterPlaylist.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Playlist obj, int position) {
                Intent intent = new Intent(getActivity(),ActivityPlaylistDetails.class);
                intent.putExtra("key.EXTRA_OBJECT", obj);
                intent.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(intent);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterPlaylist.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (count_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                mAdapter.resetListData();
                requestAction(1);
            }
        });
    }

    private void displayApiResult(final List<Playlist> items) {
        mAdapter.insertData(items);
        swipeProgress(false);
        //if (items.size() == 0) showNoItemView(true);
    }

    private void requestListPlaylist(final int page_no) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getListPlaylist();
        callbackCall.enqueue(new Callback<ResponseCategory>() {
            @Override
            public void onResponse(Call<ResponseCategory> call, Response<ResponseCategory> response) {
                ResponseCategory resp = response.body();
                if (resp != null) {
                    //count_total = resp.count_total;
                    //application.setCountTotalPlaylist(count_total);
                    application.addPlaylists(resp.categories);

                    displayApiResult(resp.categories);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ResponseCategory> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_no_internet);
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "", R.drawable.img_failed);
        //showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
            application.resetPlaylists();
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListPlaylist(page_no);
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }


    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = (View) root_view.findViewById(R.id.lyt_failed);
        ((ImageView) root_view.findViewById(R.id.failed_icon)).setImageResource(icon);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recycler_view.setVisibility(View.INVISIBLE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recycler_view.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) root_view.findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            shimmer_playlist.setVisibility(View.GONE);
            shimmer_playlist.stopShimmer();
            recycler_view.setVisibility(View.VISIBLE);
            return;
        }

        shimmer_playlist.setVisibility(View.VISIBLE);
        shimmer_playlist.startShimmer();
        recycler_view.setVisibility(View.INVISIBLE);
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    private void initShimmerLoading() {
        LinearLayout lyt_shimmer_content = root_view.findViewById(R.id.lyt_shimmer_content);
        int grid_count = Tools.getGridSpanCount(getActivity());
        for (int h = 0; h < 10; h++) {
            LinearLayout row_item = new LinearLayout(getActivity());
            row_item.setOrientation(LinearLayout.HORIZONTAL);
            for (int w = 0; w < grid_count; w++) {
                View item = getLayoutInflater().inflate(R.layout.loading_fragment_playlist, null);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                item.setLayoutParams(p);
                row_item.addView(item);
            }
            lyt_shimmer_content.addView(row_item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
