package com.sheikh.hussein.abdallah.room.table;



/*
 * To save watched
 */

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watched")
public class WatchedEntity {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "time")
    private long time = 0;

    public WatchedEntity(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
