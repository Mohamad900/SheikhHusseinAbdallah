package com.sheikh.hussein.abdallah.data;

import com.sheikh.hussein.abdallah.model.Video;

public class Constant {

    /**
     * -------------------- EDIT THIS WITH YOURS -------------------------------------------------
     */

    // Edit WEB_URL with your url. Make sure you have backslash('/') in the end url
    //public static String WEB_URL = "http://demo.dream-space.web.id/vido_panel/";
    public static String WEB_URL = "http://cmshusseinabdallah.com/";
    public static String WEB_URL_API = "http://cmshusseinabdallah.com/api/";

    /* [ IMPORTANT ] be careful when edit this security code */
    /* This string must be same with security code at Server, if its different android unable to submit order */
    //public static final String SECURITY_CODE = "8V06LupAaMBLtQqyqTxmcN42nn27FlejvaoSM3zXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";


    /**
     * ------------------- DON'T EDIT THIS -------------------------------------------------------
     */

    public static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    // this limit value used for give pagination (request and display) to decrease payload
    public static int PLAYLIST_PER_REQUEST = 20;
    public static int VIDEO_PER_REQUEST = 20;
    public static int NOTIFICATION_PAGE = 20;

    // retry load image notification
    public static int LOAD_IMAGE_NOTIF_RETRY = 3;

    // Method get path to image
    public static String getURLimgVideo(String file_name) {
        return WEB_URL + "Resources/images/VideosImages/" + file_name;
    }

    public static String getURLimgPlaylist(String file_name) {
        return WEB_URL + "Resources/images/CategoriesImages/" + file_name;
    }

    public static String getImageURL(Video video) {
        if (video.Image == null || video.Image.equals("")) {
            return "https://img.youtube.com/vi/" + video.URL + "/0.jpg";
        }
        return getURLimgVideo(video.Image);
    }

    public static String getImageURLForNotification(String  link) {
            return "https://img.youtube.com/vi/" + link + "/0.jpg";
    }
/*    public static String getImageURL(VideoEntity video) {
        Video v = new Video();
        v.image = video.getImage();
        v.url = video.getUrl();
        return getImageURL(v);
    }
*/
    public static String getURLimgVideoNotif(String image) {
        if (image != null && image.startsWith("https:")) {
            return image;
        }
        return getURLimgVideo(image);
    }

}
