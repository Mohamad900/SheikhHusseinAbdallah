package com.sheikh.hussein.abdallah.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionUtil {

    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /* Permission required for application */
    public static final String[] PERMISSION_ALL = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] getDeniedPermission(Activity act) {
        List<String> permissions = new ArrayList<>();
        for (int i = 0; i < PERMISSION_ALL.length; i++) {
            int status = act.checkSelfPermission(PERMISSION_ALL[i]);
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(PERMISSION_ALL[i]);
            }
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    public static boolean isGranted(Activity act, String permission) {
        if (!Tools.needRequestPermission()) return true;
        return (act.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isStorageGranted(Activity act) {
        return isGranted(act, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

}
