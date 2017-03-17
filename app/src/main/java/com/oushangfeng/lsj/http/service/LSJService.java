package com.oushangfeng.lsj.http.service;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface LSJService {
    /**
     *  获取首页内容
     * @param banner_num
     * @param recomdListSize
     * @param articleListSize
     * @return
     */
    @GET("insider/app/index")
    Observable<String> getNewsList(
            @Query("banner") String banner_num,
            @Query("recomdListSize") String recomdListSize,
            @Query("articleListSize") int articleListSize);

    /**
     *  微信登陆接口
     * @param code
     * @param appId
     * @return
     */
    @POST("insider/weixin/login")
    Observable<String> getWinXinLogin(
            @Query("code") String code,
            @Query("appId") int appId);

    /**
     *  获取首页三图：增量获取
     * @param offsetId
     * @param pageSSize
     * @param articleListSize
     * @return
     */
    @POST("insider/app/article")
    Observable<String> getArticle(
            @Query("offsetId") String offsetId,
            @Query("pageSize") String pageSSize,
            @Query("uid") int articleListSize);

    /**
     *更新token接口
     * @param uid
     * @param token
     * @param signKey
     * @return
     */
    @POST("insider/app/token/flush/{uid}")
    Observable<String> getUpdateToken(
            @Path("uid") String uid,
            @Query("token") String token,
            @Query("signKey") String signKey);
}
