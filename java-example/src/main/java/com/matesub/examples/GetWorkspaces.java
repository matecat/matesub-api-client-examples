package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.common.Workspace;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Use this class to retrieve the list of workspaces and folders associated with your account. The account identifier is taken from the Json Web Token.
 */
public class GetWorkspaces {

    private static final String GET_WORKSPACES_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/workspaces";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public GetWorkspaces( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public GetWorkspacesResponse executeCall() throws IOException {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( GET_WORKSPACES_ENDPOINT_URL )
                .get();

        GetWorkspacesResponse getWorkspacesResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                getWorkspacesResponse = objectMapper.readValue( responseBody.string(), GetWorkspacesResponse.class );
            }
        }

        return getWorkspacesResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GetWorkspacesResponse(@JsonProperty("user_id") String userId,
                                        @JsonProperty("owned") List<Workspace> owned,
                                        @JsonProperty("shared") List<Workspace> shared) {
    }
}
