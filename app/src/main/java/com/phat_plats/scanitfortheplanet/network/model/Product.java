package com.phat_plats.scanitfortheplanet.network.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gareth on 4/16/16.
 */
public class Product implements Serializable {
    public String upc;
    public String name;
    public int recyling;
    public String imageURL;
    public ArrayList<String> harmfulStuff;
    public ArrayList<Comment> comments;

    public Product(String upc, String name, int recyling, String imageURL, ArrayList<String> harmfulStuff) {
        this.upc = upc;
        this.name = name;
        this.recyling = recyling;
        this.imageURL = imageURL;
        this.harmfulStuff = harmfulStuff;
    }
}
