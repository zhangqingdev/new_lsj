package com.oushangfeng.lsj.http.service;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface LSJService {
    /**
     *  获取首页内容
     * @param imei

     * @return
     */
    @GET("app/index/{imei}")
    Observable<String> getNewsList(
            @Path("imei") String imei);

    /**  *  获取首页分页内容  * @param imei  * @return  */ 
    @GET("app/index/{imei}/{lastMaxId}/{pageSize}")
    Observable<String> getLastNewsList( 
            @Path("imei") String imei, 
            @Path("lastMaxId") String lastMaxId, 
            @Path("pageSize") String pageSize);

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
