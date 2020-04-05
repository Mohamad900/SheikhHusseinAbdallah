package com.sheikh.hussein.abdallah.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.adapter.AdapterVideo;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseCategoryDetails;
import com.sheikh.hussein.abdallah.connection.response.ResponseVideo;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.data.SharedPref;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPlaylistDetails extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";

    public static void navigate(Activity activity, Playlist obj, boolean from_notif) {
        Intent i = navigateBase(activity, obj, from_notif);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, Playlist obj, Boolean from_notif) {
        Intent i = new Intent(context, ActivityPlaylistDetails.class);
        i.putExtra(EXTRA_OBJECT, obj);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    // extra obj
    private Playlist playlist;
    private Boolean from_notif;

    private SharedPref sharedPref;

    private RecyclerView recycler_view;
    private AdapterVideo mAdapter;

    private Call<ResponseVideo> callbackCall = null;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private ShimmerFrameLayout shimmer_layout;
    private View lyt_main_content;

    private int count_total = 0;
    private int failed_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);
        sharedPref = new SharedPref(this);

        playlist = (Playlist) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

        initComponent();
        initToolbar();
        requestAction(1);
        // Analytics track
        //ThisApplication.getInstance().saveCustomLogEvent("PLAYLIST_DETAILS_" + playlist.id);
    }

    private void initComponent() {
        parent_view = findViewById(android.R.id.content);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        shimmer_layout = findViewById(R.id.shimmer_layout);
        lyt_main_content = findViewById(R.id.lyt_main_content);

        displayVideos(new ArrayList<Video>());

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

    private void displayVideos(List<Video> items) {
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);

        //set data and list adapter
        Video head = new Video();
        head.list_type = AdapterVideo.VIEW_HEAD;
        items.add(0, head);
        mAdapter = new AdapterVideo(this, recycler_view, items);
        mAdapter.setPlaylist(playlist);
        recycler_view.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterVideo.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position) {
                Intent i = new Intent(ActivityPlaylistDetails.this, ActivityVideoDetails.class);
                i.putExtra("key.EXTRA_OBJECT_ID",obj.VideoId);
                i.putExtra("key.EXTRA_FROM_NOTIF",false);
                startActivity(i);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterVideo.OnLoadMoreListener() {
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
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.colorPrimary));
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "", R.drawable.img_failed);
        //showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPlaylistVideoAPI(page_no);
            }
        }, 1000);
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_no_internet);
        }
    }

    private void displayApiResult(int page_no, final List<Video> items) {
        if (page_no == 1) {
            displayVideos(items);
        } else {
            mAdapter.insertData(items);
        }
        swipeProgress(false);
        //if (items.size() == 0) showNoItemView(true);
    }

    private void requestPlaylistVideoAPI(final int page_no) {
        //if (playlist.isDraft()) {
            API api = RestAdapter.createAPI();
            Call<ResponseCategoryDetails> call = api.getPlaylistDetails(new Playlist(playlist.CategoryId));
            call.enqueue(new Callback<ResponseCategoryDetails>() {
                @Override
                public void onResponse(Call<ResponseCategoryDetails> call, Response<ResponseCategoryDetails> response) {
                    ResponseCategoryDetails resp = response.body();
                    if (resp != null) {
                        playlist = resp.category;
                        requestPlaylistDetailsAPI(page_no);
                    } else {
                        onFailRequest(page_no);
                    }
                }

                @Override
                public void onFailure(Call<ResponseCategoryDetails> call, Throwable t) {
                    if (!call.isCanceled()) requestPlaylistDetailsAPI(page_no);
                }
            });
        //} else {
        //    requestPlaylistDetailsAPI(page_no);
        //}
    }

    private void requestPlaylistDetailsAPI(final int page_no) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getListVideo(new Playlist(playlist.CategoryId));
        callbackCall.enqueue(new Callback<ResponseVideo>() {
            @Override
            public void onResponse(Call<ResponseVideo> call, Response<ResponseVideo> response) {
                ResponseVideo resp = response.body();
                if (resp != null) {
                    count_total = resp.count_total;
                    displayApiResult(page_no, resp.videos);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ResponseVideo> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest(page_no);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (from_notif) {
            if (ActivityMain.active) {
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivitySplash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);

        ((ImageView) findViewById(R.id.failed_icon)).setImageResource(icon);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.INVISIBLE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            shimmer_layout.setVisibility(View.GONE);
            shimmer_layout.stopShimmer();
            lyt_main_content.setVisibility(View.VISIBLE);
            return;
        }
        shimmer_layout.setVisibility(View.VISIBLE);
        shimmer_layout.startShimmer();
        lyt_main_content.setVisibility(View.INVISIBLE);
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_playlist_details, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackPressed();
        } else if (item_id == R.id.action_settings) {
            ActivitySettings.navigate(this);
        } else if (item_id == R.id.action_rate) {
            Tools.rateAction(this);
        } else if (item_id == R.id.action_about) {
            Tools.showDialogAbout(this);
        }
        return super.onOptionsItemSelected(item);
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
