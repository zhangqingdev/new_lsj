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
        public String desc;
        @JsonProperty("title")
        public String title;
        @JsonProperty("laud")
        public int laud;
        @JsonProperty("comm")
        public int comm;
        @JsonProperty("id")
        public int id;
        @JsonProperty("publishDate")
        public String publishDate;
        @JsonProperty("url")
        public String url;
        @JsonProperty("img")
        public List<?> img;
    }
}
