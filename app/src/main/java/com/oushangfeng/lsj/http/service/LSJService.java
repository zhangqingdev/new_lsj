package com.oushangfeng.lsj.http.service;

import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.bean.InitModel;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface LSJService {
    /**
     *  获取首页内容
     * @param imei

     * @return
     */
    @GET("app/index/{imei}")
    Observable<IndexPageModel> getNewsList(
            @Path("imei") String imei);

    /**
     *  获取首页banner内容
     * @param imei

     * @return
     */
    @GET("app/index/banner/{imei}/{pageSize}")
    Observable<List<IndexPageBannerModel>> getIndexBannerList(
            @Path("imei") String imei,@Path("pageSize")String pageSize);


    /**
     * 获取首页分页内容
     * @param imei
     * @param lastMaxId
     * @param pageSize
     * @return
     */
    @GET("app/index/list/{imei}/{lastMaxId}/{pageSize}")
    Observable<IndexPageModel> getLastNewsList(
            @Path("imei") String imei,
            @Path("lastMaxId") String lastMaxId,
            @Path("pageSize") String pageSize);


    /**
     * 获取图片墙数据接口
      * @param imei
     * @param lastMaxId
     * @param pageSize
     * @return
     */
    @GET("app/photo/list/{imei}/{lastMaxId}/{pageSize}")
    Observable<IndexPhotoModel> getPhotoList(
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

    /**
     * 初始化接口
     * @return
     */
    @POST("app/default")
    Observable<InitModel> initEnv(@Body RequestBody body);

	/**
	 * 下载APK
	 */
	@Streaming
	@GET
	Observable<ResponseBody> downloadApk(@Url String fileUrl);
}
