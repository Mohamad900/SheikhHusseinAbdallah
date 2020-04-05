package com.sheikh.hussein.abdallah.activity;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.adapter.AdapterPlaylistSimple;
import com.sheikh.hussein.abdallah.adapter.AdapterVideoSimple;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseVideoDetails;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.fragment.FragmentVideoPlayer;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.SearchFilter;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.room.table.VideoEntity;
import com.sheikh.hussein.abdallah.room.table.WatchedEntity;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.StartSnapHelper;
import com.sheikh.hussein.abdallah.utils.Tools;
import com.sheikh.hussein.abdallah.utils.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

public class ActivityVideoDetails extends AppCompatActivity {


    private DAO dao;

    // extra obj
    private String video_id;
    private Boolean from_notif;

    private Boolean fullscreen = false;
    private boolean is_favorite = false;

    private Video video;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<Video> related = new ArrayList<>();

    private Call<ResponseVideoDetails> callbackCall = null;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private ShimmerFrameLayout shimmer_layout;
    private ImageView img_favorite;
    private View lyt_main_content;
    private View lyt_webview;
    private TextView detail_desc;
    private FragmentVideoPlayer fragmentVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        dao = AppDatabase.getDb(this).getDAO();

        video_id = getIntent().getStringExtra("key.EXTRA_OBJECT_ID");
        from_notif = getIntent().getBooleanExtra("key.EXTRA_FROM_NOTIF", false);

