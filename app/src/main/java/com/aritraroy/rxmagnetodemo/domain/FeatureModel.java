package com.aritraroy.rxmagnetodemo.domain;

/**
 * Created by aritraroy on 19/02/17.
 */

public class FeatureModel {

    private int id;
    private String title;

    public FeatureModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "FeatureModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
