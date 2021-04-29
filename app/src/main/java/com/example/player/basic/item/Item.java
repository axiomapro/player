package com.example.player.basic.item;

public class Item {

    private int id,type;
    private String name,img,url,link,date;
    private boolean favourite,active;

    // Audio
    public Item(int id,int type,String name,String url,boolean favourite) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.url = url;
        this.favourite = favourite;
    }

    // Country
    public Item(int id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }

    // Clock
    public Item(int id,String name,String date,boolean active) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.active = active;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }

    public String getLink() {
        return link;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public String getDate() {
        return date;
    }

    public boolean isActive() {
        return active;
    }
}
