package com.sheikh.hussein.abdallah.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.NotifType;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.room.table.NotificationEntity;
import com.sheikh.hussein.abdallah.utils.Tools;

import androidx.appcompat.app.AppCompatActivity;


public class ActivityDialogNotification extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";
    private static final String EXTRA_POSITION = "key.EXTRA_FROM_POSITION";

    // activity transition
    public static void navigate(Activity activity, NotificationEntity obj, Boolean from_notif, int position) {
        Intent i = navigateBase(activity, obj, from_notif);
        i.putExtra(EXTRA_POSITION, position);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, NotificationEntity obj, Boolean from_notif) {
        Intent i = new Intent(context, ActivityDialogNotification.class);
        i.putExtra(EXTRA_OBJECT, obj);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Boolean from_notif;
    private NotificationEntity notification;
    private Intent intent;
    private DAO dao;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_notification);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dao = AppDatabase.getDb(this).getDAO();

        notification = (NotificationEntity) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        // set notification as read
        notification.read = true;
        dao.insertNotification(notification);


        initComponent();
    }

    private void initComponent() {
        ((TextView) findViewById(R.id.title)).setText(notification.title);
        ((TextView) findViewById(R.id.content)).setText(notification.content);
        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDateSimple2(notification.created_at));
        ((TextView) findViewById(R.id.type)).setText(Tools.getNotificationType(this, notification.type));

        String image_url = null;
        String type = notification.type;
        intent = new Intent(this, ActivitySplash.class);
        if (type.equalsIgnoreCase(NotifType.VIDEO.name())) {
            image_url = Constant.getImageURLForNotification(notification.link);
            intent= new Intent(this, ActivityVideoDetails.class);
            intent.putExtra("key.EXTRA_OBJECT_ID", notification.obj_id);
            intent.putExtra("key.EXTRA_FROM_NOTIF", from_notif);

        } else if (type.equalsIgnoreCase(NotifType.PLAYLIST.name())) {
            image_url = Constant.getURLimgPlaylist(notification.image);
            Playlist playlist = new Playlist();
            playlist.CategoryId = notification.obj_id;
            intent = ActivityPlaylistDetails.navigateBase(this, playlist, from_notif);

        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }

        if (from_notif) {
            (findViewById(R.id.bt_delete)).setVisibility(View.GONE);
            if (ActivityMain.active && (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name()))) {
                ((LinearLayout) findViewById(R.id.lyt_action)).setVisibility(View.GONE);
            }
        } else {
            if (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name())) {
                (findViewById(R.id.bt_open)).setVisibility(View.GONE);
            }
            ((TextView) findViewById(R.id.dialog_title)).setText(null);
            ((ImageView) findViewById(R.id.logo)).setVisibility(View.GONE);
            ((View) findViewById(R.id.view_space)).setVisibility(View.GONE);
        }

        (findViewById(R.id.lyt_image)).setVisibility(View.GONE);
        if (image_url != null) {
            (findViewById(R.id.lyt_image)).setVisibility(View.VISIBLE);
            Tools.displayImage(this, ((ImageView) findViewById(R.id.image)), image_url);
        }

        ((ImageView) findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.bt_open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(intent);
            }
        });

        (findViewById(R.id.bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (!from_notif && position != -1) {
                    dao.deleteNotification(notification.id);
                    ActivityNotifications.getInstance().adapter.removeItem(position);
                    Snackbar.make(ActivityNotifications.getInstance().parent_view, R.string.delete_successfully, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}