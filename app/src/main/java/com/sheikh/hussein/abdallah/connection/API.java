package com.sheikh.hussein.abdallah.connection;


import com.sheikh.hussein.abdallah.connection.response.ResponseDevice;
import com.sheikh.hussein.abdallah.connection.response.ResponseHome;
import com.sheikh.hussein.abdallah.connection.response.ResponseInfo;
import com.sheikh.hussein.abdallah.connection.response.ResponseCategory;
import com.sheikh.hussein.abdallah.connection.response.ResponseCategoryDetails;
import com.sheikh.hussein.abdallah.connection.response.ResponseVideo;
import com.sheikh.hussein.abdallah.connection.response.ResponseVideoDetails;
import com.sheikh.hussein.abdallah.data.Constant;
import com.sheikh.hussein.abdallah.model.DeviceInfo;
import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.SearchBody;
import com.sheikh.hussein.abdallah.model.Settings;
import com.sheikh.hussein.abdallah.model.Video;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: SheikhHusseinAbdallah";
    //String SECURITY = "Security: " + Constant.SECURITY_CODE;

    @Headers({CACHE, AGENT})
    @GET("services/info")
    Call<ResponseInfo> getInfo(
            @Query("version") Integer version
    );

    @Headers({CACHE, AGENT})
    @POST("Settings/IsAppUpToDate")
    Call<Boolean> isAppUpToDate(
            @Body Settings settings
    );

    /* Fcm API ----------------------------------------------------------- */
    @Headers({CACHE, AGENT})
    @POST("AndroidApp/RegisterDeviceForFCM")
    Call<ResponseDevice> registerDevice(
            @Body DeviceInfo deviceInfo
    );

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetHome")
    Call<ResponseHome> getHome();

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetAppVideoDetails")
    Call<ResponseVideoDetails> getVideoDetails(
            @Body Video video
    );

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetCategories")
    Call<ResponseCategory> getListPlaylist();

    @Headers({CACHE, AGENT})
    @GET("services/listPlaylistName")
    Call<ResponseCategory> getListPlaylistName();

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetVideosByCategoryId")
    Call<ResponseVideo> getListVideo(
            @Body Playlist category
    );

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetAllVideos")
    Call<ResponseVideo> getAllVideos();

    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetFilteredVideos")
    Call<ResponseVideo> getListVideoAdv(
            @Body SearchBody searchBody
    );



    @Headers({CACHE, AGENT})
    @POST("AndroidApp/GetCategoryDetailsById")
    Call<ResponseCategoryDetails> getPlaylistDetails(
            @Body Playlist category
    );

}