        initComponent();
        initToolbar();
        requestAction();
    }

    private void initComponent() {
        parent_view = findViewById(android.R.id.content);
        lyt_webview = findViewById(R.id.lyt_webview);
        swipe_refresh =  findViewById(R.id.swipe_refresh);
        shimmer_layout = findViewById(R.id.shimmer_layout);
        lyt_main_content = (View) findViewById(R.id.lyt_main_content);
        boolean watched = dao.countWatched(video_id) > 0;
        (findViewById(R.id.watched)).setVisibility(watched ? View.VISIBLE : View.GONE);

        swipe_refresh.setEnabled(true);
        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
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

    private void requestAction() {
        showFailedView(false, "", R.drawable.img_failed);
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestVideoDetailsApi();
            }
        }, 1000);
    }

    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = findViewById(R.id.lyt_failed);

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
                requestAction();
            }
        });
    }

    private void swipeProgress(final boolean show) {
        swipe_refresh.setEnabled(true);
        if (!show) {
            swipe_refresh.setRefreshing(show);
            shimmer_layout.setVisibility(View.GONE);
            shimmer_layout.stopShimmer();
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

    private void requestVideoDetailsApi() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getVideoDetails(new Video(video_id));
        callbackCall.enqueue(new Callback<ResponseVideoDetails>() {
            @Override
            public void onResponse(Call<ResponseVideoDetails> call, Response<ResponseVideoDetails> response) {
                ResponseVideoDetails resp = response.body();
                if (resp != null) {
                    video = resp.video;
                    playlists = resp.categories;
                    tags = resp.tags;
                    related = resp.related_video;
                    displayVideoData();
                    displayPlaylistData();
                    displayTagsData();
                    displayRelatedData();
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<ResponseVideoDetails> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void displayVideoData() {

        if(video != null)((TextView) findViewById(R.id.title)).setText(Tools.convertTextNumbersToArabic(video.Name));
        if(video != null)((TextView) findViewById(R.id.duration)).setText(Tools.convertTextNumbersToArabic(video.Duration));

        detail_desc =  findViewById(R.id.detail_desc);
        detail_desc.setText(Tools.convertTextNumbersToArabic(video.Description));

        if(video.CreatedOn != null ) ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDateFull(video.CreatedOn));
        if (!video.Featured) {
            ((TextView) findViewById(R.id.featured)).setVisibility(View.GONE);
        }

        prepareYoutube();
        lyt_main_content.setVisibility(View.VISIBLE);
        lyt_webview.setVisibility(View.GONE);

        ImageView btn_toggle_desc = findViewById(R.id.btn_toggle_desc);
        btn_toggle_desc.setRotation(180);
        btn_toggle_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSectionText(v);
            }
        });

        img_favorite = findViewById(R.id.img_favorite);
        img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_favorite) {
                    dao.insertVideo(VideoEntity.entity(video));
                } else {
                    dao.deleteVideo(video.VideoId);
                }
                refreshFavorite();
                ThisApplication.getInstance().setFavoriteChange(true);
            }
        });

        refreshFavorite();

        // Analytics track
        //ThisApplication.getInstance().saveCustomLogEvent("VIDEO_DETAILS_" + video.VideoId);
    }

    private void displayPlaylistData() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_playlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AdapterPlaylistSimple mAdapter = new AdapterPlaylistSimple(this, playlists);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFlingListener(null);
        StartSnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);

        mAdapter.setOnItemClickListener(new AdapterPlaylistSimple.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Playlist obj, int position) {
                //if (!AppConfig.CLICK_PLAYLIST_ON_DETAILS) return;
                Intent intent = new Intent(ActivityVideoDetails.this,ActivityPlaylistDetails.class);
                intent.putExtra("key.EXTRA_OBJECT", obj);
                intent.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(intent);
            }
        });
    }

    private void displayTagsData() {
        FlexboxLayout tags_flex_box = findViewById(R.id.tags_flex_box);
        tags_flex_box.removeAllViews();
        for (final Tag t : tags) {
            int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
            Button btn = new Button(new ContextThemeWrapper(this, buttonStyle), null, buttonStyle);
            btn.setText(t.Name);
            btn.setAllCaps(false);
            btn.setTextColor(getResources().getColor(R.color.grey_80));
            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_tag));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Tools.dpToPx(this, 35));
            btn.setLayoutParams(layoutParams);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // if (!AppConfig.CLICK_TAG_ON_DETAILS) return;
                    SearchFilter searchFilter = new SearchFilter();
                    searchFilter.tags.add(t);
                    ActivitySearch.navigate(ActivityVideoDetails.this, searchFilter);
                }
            });
            tags_flex_box.addView(btn);
        }
    }

    private void displayRelatedData() {
        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);

        //set data and list adapter
        AdapterVideoSimple mAdapter = new AdapterVideoSimple(this, related);
        recycler_view.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterVideoSimple.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position) {
                Intent intent = new Intent(ActivityVideoDetails.this,ActivityVideoDetails.class);
                intent.putExtra("key.EXTRA_OBJECT_ID", obj.VideoId);
                intent.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(intent);
                //if (!AppConfig.ENABLE_VIDEO_HISTORY) finish();
            }
        });
    }

    private void prepareYoutube() {
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);

        if (result != YouTubeInitializationResult.SUCCESS) {
            result.getErrorDialog(this, 0).show();
            return;
        }

        FragmentVideoPlayer fragment = displayPlayerFragment(null);

        final View fragmentView = findViewById(R.id.fragment_youtube);
        final ImageView image = findViewById(R.id.image);
        final ImageView btn_play = findViewById(R.id.btn_play);

        fragmentView.setVisibility(View.INVISIBLE);
        image.setVisibility(View.VISIBLE);
        btn_play.setVisibility(View.VISIBLE);

        fragment.setFragmentCallback(new FragmentVideoPlayer.FragmentCallback() {
            @Override
            public void onViewCreated() {
                image.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fragmentView.getHeight()));
                Tools.displayImage(ActivityVideoDetails.this, image, Constant.getImageURL(video));
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
                fragmentView.setVisibility(View.VISIBLE);
                displayPlayerFragment(video.URL);
            }
        });
    }

    private FragmentVideoPlayer displayPlayerFragment(String videoId) {
        if (videoId != null) {
            fragmentVideo = FragmentVideoPlayer.newInstance(videoId);
        } else {
            fragmentVideo = new FragmentVideoPlayer();
        }

        fragmentVideo.setFullScreenListener(new FragmentVideoPlayer.FullScreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                fullscreen = b;
            }
        });

        fragmentVideo.setOnVideoPlayListener(new FragmentVideoPlayer.OnVideoPlayListener() {
            @Override
            public void onPlaying(String videoId) {
                dao.insertWatched(new WatchedEntity(video_id, System.currentTimeMillis()));
                if (dao.countWatched(video_id) > 0) return;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_youtube, fragmentVideo);

        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        return fragmentVideo;
    }

    private void refreshFavorite() {
        if (dao.getVideo(video_id) != null) {
            img_favorite.setImageResource(R.drawable.ic_favorite);
            is_favorite = true;
        } else {
            img_favorite.setImageResource(R.drawable.ic_favorite_border);
            is_favorite = false;
        }
    }

    private void toggleSectionText(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.collapse(lyt_webview);
        } else {
            ViewAnimation.expand(lyt_webview);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_no_internet);
        }
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            fragmentVideo.backFromFullscreen();
            return;
        }
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
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fragmentVideo != null && fragmentVideo.getPlayer() != null) {
            fragmentVideo.getPlayer().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_video_details, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackPressed();
        } else if (item_id == R.id.action_share) {
            Tools.methodShare(this, video);
        } else if (item_id == R.id.action_settings) {
            ActivitySettings.navigate(this);
        } else if (item_id == R.id.action_rate) {
            Tools.rateAction(this);
        } else if (item_id == R.id.action_about) {
            Tools.showDialogAbout(this);
        }
        return super.onOptionsItemSelected(item);
    }

}
