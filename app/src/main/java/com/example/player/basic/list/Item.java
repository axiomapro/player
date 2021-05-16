package com.example.player.basic.list;

public class Item {

    private int id,type,icon,group;
    private String name,desc,img,url,link,date;
    private boolean favourite,active,visible;

    // Audio
    public Item(int id,int type,String name,String desc,String url,boolean favourite) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.desc = desc;
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

    // Menu
    public Item(int id,int group,String name,int icon,boolean active,boolean visible) {
        this.id = id;
        this.group = group;
        this.name = name;
        this.icon = icon;
        this.active = active;
        this.visible = visible;
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

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public int getIcon() {
        return icon;
    }

    public boolean isActive() {
        return active;
    }

    public String getDesc() {
        return desc;
    }

    public int getGroup() {
        return group;
    }

    public boolean isVisible() {
        return visible;
    }
}
