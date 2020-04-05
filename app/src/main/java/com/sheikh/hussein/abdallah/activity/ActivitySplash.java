package com.sheikh.hussein.abdallah.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.connection.API;
import com.sheikh.hussein.abdallah.connection.RestAdapter;
import com.sheikh.hussein.abdallah.data.RemoteConfig;
import com.sheikh.hussein.abdallah.data.SharedPref;
import com.sheikh.hussein.abdallah.model.Settings;
import com.sheikh.hussein.abdallah.utils.NetworkCheck;
import com.sheikh.hussein.abdallah.utils.PermissionUtil;
import com.sheikh.hussein.abdallah.utils.Tools;

import java.util.Timer;
import java.util.TimerTask;

public class ActivitySplash extends AppCompatActivity {

    private boolean on_permission_result = false;
    private SharedPref sharedPref;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fullscreen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        sharedPref = new SharedPref(this);
        new RemoteConfig().fetchData(this);
        progress_bar = findViewById(R.id.progress_bar);
        startProgressBar();
    }

    private void startProgressBar() {
        int progress = progress_bar.getProgress();
        progress = progress + 2;
        if (progress > progress_bar.getMax()) progress = 0;
        progress_bar.setProgress(progress);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProgressBar();
            }
        }, 50);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // permission checker for android M or higher
        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            } else {
                startProcess();
            }
        } else {
            startProcess();
        }
    }

    private void startProcess() {
        if (!NetworkCheck.isConnect(this)) {
            dialogNoInternet();
        } else {
            requestInfo();
        }
    }

    public void dialogNoInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_no_internet);
        builder.setMessage(R.string.msg_no_internet);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                retryOpenApplication();
            }
        });
        builder.show();
    }

    private void retryOpenApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProcess();
            }
        }, 2000);
    }

    private void requestInfo() {
        API api = RestAdapter.createAPI();
        int versionCode = Tools.getVersionCode(this);

        Call<Boolean> callbackCall = api.isAppUpToDate(new Settings(versionCode));
        callbackCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Boolean isUpdated = response.body();

                if (isUpdated != null) {

                    if (isUpdated) {
                        startActivityMainDelay();
                    } else {
                        dialogOutDate();
                    }

                } else {
                    dialogServerNotConnect();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                dialogServerNotConnect();
            }
        });
    }

    public void dialogServerNotConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_unable_connect);
        builder.setMessage(R.string.msg_unable_connect);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                retryOpenApplication();
            }
        });
        builder.setNegativeButton(R.string.CLOSE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    public void dialogOutDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.title_info);
        builder.setMessage(R.string.msg_app_out_date);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.UPDATE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Tools.OpenAppInPlayStore(ActivitySplash.this);
            }
        });
        builder.setNegativeButton(R.string.CLOSE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void startActivityMainDelay() {
        // Show splash screen for 2 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(i);
                finish(); // kill current activity
            }
        };
        new Timer().schedule(task, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = shouldShowRequestPermissionRationale(perm);
                sharedPref.setNeverAskAgain(perm, !rationale);
            }
            on_permission_result = true;
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
