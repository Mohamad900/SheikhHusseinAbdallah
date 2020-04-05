package com.sheikh.hussein.abdallah.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.adapter.AdapterVideo;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseVideo;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.data.SharedPref;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.model.SearchBody;
import com.sheikh.hussein.abdallah.model.SearchFilter;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";

    public static void navigate(Activity activity, SearchFilter searchFilter) {
        Intent i = new Intent(activity, ActivitySearch.class);
        i.putExtra(EXTRA_OBJECT, searchFilter);
        activity.startActivity(i);
    }

    private Call<ResponseVideo> callbackCall = null;
    private EditText et_search;
    private RecyclerView recyclerView;
    private AdapterVideo mAdapter;
    private ImageView btn_filter;
    private SharedPref sharedPref;
    private SearchFilter searchFilter = new SearchFilter();
    private Set<Tag> tags = new HashSet<>();

    private int post_total = 0;
    private int failed_page = 0;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sharedPref = new SharedPref(this);

        initToolbar();
        initComponent();

        if (getIntent().getSerializableExtra(EXTRA_OBJECT) != null) {
            searchFilter = (SearchFilter) getIntent().getSerializableExtra(EXTRA_OBJECT);
            checkFilterIsActive();
        }
        hideKeyboard();

        // Analytics track
        //ThisApplication.getInstance().saveCustomLogEvent("OPEN_SEARCH");
    }

    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        et_search = findViewById(R.id.et_search);
        btn_filter = findViewById(R.id.btn_filter);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        mAdapter = new AdapterVideo(this, recyclerView, new ArrayList<Video>());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterVideo.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int pos) {
                Intent i = new Intent(ActivitySearch.this, ActivityVideoDetails.class);
                i.putExtra("key.EXTRA_OBJECT_ID", obj.VideoId);
                i.putExtra("key.EXTRA_FROM_NOTIF", false);
                startActivity(i);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterVideo.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySearchFilter.navigate(ActivitySearch.this, searchFilter);
            }
        });

        (findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showNoItemView(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivitySearchFilter.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            searchFilter = (SearchFilter) data.getSerializableExtra(ActivitySearchFilter.RESULT_DATA);
            checkFilterIsActive();
        }
    }

    private void checkFilterIsActive() {
        if (searchFilter.isDefault()) {
            btn_filter.setColorFilter(getResources().getColor(R.color.grey_40), PorterDuff.Mode.SRC_ATOP);
        } else {
            btn_filter.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        tags.clear();
        for (Tag t : searchFilter.tags) tags.add(new Tag(t.Id));
        searchAction();
    }

    private void searchAction() {
        query = et_search.getText().toString().trim();
        if (!query.equals("") || !searchFilter.isDefault()) {
            mAdapter.resetListData();
            // request action will be here
            requestAction(1);

            // Analytics track
            //ThisApplication.getInstance().saveCustomLogEvent("ACTION_SEARCH_" + query);
        } else {
            Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            showProgress(true);
        } else {
            mAdapter.setLoading();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListVideo(page_no);
            }
        }, 1000);
    }

    private void requestListVideo(final int page_no) {
        API api = RestAdapter.createAPI();
        SearchBody body = new SearchBody(query);
        body.category_id = searchFilter.playlist.CategoryId;
        body.tags = tags;

        callbackCall = api.getListVideoAdv(body);
        callbackCall.enqueue(new Callback<ResponseVideo>() {
            @Override
            public void onResponse(Call<ResponseVideo> call, Response<ResponseVideo> response) {
                ResponseVideo resp = response.body();
                if (resp != null) {
                    post_total = resp.count_total;
                    displayApiResult(resp.videos);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ResponseVideo> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void displayApiResult(final List<Video> items) {
        mAdapter.insertData(items);
        showProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        showProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_failed);
        (findViewById(R.id.failed_retry)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.failed_icon)).setImageResource(R.drawable.img_no_item);
        ((TextView) findViewById(R.id.failed_message)).setText(R.string.no_item);
        if (show) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
