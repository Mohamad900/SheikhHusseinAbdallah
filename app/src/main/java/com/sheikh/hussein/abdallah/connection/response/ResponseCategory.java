package com.sheikh.hussein.abdallah.connection.response;

import com.sheikh.hussein.abdallah.model.Playlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseCategory implements Serializable {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Playlist> categories = new ArrayList<>();

}
