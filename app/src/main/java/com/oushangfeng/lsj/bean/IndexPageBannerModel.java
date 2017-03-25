package com.oushangfeng.lsj.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangqing on 2017/3/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexPageBannerModel {
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
        public List<ImgEntity> img;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImgEntity {
        @JsonProperty("url")
        public String url;
        @JsonProperty("width")
        public int width;
        @JsonProperty("height")
        public int height;
    }
}
