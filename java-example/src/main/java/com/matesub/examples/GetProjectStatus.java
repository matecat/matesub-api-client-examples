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

import java.util.List;

/**
 * Use this class to retrieve the projects targets progress status.
 */
public class GetProjectStatus {

    private static final String GET_PROJECT_STATUS_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/stats/project/";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public GetProjectStatus( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public GetProjectStatusResponse executeCall( String projectId ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( GET_PROJECT_STATUS_ENDPOINT_URL + projectId )
                .get();

        GetProjectStatusResponse getProjectStatusResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                getProjectStatusResponse = objectMapper.readValue( responseBody.string(), GetProjectStatusResponse.class );
            }
        }

        return getProjectStatusResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GetProjectStatusResponse(@JsonProperty("stats") List<TargetStatus> stats) {
    }
}
