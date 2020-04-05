package com.sheikh.hussein.abdallah.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchBody implements Serializable {

    public int page = 1;
    public int count = 10;
    public String q = "";
    public String category_id;
    public Set<Tag> tags = new HashSet<>();

    public SearchBody() {
    }

    public SearchBody(String q) {
        this.q = q;
    }
}
