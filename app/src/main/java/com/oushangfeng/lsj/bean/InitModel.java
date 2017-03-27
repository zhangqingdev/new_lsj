package com.oushangfeng.lsj.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zhangqing on 2017/3/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitModel {
    @JsonProperty("uuid")
    public String uuid;
    @JsonProperty("feedback")
    public String feedback;
    @JsonProperty("clientPrivateKey")
    public String clientPrivateKey;
    @JsonProperty("insiderPublicKey")
    public String insiderPublicKey;
    @JsonProperty("client")
    public ClientCheckUpdate client;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientCheckUpdate{
        @JsonProperty("update")
        public boolean update;
        @JsonProperty("download")
        public String download;
    }
	@JsonProperty("errorCode")
	public String errorCode;
	@JsonProperty("errorMsg")
	public String errorMsg;
}
