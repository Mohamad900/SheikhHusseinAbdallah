package com.sheikh.hussein.abdallah.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.SharedPref;
import com.sheikh.hussein.abdallah.utils.PermissionUtil;
import com.sheikh.hussein.abdallah.utils.Tools;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;


/**
 * ATTENTION : To see where list of setting comes is open res/xml/setting_preferences.xml
 */
public class ActivitySettings extends PreferenceActivity {

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivitySettings.class);
        activity.startActivity(i);
    }

    private AppCompatDelegate mDelegate;
    private ActionBar actionBar;
    private SharedPref sharedPref;
    private View parent_view;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
    {
        View v = super.onCreateView(parent, name, context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            if(parent != null)
                parent.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            if(v != null)
                v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        return v;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preferences);
        parent_view = findViewById(android.R.id.content);
        sharedPref = new SharedPref(this);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_title_ringtone)));

        Preference notifPref = (Preference) findPreference(getString(R.string.pref_title_notif));
        if (!PermissionUtil.isStorageGranted(this)) {
            PreferenceCategory prefCat = (PreferenceCategory) findPreference(getString(R.string.pref_group_notif));
            prefCat.setTitle(Html.fromHtml("<b>" + getString(R.string.pref_group_notif) + "</b><br><i>" + getString(R.string.grant_permission_storage) + "</i>"));
            notifPref.setEnabled(false);
        }

       /* final Preference prefVersion = (Preference) findPreference(getString(R.string.pref_title_build));
        prefVersion.setSummary(Tools.getVersionName(this));*/

        final Preference prefEmail = (Preference) findPreference(getString(R.string.pref_title_contact_us));
        prefEmail.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.pref_title_contact_us), getString(R.string.developer_email));
                clipboard.setPrimaryClip(clip);
                Snackbar.make(parent_view, "تم نسخ البريد الإلكتروني", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

        final Preference wtp = (Preference) findPreference(getString(R.string.pref_title_wtp));
        wtp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.pref_title_wtp), getString(R.string.wtp_number));
                clipboard.setPrimaryClip(clip);
                Snackbar.make(parent_view, "تم نسخ الرقم", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

/*        final Preference prefTerm = (Preference) findPreference(getString(R.string.pref_title_term));
        prefTerm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ActivityWebView.navigate(ActivitySettings.this, sharedPref.getPrivacyPolicyUrl(), false);
                return false;
            }
        });*/

      /*  final Preference prefAbout = (Preference) findPreference(getString(R.string.pref_title_about));
        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Tools.showDialogAbout(ActivitySettings.this);
                return false;
            }
        });*/

        initToolbar();

        // Analytics track
        //ThisApplication.getInstance().saveCustomLogEvent("OPEN_SETTINGS");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    private void initToolbar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(R.string.title_activity_settings);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_close) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_settings, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));
        return true;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else {
                // For all other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /*
     * Support for Activity : DO NOT CODE BELOW ----------------------------------------------------
     */

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

}
