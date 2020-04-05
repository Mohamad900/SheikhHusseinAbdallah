package com.sheikh.hussein.abdallah.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.RemoteConfig;
import com.sheikh.hussein.abdallah.data.SharedPref;

public class FragmentVideoPlayer extends YouTubePlayerSupportFragmentX implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    private String mVideoId;

    private YouTubePlayer player;

    private OnVideoPlayListener onVideoPlayListener;

    public interface OnVideoPlayListener {
        void onPlaying(String videoId);
    }

    public void setOnVideoPlayListener(OnVideoPlayListener onVideoPlaying) {
        this.onVideoPlayListener = onVideoPlaying;
    }

    //Empty constructor
    public FragmentVideoPlayer() {
    }

    /**
     * Returns a new instance of this Fragment
     *
     * @param videoId The ID of the video to play
     */
    public static FragmentVideoPlayer newInstance(final String videoId) {
        final FragmentVideoPlayer youTubeFragment = new FragmentVideoPlayer();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_VIDEO_ID, videoId);
        youTubeFragment.setArguments(bundle);
        return youTubeFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final Bundle arguments = getArguments();

        if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
            mVideoId = bundle.getString(KEY_VIDEO_ID);
        } else if (arguments != null && arguments.containsKey(KEY_VIDEO_ID)) {
            mVideoId = arguments.getString(KEY_VIDEO_ID);
        }

        String API_KEY = new RemoteConfig().getYoutubeApiKey();
        initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        if (mVideoId != null) {
            player = youTubePlayer;
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            if (!restored) {
                player = youTubePlayer;
                player.setFullscreen(false);
                player.loadVideo(mVideoId);
                player.play();
            }
            if (fullScreenListener != null) {
                player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        fullScreenListener.onFullscreen(b);
                    }
                });
            }
            if (onVideoPlayListener != null) onVideoPlayListener.onPlaying(mVideoId);
        }
        if (fragmentCallback != null) {
            fragmentCallback.onViewCreated();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            //Handle the failure
            Toast.makeText(getActivity(), R.string.error_init_failure, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_VIDEO_ID, mVideoId);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View root_view = super.onCreateView(layoutInflater, viewGroup, bundle);
        return root_view;
    }

    public YouTubePlayer getPlayer() {
        return player;
    }

    public void backFromFullscreen() {
        if (mVideoId == null || player == null) return;
        player.setFullscreen(false);
        player.pause();
    }

    private FragmentCallback fragmentCallback;
    private FullScreenListener fullScreenListener = null;

    public void setFragmentCallback(FragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    public void setFullScreenListener(FullScreenListener fullScreenListener) {
        this.fullScreenListener = fullScreenListener;
    }

    public interface FragmentCallback {
        void onViewCreated();
    }

    public interface FullScreenListener {
        void onFullscreen(boolean b);
    }
}