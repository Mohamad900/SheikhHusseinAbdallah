package com.sheikh.hussein.abdallah.data;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.connection.response.ResponseDevice;
import com.sheikh.hussein.abdallah.connection.response.ResponseHome;
import com.sheikh.hussein.abdallah.model.DeviceInfo;
import com.sheikh.hussein.abdallah.model.Info;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.Video;
import com.sheikh.hussein.abdallah.room.table.VideoEntity;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThisApplication extends Application {

    private static ThisApplication mInstance;
    private SharedPref sharedPref;
    private FirebaseAnalytics mFirebaseAnalytics;

    private int fcm_count = 0;
    private final int FCM_MAX_COUNT = 10;
    private Info info = null;

    // global data for activity main
    private ResponseHome responseHome = null;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Video> videos = new ArrayList<>();
    private List<VideoEntity> favorites = new ArrayList<>();
    private int countTotalPlaylist = 0;
    private int countTotalVideo = 0;
    private boolean favoriteChange = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPref = new SharedPref(this);

        // initialize firebase
        FirebaseApp.initializeApp(this);

        // obtain regId & registering device to server
        subscribeToTopicForNotification();//obtainFirebaseToken();

        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void subscribeToTopicForNotification() {

        if (NetworkCheck.isConnect(this) && sharedPref.isNeedRegister()) {
            fcm_count++;


            FirebaseMessaging.getInstance().subscribeToTopic("news").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sharedPref.setNeedRegister(false);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (fcm_count > FCM_MAX_COUNT) return;
                        subscribeToTopicForNotification();
                }
            });

        }
    }


    /*private void obtainFirebaseToken() {
        if (NetworkCheck.isConnect(this) && sharedPref.isNeedRegister()) {
            fcm_count++;

            Task<InstanceIdResult> resultTask = FirebaseInstanceId.getInstance().getInstanceId();
            resultTask.addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String regId = instanceIdResult.getToken();
                    sharedPref.setFcmRegId(regId);
                    if (!TextUtils.isEmpty(regId)) sendRegistrationToServer(regId);
                }
            });

            resultTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (fcm_count > FCM_MAX_COUNT) return;
                    obtainFirebaseToken();
                }
            });
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.d("FCM_TOKEN", token + "");
        DeviceInfo deviceInfo = Tools.getDeviceInfo(this);
        deviceInfo.regid = token;

        API api = RestAdapter.createAPI();
        Call<ResponseDevice> callbackCall = api.registerDevice(deviceInfo);
        callbackCall.enqueue(new Callback<ResponseDevice>() {
            @Override
            public void onResponse(Call<ResponseDevice> call, Response<ResponseDevice> response) {
                ResponseDevice resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    sharedPref.setNeedRegister(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseDevice> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }*/


    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    public ResponseHome getResponseHome() {
        return responseHome;
    }

    public void setResponseHome(ResponseHome responseHome) {
        this.responseHome = responseHome;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void addVideos(List<Video> videos) {
        this.videos.addAll(videos);
    }

    public void resetVideos() {
        this.videos.clear();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getCountTotalVideo() {
        return countTotalVideo;
    }

    public void setCountTotalVideo(int countTotalVideo) {
        this.countTotalVideo = countTotalVideo;
    }

    public void addPlaylists(List<Playlist> playlists) {
        this.playlists.addAll(playlists);
    }

    public void resetPlaylists() {
        this.playlists.clear();
    }

    public int getCountTotalPlaylist() {
        return countTotalPlaylist;
    }

    public void setCountTotalPlaylist(int countTotalPlaylist) {
        this.countTotalPlaylist = countTotalPlaylist;
    }

    public boolean isFavoriteChange() {
        return favoriteChange;
    }

    public void setFavoriteChange(boolean favoriteChange) {
        this.favoriteChange = favoriteChange;
    }

    public List<VideoEntity> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<VideoEntity> favorites) {
        this.favorites = favorites;
    }
}
