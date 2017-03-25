package com.oushangfeng.lsj.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by zhangqing on 2017/3/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexPageModel {
    @JsonProperty("banner")
    public List<IndexBanner> banner;
    @JsonProperty("indexContent")
    public ArticleContent indexContent;
//    @JsonProperty("status")
//    public int status;
//    @JsonProperty("msg")
//    public String msg;

    @JsonIgnoreProperties(ignoreUnknown = true)
     public static class ArticleContent{
         @JsonProperty("list")
         public List<IndexArticleContent> list;
         @JsonProperty("lastMaxId")
         public int lastMaxId;
     }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndexBanner{
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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class IndexArticleContent{
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
      @JsonProperty("showType")
      private int showType;

  }
}
