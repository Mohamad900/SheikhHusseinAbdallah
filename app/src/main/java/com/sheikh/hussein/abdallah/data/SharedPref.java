package com.sheikh.hussein.abdallah.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.sheikh.hussein.abdallah.R;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;

    private static final String FCM_PREF_KEY = "_.FCM_PREF_KEY";
    private static final String NEED_REGISTER = "_.NEED_REGISTER";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setNeedRegister(boolean value) {
        sharedPreferences.edit().putBoolean(NEED_REGISTER, value).apply();
    }

    public boolean isNeedRegister() {
        return sharedPreferences.getBoolean(NEED_REGISTER, true);
    }

    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString(FCM_PREF_KEY, fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString(FCM_PREF_KEY, null);
    }

    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * For notifications flag
     */
    public boolean getNotification() {
        return prefs.getBoolean(context.getString(R.string.pref_title_notif), true);
    }

    public String getRingtone() {
        return prefs.getString(context.getString(R.string.pref_title_ringtone), "content://settings/system/notification_sound");
    }

    public boolean getVibration() {
        return prefs.getBoolean(context.getString(R.string.pref_title_vibrate), true);
    }

}
