package com.sheikh.hussein.abdallah.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.activity.ActivityMain;
import com.sheikh.hussein.abdallah.activity.ActivityPlaylistDetails;
import com.sheikh.hussein.abdallah.activity.ActivitySearch;
import com.sheikh.hussein.abdallah.activity.ActivityVideoDetails;
import com.sheikh.hussein.abdallah.adapter.AdapterPlaylistFeatured;
import com.sheikh.hussein.abdallah.adapter.AdapterVideoFeatured;
import com.sheikh.hussein.abdallah.adapter.AdapterVideoRecent;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseHome;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.model.Info;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.SearchFilter;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.StartSnapHelper;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {

    private View root_view;
    private ViewPager view_pager_featured;
    private SwipeRefreshLayout swipe_refresh;
    private ShimmerFrameLayout shimmer_home;
    private AdapterVideoFeatured mAdapter;
    private AdapterVideoRecent mAdapterRecent;
    private View lyt_main_content;

    private Handler handler = new Handler();
    private Runnable runnableCode = null;
    private Call<ResponseHome> callbackCall;

    private ThisApplication application;
    private int click_position_featured = -1;
    private int click_position_recent = -1;
    private boolean on_pause = false;

    public FragmentHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        application = ThisApplication.getInstance();
        initComponent();

        ResponseHome responseHome = application.getResponseHome();
        if (responseHome == null) {
            requestAction();
        } else {
            displayData(responseHome);
        }
        return root_view;
    }

    private void initComponent() {
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        shimmer_home = root_view.findViewById(R.id.shimmer_home);
        lyt_main_content = root_view.findViewById(R.id.lyt_home_content);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });

        (root_view.findViewById(R.id.more_video)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavigationMenu(R.id.navigation_video);
            }
        });
        (root_view.findViewById(R.id.more_playlist)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavigationMenu(R.id.navigation_playlist);
            }
        });
    }

    private void selectNavigationMenu(int id) {
        Activity act = getActivity();
        if (act instanceof ActivityMain) {
            ActivityMain activityMain = (ActivityMain) act;
            activityMain.onNavigationMenuItemSelected(id, true);
        }
    }

    private void requestAction() {
        showFailedView(false, "", R.drawable.img_failed);
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestHomeData();
            }
        }, 1000);
    }

    private void requestHomeData() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getHome();
        callbackCall.enqueue(new Callback<ResponseHome>() {
            @Override
            public void onResponse(Call<ResponseHome> call, Response<ResponseHome> response) {
                ResponseHome resp = response.body();
                if (resp != null) {
                    displayData(resp);
                    ThisApplication.getInstance().setResponseHome(resp);
                    swipeProgress(false);
                    lyt_main_content.setVisibility(View.VISIBLE);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<ResponseHome> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_no_internet);
        }
    }

    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed);
        ((ImageView) root_view.findViewById(R.id.failed_icon)).setImageResource(icon);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) root_view.findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            shimmer_home.setVisibility(View.GONE);
            shimmer_home.stopShimmer();
            return;
        }
        shimmer_home.setVisibility(View.VISIBLE);
        shimmer_home.startShimmer();
        lyt_main_content.setVisibility(View.INVISIBLE);
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    private void startAutoSlider(final int count) {
        if (runnableCode != null) handler.removeCallbacks(runnableCode);
        runnableCode = new Runnable() {
            @Override
            public void run() {
                int pos = view_pager_featured.getCurrentItem();
                pos = pos + 1;
                if (pos >= count) pos = 0;
                if (!on_pause) view_pager_featured.setCurrentItem(pos);
                handler.postDelayed(runnableCode, 3000);
            }
        };
        handler.postDelayed(runnableCode, 3000);
    }

    private void displayData(ResponseHome resp) {
        displayFeatured(resp.featuredVideos);
        displayRecent(resp.recentVideos);
        displayPlaylist(resp.featuredCategories);
        displayTags(resp.tags);
    }

    private void displayFeatured(final List<Video> items) {
        view_pager_featured = root_view.findViewById(R.id.view_pager_featured);
        final TextView featured_title = root_view.findViewById(R.id.featured_title);
        mAdapter = new AdapterVideoFeatured(getActivity(), items);
        view_pager_featured.setAdapter(mAdapter);
        view_pager_featured.setOffscreenPageLimit(4);
        if (items.size() > 0) featured_title.setText( Tools.convertTextNumbersToArabic(items.get(0).Name));
        view_pager_featured.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position >= items.size()) return;
                featured_title.setText( Tools.convertTextNumbersToArabic(items.get(position).Name));
                startAutoSlider(items.size());
            }
        });
        mAdapter.setOnItemClickListener(new AdapterVideoFeatured.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Video obj, int position) {
                Intent i = new Intent(getActivity(), ActivityVideoDetails.class);
                i.putExtra("key.EXTRA_OBJECT_ID", obj.VideoId);
                i.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(i);

                click_position_featured = position;
            }
        });
        startAutoSlider(items.size());
    }

    private void displayRecent(List<Video> items) {
        RecyclerView recyclerView = root_view.findViewById(R.id.recycler_view_recent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mAdapterRecent = new AdapterVideoRecent(getActivity(), items);
        recyclerView.setAdapter(mAdapterRecent);
        //recyclerView.setOnFlingListener(null);
        //StartSnapHelper startSnapHelper = new StartSnapHelper();
        //startSnapHelper.attachToRecyclerView(recyclerActivityVideoDetailsView);

        mAdapterRecent.setOnItemClickListener(new AdapterVideoRecent.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Video obj, int position) {
                click_position_recent = position;
                Intent i = new Intent(getActivity(), ActivityVideoDetails.class);
                i.putExtra("key.EXTRA_OBJECT_ID", obj.VideoId);
                i.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(i);
            }
        });
    }

    private void displayPlaylist(List<Playlist> items) {
        RecyclerView recycler_view = root_view.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        //set data and list adapter
        final AdapterPlaylistFeatured mAdapter = new AdapterPlaylistFeatured(getActivity(), items);
        recycler_view.setAdapter(mAdapter);
        recycler_view.setNestedScrollingEnabled(false);
        mAdapter.setOnItemClickListener(new AdapterPlaylistFeatured.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Playlist obj) {
                Intent i = new Intent(getActivity(), ActivityPlaylistDetails.class);
                i.putExtra("key.EXTRA_OBJECT", obj);
                i.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(i);
            }
        });
    }

    private void displayTags(List<Tag> tags) {
        FlexboxLayout tags_flex_box = root_view.findViewById(R.id.tags_flex_box);
        tags_flex_box.removeAllViews();
        for (final Tag t : tags) {
            int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
            Button btn = new Button(new ContextThemeWrapper(getActivity(), buttonStyle), null, buttonStyle);
            btn.setText(t.Name);
            btn.setAllCaps(false);
            btn.setTextColor(getResources().getColor(R.color.grey_80));
            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_tag));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Tools.dpToPx(getActivity(), 35));
            btn.setLayoutParams(layoutParams);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchFilter searchFilter = new SearchFilter();
                    searchFilter.tags.add(t);
                    ActivitySearch.navigate(getActivity(), searchFilter);
                }
            });
            tags_flex_box.addView(btn);
        }
    }

    @Override
    public void onDestroy() {
        if (callbackCall != null && !callbackCall.isCanceled()) callbackCall.cancel();
        shimmer_home.stopShimmer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        on_pause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        on_pause = false;
        if (mAdapter != null && 0 <= click_position_featured && click_position_featured < mAdapter.getCount()) {
            mAdapter.notifyDataSetChanged();
        }

        if (mAdapterRecent != null && 0 <= click_position_recent && click_position_recent < mAdapterRecent.getItemCount()) {
            mAdapterRecent.notifyItemChanged(click_position_recent);
        }
    }
}
