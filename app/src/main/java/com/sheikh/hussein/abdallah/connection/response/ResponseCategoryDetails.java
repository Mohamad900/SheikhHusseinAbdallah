package com.sheikh.hussein.abdallah.connection.response;


import com.sheikh.hussein.abdallah.model.Playlist;

import java.io.Serializable;

public class ResponseCategoryDetails implements Serializable {

    public String status = "";
    public Playlist category = new Playlist();

}
