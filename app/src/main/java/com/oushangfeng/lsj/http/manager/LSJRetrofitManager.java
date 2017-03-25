package com.oushangfeng.lsj.http.manager;

import android.util.Log;
import android.util.SparseArray;
import com.oushangfeng.lsj.app.App;
import com.oushangfeng.lsj.base.BaseSchedulerTransformer;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.bean.NeteastNewsSummary;
import com.oushangfeng.lsj.http.Api;
import com.oushangfeng.lsj.http.HostType;
import com.oushangfeng.lsj.http.service.LSJService;
import com.oushangfeng.lsj.utils.NetUtil;
import com.oushangfeng.lsj.utils.Utils;
import com.socks.library.KLog;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Path;
import rx.Observable;

public class LSJRetrofitManager {

    // 管理不同HostType的单例
    private static SparseArray<LSJRetrofitManager> sInstanceManager = new SparseArray<>(HostType.TYPE_COUNT);
    // 设缓存有效期为两天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    // 30秒内直接读缓存
    private static final long CACHE_AGE_SEC = 0;

    private static volatile OkHttpClient sOkHttpClient;
    private LSJService mNewsService;
    // 云端响应头拦截S器，用来配置缓存策略
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            // 在这里统一配置请求头缓存策略以及响应头缓存策略
            if (NetUtil.isConnected(App.getContext())) {
                // 在有网的情况下CACHE_AGE_SEC秒内读缓存，大于CACHE_AGE_SEC秒后会重新请求数据
                request = request.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + CACHE_AGE_SEC).build();
                Response response = chain.proceed(request);
                return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + CACHE_AGE_SEC).build();
            } else {
                // 无网情况下CACHE_STALE_SEC秒内读取缓存，大于CACHE_STALE_SEC秒缓存无效报504
                request = request.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC).build();
                Response response = chain.proceed(request);
                return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC).build();
            }

        }
    };

  private ParamsInterceptor paramsInterceptor=new ParamsInterceptor.Builder()
            .addParamsMap(Utils.getDeviceInfo(App.getContext()))
            .build();

    // 打印返回的json数据拦截器
    private Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();
            request = requestBuilder.build();
            final Response response = chain.proceed(request);
            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                     KLog.e("Couldn't decode the response body; charset is likely malformed.");
                    return response;
                }
            }
            if (contentLength != 0) {
                KLog.json(buffer.clone().readString(charset));
            }
            return response;
        }
    };


	private LSJRetrofitManager() {
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.getHost(hostType)).client(getOkHttpClient()).addConverterFactory(JacksonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
//        mNewsService = retrofit.create(LSJService.class);
//		Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.LSJ_BASE_HOST).client(getOkHttpClient()).addConverterFactory(JacksonConverterFactory.create())
//				.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
//		mNewsService = retrofit.create(LSJService.class);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.LSJ_BASE_HOST).client(getOkHttpClient()).addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        mNewsService = retrofit.create(LSJService.class);
	}

    /**
     * 获取单例
     *
     * @param hostType host类型
     * @return 实例
     */
    public static LSJRetrofitManager getInstance(int hostType) {
        LSJRetrofitManager instance = sInstanceManager.get(hostType);
        if (instance == null) {
            instance = new LSJRetrofitManager();
            sInstanceManager.put(hostType, instance);
            return instance;
        } else {
            return instance;
        }
    }

    // 配置OkHttpClient
    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (sOkHttpClient == null) {
                    // OkHttpClient配置是一样的,静态创建一次即可
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(App.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                    sOkHttpClient = new OkHttpClient.Builder().cache(cache).addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mRewriteCacheControlInterceptor).addInterceptor(mLoggingInterceptor).retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS).build();

                }
            }
        }
        return sOkHttpClient;
    }

    /**
     * 获取新闻列表
     * @param imei
     * @return
     */
    public Observable<IndexPageModel> getNewsListObservable(String imei){
        return  mNewsService.getNewsList(imei).compose(new BaseSchedulerTransformer<IndexPageModel>());
    }

    /**
     * 获取下一页新闻列表
     * @param imei
     * @return
     */
    public Observable<IndexPageModel> getgetLastNewsListObservable(String imei,String lastMaxId,String pageSize){
        return  mNewsService.getLastNewsList(imei,lastMaxId,pageSize).compose(new BaseSchedulerTransformer<IndexPageModel>());
    }

    /**
     * 获取首页banner数据
     * @param imei
     * @param pageSize
     * @return
     */
    public Observable<List<IndexPageBannerModel>> getIndexBannerList(String imei,String pageSize){
        return  mNewsService.getIndexBannerList(imei,pageSize).compose(new BaseSchedulerTransformer<List<IndexPageBannerModel>>());

    }
    /**
     * 获取图片列表
     * @param imei
     * @return
     */
    public Observable<IndexPhotoModel> getPhotoListObservable(String imei, String lastMaxId, String pageSize){
        return  mNewsService.getPhotoList(imei,lastMaxId,pageSize).compose(new BaseSchedulerTransformer<IndexPhotoModel>());
    }


    /**
     * 第三方登录接口
     * @param code
     * @param appId
     * @return
     */
   public Observable<String> getWinXinLoginObservable(String code,int appId){
       return mNewsService.getWinXinLogin(code,appId);
   }

    /**
     * 获取文章列表接口
     * @param offsetId
     * @param pageSSize
     * @param articleListSize
     * @return
     */
    public Observable<String> getArticleObservable(String offsetId, String pageSSize, int articleListSize){
        return mNewsService.getArticle(offsetId,pageSSize,articleListSize);
    }

    /**
     * 更新token
     * @param uid
     * @param token
     * @param signKey
     * @return
     */
    public Observable<String> getUpdateToken(String uid,String token, String signKey){
         return mNewsService.getUpdateToken(uid,token,signKey);
    }

}
