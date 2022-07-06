package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

import java.util.List;

/**
 * Use this class to specify the target languages of the project.
 */
public class AddTarget {

    private static final String ADD_TARGET_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/project";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public AddTarget( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public CreateProject.CreateProjectResponse executeCall( AddTargetRequest addTargetRequest ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        RequestBody req = RequestBody.create( objectMapper.writeValueAsString( addTargetRequest ), MediaType.parse( "application/json" ) );
        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( ADD_TARGET_ENDPOINT_URL )
                .patch( req );

        CreateProject.CreateProjectResponse createProjectResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                createProjectResponse = objectMapper.readValue( responseBody.string(), CreateProject.CreateProjectResponse.class );
            }
        }

        return createProjectResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddTargetRequest(@JsonProperty("user_id") String userId,
                                   @JsonProperty("new_target") NewTargetContainer newTargetContainer) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record NewTargetContainer(@JsonProperty("project_id") String projectId,
                                         @JsonProperty("targets") List<NewTarget> targets) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record NewTarget(@JsonProperty("auto_spotting") Boolean autoSpotting,
                                @JsonProperty("target_language") String targetLanguage) {
        }
    }
}
