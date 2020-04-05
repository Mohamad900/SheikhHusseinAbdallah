package com.sheikh.hussein.abdallah.room;


import android.content.Context;

import com.sheikh.hussein.abdallah.room.table.NotificationEntity;
import com.sheikh.hussein.abdallah.room.table.VideoEntity;
import com.sheikh.hussein.abdallah.room.table.WatchedEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {VideoEntity.class, NotificationEntity.class, WatchedEntity.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO getDAO();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "vido_database")
                            .allowMainThreadQueries()
                            .addMigrations(MIGRATION_2_3)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration MIGRATION_2_3 = new Migration(4, 5){
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table Watched
            database.execSQL("CREATE TABLE watched (id TEXT NOT NULL, time INTEGER NOT NULL, PRIMARY KEY(id))");
        }
    };
}