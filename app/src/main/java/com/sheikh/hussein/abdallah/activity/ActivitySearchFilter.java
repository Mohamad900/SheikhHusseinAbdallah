package com.sheikh.hussein.abdallah.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.adapter.AdapterPlaylistPick;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseCategory;
import com.sheikh.hussein.abdallah.connection.response.ResponseHome;
import com.sheikh.hussein.abdallah.data.ThisApplication;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.SearchFilter;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearchFilter extends AppCompatActivity {

    public static final int REQUEST_CODE = 500;
    public static final String RESULT_DATA = "RESULT_DATA";

    public static void navigate(Activity activity, SearchFilter searchFilter) {
        Intent i = new Intent(activity, ActivitySearchFilter.class);
        i.putExtra(RESULT_DATA, searchFilter);
        activity.startActivityForResult(i, REQUEST_CODE);
    }

    private ActionBar actionBar;
    private FlexboxLayout tags_flex_box;
    private RadioGroup rg_playlist_1, rg_playlist_2;
    private OnCheckedChangeListener listener;
    private ThisApplication application;
    private ResponseHome responseHome;
    private SearchFilter searchFilter = new SearchFilter();
    private List<Playlist> displayed_playlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);
        application = ThisApplication.getInstance();
        responseHome = application.getResponseHome();

        if (getIntent().getSerializableExtra(RESULT_DATA) != null) {
            searchFilter = (SearchFilter) getIntent().getSerializableExtra(RESULT_DATA);
        }

        initToolbar();
        initComponent();
        if (responseHome != null) {
            displayed_playlist = new ArrayList<>(responseHome.featuredCategories);
            displayed_playlist.add(0, new Playlist("", getString(R.string.all_playlist)));
            Boolean isExist=false;
            for(int i=0;i<displayed_playlist.size();i++){
                Playlist p = displayed_playlist.get(i);
                if (p.CategoryId.equals(searchFilter.playlist.CategoryId)) {
                    isExist = true;
                }
            }
            if(!isExist){
                displayed_playlist.add(1, new Playlist(searchFilter.playlist.CategoryId, searchFilter.playlist.Name));
            }
            initPlaylist();
        }
        initTags();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(R.string.title_search_filter);
        Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.colorPrimary));
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        rg_playlist_1 = findViewById(R.id.rg_playlist_1);
        rg_playlist_2 = findViewById(R.id.rg_playlist_2);
        tags_flex_box = findViewById(R.id.tags_flex_box);
        (findViewById(R.id.btn_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rg_playlist_1.check(0);
                resetTag();
            }
        });

        (findViewById(R.id.btn_apply)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(RESULT_DATA, searchFilter);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        (findViewById(R.id.see_all_playlist)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPlaylist();
            }
        });
    }

    private void initPlaylist() {
        rg_playlist_1.removeAllViews();
        rg_playlist_2.removeAllViews();
        refreshRadioGroup(rg_playlist_1.getId());
        refreshRadioGroup(rg_playlist_2.getId());

        int div_size = displayed_playlist.size() / 2;
        int mod_size = displayed_playlist.size() % 2;

        for (int i = 0; i < div_size + mod_size; i++) {
            Playlist p = displayed_playlist.get(i);
            rg_playlist_1.addView(getRadioButtonTemplate(i, p));
            if (p.CategoryId.equals(searchFilter.playlist.CategoryId)) {
                rg_playlist_1.check(i);
            }
        }

        for (int i = div_size + mod_size; i < displayed_playlist.size(); i++) {
            Playlist p = displayed_playlist.get(i);
            rg_playlist_2.addView(getRadioButtonTemplate(i, p));
            if (p.CategoryId.equals(searchFilter.playlist.CategoryId)) {
                rg_playlist_2.check(i);
            }
        }

        listener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return;
                refreshRadioGroup(group.getId());
                Playlist p = displayed_playlist.get(checkedId);
                searchFilter.playlist = new Playlist(p.CategoryId, p.Name);
            }
        };
        rg_playlist_1.setOnCheckedChangeListener(listener);
        rg_playlist_2.setOnCheckedChangeListener(listener);
    }

    private void initTags() {
        if (responseHome.tags == null) return;
        List<Tag> tags = new ArrayList<>(responseHome.tags);
        for (Tag t : tags) {
            int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
            Button btn = new Button(new ContextThemeWrapper(this, buttonStyle), null, buttonStyle);
            btn.setTag(t);
            btn.setText(t.Name);
            btn.setAllCaps(false);
            btn.setTextColor(getResources().getColor(R.color.grey_80));
            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_tag_outline));
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Tools.dpToPx(this, 35));
            btn.setLayoutParams(layoutParams);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagClick(v);
                }
            });
            for (Tag selectedTag : searchFilter.tags) {
                if (selectedTag.Id.equals(t.Id)) {
                    btn.setSelected(true);
                    btn.setTextColor(Color.WHITE);
                }
            }
            tags_flex_box.addView(btn);
        }
    }

    private void tagClick(View view) {
        if (view instanceof Button) {
            Button b = (Button) view;
            b.setSelected(!b.isSelected());
            Tag value = (Tag) b.getTag();
            if (b.isSelected()) {
                b.setTextColor(Color.WHITE);
                searchFilter.tags.add(value);
            } else {
                b.setTextColor(getResources().getColor(R.color.grey_80));
                Iterator iterator = searchFilter.tags.iterator();
                while (iterator.hasNext()) {
                    Tag t = (Tag) iterator.next();
                    if (t.Id.equals(value.Id)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private void resetTag() {
        for (int i = 0; i < tags_flex_box.getChildCount(); i++) {
            Button b = (Button) tags_flex_box.getChildAt(i);
            b.setSelected(false);
            b.setTextColor(getResources().getColor(R.color.grey_80));
        }
        searchFilter.tags.clear();
    }

    private void refreshRadioGroup(int groupId) {
        if (groupId == R.id.rg_playlist_1) {
            rg_playlist_2.setOnCheckedChangeListener(null);
            rg_playlist_2.clearCheck();
            rg_playlist_2.setOnCheckedChangeListener(listener);
        } else if (groupId == R.id.rg_playlist_2) {
            rg_playlist_1.setOnCheckedChangeListener(null);
            rg_playlist_1.clearCheck();
            rg_playlist_1.setOnCheckedChangeListener(listener);
        }
    }

    private AppCompatRadioButton getRadioButtonTemplate(int idx, Playlist p) {
        AppCompatRadioButton rb = new AppCompatRadioButton(this);
        rb.setId(idx);
        rb.setText(p.Name);
        rb.setMaxLines(1);
        rb.setSingleLine(true);
        rb.setEllipsize(TextUtils.TruncateAt.END);
        rb.setTextAppearance(this, R.style.TextAppearance_AppCompat_Subhead);
        rb.setTextColor(getResources().getColor(R.color.grey_80));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rb.setLayoutParams(params);
        rb.setPadding(0, 15, 0, 15);
        return rb;
    }


    private Call<ResponseCategory> callbackCall;

    private void populatePlaylist(final RequestPlaylistListener listener) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getListPlaylist();//api.getListPlaylistName();
        callbackCall.enqueue(new Callback<ResponseCategory>() {
            @Override
            public void onResponse(Call<ResponseCategory> call, Response<ResponseCategory> response) {
                ResponseCategory resp = response.body();
                if (resp != null) {
                    listener.onSuccess(resp.categories);
                } else {
                    listener.onFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseCategory> call, Throwable t) {
                listener.onFailed();
            }

        });
    }

    private boolean isPlaylistDisplayed(String id) {
        for (Playlist p : displayed_playlist) {
            if (p.CategoryId.equals(id)) return true;
        }
        return false;
    }

    private void showDialogPlaylist() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_playlist_pick);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        final View progress_loading = dialog.findViewById(R.id.progress_loading);
        progress_loading.setVisibility(View.VISIBLE);

        RecyclerView recycler = dialog.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        //set data and list adapter
        final AdapterPlaylistPick mAdapter = new AdapterPlaylistPick(this, new ArrayList<Playlist>());
        recycler.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterPlaylistPick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Playlist obj) {
                dialog.dismiss();
                if (!isPlaylistDisplayed(obj.CategoryId)) {
                    displayed_playlist.add(obj);
                }
                searchFilter.playlist.CategoryId = obj.CategoryId;
                initPlaylist();
            }
        });
        RequestPlaylistListener listener = new RequestPlaylistListener() {
            @Override
            public void onSuccess(List<Playlist> playlist) {
                progress_loading.setVisibility(View.GONE);
                mAdapter.setItems(playlist);
            }

            @Override
            public void onFailed() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivitySearchFilter.this, R.string.failed_when_load, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }, 1000);
            }
        };
        populatePlaylist(listener);


        ((EditText) dialog.findViewById(R.id.et_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        (dialog.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search_filter, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_close) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private interface RequestPlaylistListener {
        void onSuccess(List<Playlist> playlist);

        void onFailed();
    }

}
