package com.sheikh.hussein.abdallah.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.DeviceInfo;
import com.sheikh.hussein.abdallah.model.NotifType;
import com.sheikh.hussein.abdallah.model.Video;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.widget.Toolbar;

public class Tools {

    public static int getVersionCode(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static void OpenAppInPlayStore(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }

    public static void setSystemBarLight(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = act.findViewById(android.R.id.content);
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void changeOverflowMenuIconColor(Toolbar toolbar, @ColorInt int color) {
        try {
            Drawable drawable = toolbar.getOverflowIcon();
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {
        }
    }

    public static void displayImage(Context ctx, ImageView img, String url) {
        try {
            try {
                Glide.with(ctx).load(url)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(img);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    public static String getFormattedDate(String dateTime) {
        Locale locale = new Locale("ar");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy hh:mm",locale);//dd/MM/yy
        return newFormat.format(new Date(dateTime));
    }

    public static void displayImageThumb(Context ctx, ImageView img, String url, float thumb) {
        try {
            Glide.with(ctx).load(url)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(thumb)
                    .into(img);
        } catch (Exception e) {
        }
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void directLinkToBrowser(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Ops, Cannot open url", Toast.LENGTH_LONG).show();
        }
    }

    public static String getFormattedDateFull(String dateTime) {
        Locale locale = new Locale("ar");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm",locale);
        return newFormat.format(new Date(dateTime));
    }

    public static String getFormattedDateSimple(String dateTime) {
        Locale locale = new Locale("ar");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy hh:mm",locale);
        return newFormat.format(new Date(dateTime));
    }

    public static String getFormattedDateSimple2(Long dateTime) {
        Locale locale = new Locale("ar");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy hh:mm",locale);
        return newFormat.format(new Date(dateTime));
    }

    public static int getGridSpanCount(Activity activity) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_playlist_width);
        return Math.round(screenWidth / cellWidth);
    }

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void showDialogAbout(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void rateAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static String convertTextNumbersToArabic (String text) {

        if(text == null) return "";
        return text.replace("0","٠").replace("1","١").replace("2","٢").replace("3","٣")
                .replace("4","٤").replace("5","٥").replace("6","٦")
                .replace("7","٧").replace("8","٨").replace("9","٩")
                .replace("10","١٠");

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + "";
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return ctx.getString(R.string.app_version) + " " + info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return ctx.getString(R.string.version_unknown);
        }
    }

    public static String getVersionNamePlain(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return ctx.getString(R.string.version_unknown);
        }
    }

    public static String getDeviceID(Context context) {
        String deviceID = Build.SERIAL;
        if (deviceID == null || deviceID.trim().isEmpty() || deviceID.equalsIgnoreCase("unknown") || deviceID.equals("0")) {
            try {
                deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
            }
        }
        return deviceID;
    }

    public static DeviceInfo getDeviceInfo(Context context) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.device = Tools.getDeviceName();
        deviceInfo.os_version = Tools.getAndroidVersion();
        deviceInfo.app_version = Tools.getVersionCode(context) + " (" + Tools.getVersionNamePlain(context) + ")";
        deviceInfo.serial = Tools.getDeviceID(context);
        return deviceInfo;
    }

    public static String getNotificationType(Context ctx, String type) {
        if (type.equalsIgnoreCase(NotifType.VIDEO.name())) {
            return ctx.getString(R.string.type_video);
        } else if (type.equalsIgnoreCase(NotifType.PLAYLIST.name())) {
            return ctx.getString(R.string.type_playlist);
        } else {
            return null;
        }
    }

    public static void methodShare(Activity act, Video v) {
        try {
            /* if (v.isDraft()) {
                return;
            }*/
            // string to share
            StringBuilder sb = new StringBuilder();
            sb.append("شاهد الفيديو \'" + Tools.convertTextNumbersToArabic(v.Name) + "\'\n");
            sb.append("تطبيق \'" + act.getString(R.string.app_name) + "\'\n");
            sb.append(Constant.YOUTUBE_BASE_URL + v.URL);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, " محاضرة فقهية :  \'" + Tools.convertTextNumbersToArabic(v.Name) + "\'\n");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            act.startActivity(Intent.createChooser(sharingIntent, "شارك باستخدام"));
        } catch (Exception e) {
            Toast.makeText(act, "فشل العملية", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean needRequestPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

}
