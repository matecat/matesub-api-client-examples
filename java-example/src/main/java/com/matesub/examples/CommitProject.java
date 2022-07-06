package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Use this class to commit the project creation request.
 */
public class CommitProject {

    private static final String CREATE_PROJECT_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/project";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public CommitProject( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public CreateProject.CreateProjectResponse executeCall( CommitProjectRequest commitProjectRequest ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        RequestBody req = RequestBody.create( objectMapper.writeValueAsString( commitProjectRequest ), MediaType.parse( "application/json" ) );
        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( CREATE_PROJECT_ENDPOINT_URL )
                .post( req );

        CreateProject.CreateProjectResponse createProjectResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                createProjectResponse = objectMapper.readValue( responseBody.string(), CreateProject.CreateProjectResponse.class );
            }
        }

        return createProjectResponse;
    }

    public record CommitProjectRequest(@JsonProperty("user_id") String userId, @JsonProperty("commit") Commit commit) {

        public record Commit(@JsonProperty("project_id") String project_id) {
        }
    }
}
