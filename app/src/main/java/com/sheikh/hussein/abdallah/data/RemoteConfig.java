package com.sheikh.hussein.abdallah.data;

import android.app.Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sheikh.hussein.abdallah.BuildConfig;
import com.sheikh.hussein.abdallah.R;

import androidx.annotation.NonNull;

public class RemoteConfig {

    // firebase remote config key property
    private static final String YOUTUBE_API_KEY = "youtube_api_key";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public RemoteConfig() {
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        // Get Remote Config instance.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

    }

    public void fetchData(Activity activity) {
        long cacheExpiration = 3600;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activateFetched();
                }
            }
        });
    }

    public String getYoutubeApiKey() {
        return mFirebaseRemoteConfig.getString(YOUTUBE_API_KEY);
    }

}
