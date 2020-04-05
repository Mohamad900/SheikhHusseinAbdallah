package com.sheikh.hussein.abdallah.room;


import com.sheikh.hussein.abdallah.room.table.NotificationEntity;
import com.sheikh.hussein.abdallah.room.table.VideoEntity;
import com.sheikh.hussein.abdallah.room.table.WatchedEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DAO {

    /* table video transaction ------------------------------------------------------------------ */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideo(VideoEntity video);

    @Query("DELETE FROM video WHERE id = :id")
    void deleteVideo(String id);

    @Query("DELETE FROM video")
    void deleteAllVideo();

    @Query("SELECT * FROM video ORDER BY saved_date DESC")
    List<VideoEntity> getAllVideo();

    @Query("SELECT * FROM video WHERE id = :id LIMIT 1")
    VideoEntity getVideo(String id);

    /* table notification transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(NotificationEntity notification);

    @Query("DELETE FROM notification WHERE id = :id")
    void deleteNotification(long id);

    @Query("DELETE FROM notification")
    void deleteAllNotification();

    @Query("SELECT * FROM notification ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<NotificationEntity> getNotificationByPage(int limit, int offset);

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    NotificationEntity getNotification(long id);

    @Query("SELECT COUNT(id) FROM notification WHERE read = 0")
    Integer getNotificationUnreadCount();

    @Query("SELECT COUNT(id) FROM notification")
    Integer getNotificationCount();

    /* table watched transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWatched(WatchedEntity watched);

    @Query("SELECT COUNT(id) FROM watched WHERE id = :id")
    Integer countWatched(String id);

}
