package com.sheikh.hussein.abdallah.room.table;


/*
 * To save favorites video
 */

import com.sheikh.hussein.abdallah.model.Video;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video")
public class VideoEntity {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    private String name = "";

    @ColumnInfo(name = "url")
    private String url = "";

    @ColumnInfo(name = "image")
    private String image = "";

    @ColumnInfo(name = "duration")
    private String duration = "";

    @ColumnInfo(name = "saved_date")
    private long savedDate = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(long savedDate) {
        this.savedDate = savedDate;
    }

    public static VideoEntity entity(Video video) {
        VideoEntity entity = new VideoEntity();
        entity.setId(video.VideoId);
        entity.setName(video.Name);
        entity.setUrl(video.URL);
        entity.setImage(video.Image);
        entity.setDuration(video.Duration);
        entity.setSavedDate(System.currentTimeMillis());
        return entity;
    }
}
