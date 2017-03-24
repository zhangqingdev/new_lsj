package com.oushangfeng.lsj.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangqing on 2017/3/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexPhotoModel {

    @JsonProperty("list")
    public List<IndexPhotoModel.PhotoModel> list;
//    @JsonProperty("status")
//    public int status;
//    @JsonProperty("msg")
//    public String msg;
    @JsonProperty("lastMaxId")
    public int lastMaxId;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhotoModel{
        @JsonProperty("desc")
        private String desc;
        @JsonProperty("title")
        private String title;
        @JsonProperty("laud")
        private int laud;
        @JsonProperty("comm")
        private int comm;
        @JsonProperty("id")
        private int id;
        @JsonProperty("publishDate")
        private String publishDate;
        @JsonProperty("url")
        private String url;
        @JsonProperty("img")
        private List<?> img;
    }
}
