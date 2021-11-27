package com.steelparrot.freedecibel.model;

import com.google.gson.annotations.SerializedName;

public class YTItem {

    @SerializedName("thumbnail")
    private String m_thumbnail;

    @SerializedName("title")
    private String m_title;

    @SerializedName("duration")
    private String m_duration;

    @SerializedName("views")
    private Long m_views;

    @SerializedName("uploader")
    private String m_uploader;

    @SerializedName("url")
    private String m_url;

    @SerializedName("time_upload")
    private String m_time_upload;

    private boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public YTItem(String m_url, String m_thumbnail, String m_title, String m_duration, Long m_views, String m_uploader, String m_time_upload) {
        this.m_thumbnail = m_thumbnail;
        this.m_title = m_title;
        this.m_duration = m_duration;
        this.m_views = m_views;
        this.m_uploader = m_uploader;
        this.m_url = m_url;
        this.m_time_upload = m_time_upload;
    }

    public YTItem() {}

    public String getM_thumbnail() {
        return m_thumbnail;
    }

    public void setM_thumbnail(String m_thumbnail) {
        this.m_thumbnail = m_thumbnail;
    }

    public String getM_title() {
        return m_title;
    }

    public void setM_title(String m_title) {
        this.m_title = m_title;
    }

    public String getM_duration() {
        return m_duration;
    }

    public void setM_duration(String m_duration) {
        this.m_duration = m_duration;
    }

    public Long getM_views() {
        return m_views;
    }

    public void setM_views(Long m_views) {
        this.m_views = m_views;
    }

    public String getM_uploader() {
        return m_uploader;
    }

    public void setM_uploader(String m_uploader) {
        this.m_uploader = m_uploader;
    }

    public String getM_url() {
        return m_url;
    }

    public void setM_url(String m_url) {
        this.m_url = m_url;
    }


    public String getM_time_upload() {
        return m_time_upload;
    }

    public void setM_time_upload(String m_time_upload) {
        this.m_time_upload = m_time_upload;
    }


}
