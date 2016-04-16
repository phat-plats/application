package com.phat_plats.scanitfortheplanet.search.model;


import java.io.Serializable;

public class QueryItem implements Serializable{
    public String name;
    public String upc;

    public QueryItem(String name, String upc) {
        this.name = name;
        this.upc = upc;
    }
}
