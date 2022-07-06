package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.common.TargetStatus;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Use this class to retrieve the target progress status.
 */
public class GetTargetStatus {

    private static final String GET_TARGET_STATUS_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/stats/project/";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public GetTargetStatus( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public GetTargetStatusResponse executeCall( String projectId, String targetId ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( GET_TARGET_STATUS_ENDPOINT_URL + projectId + "/" + targetId )
                .get();

        GetTargetStatusResponse getTargetStatusResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                getTargetStatusResponse = objectMapper.readValue( responseBody.string(), GetTargetStatusResponse.class );
            }
        }

        return getTargetStatusResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GetTargetStatusResponse(@JsonProperty("stats") TargetStatus stats) {
    }
}
