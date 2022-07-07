package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.common.Project;
import com.matesub.examples.common.Target;
import com.matesub.examples.common.Template;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Use this class to start the project creation process.
 */
public class CreateProject {

    private static final String CREATE_PROJECT_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/project";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public CreateProject( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public CreateProjectResponse executeCall( CreateProjectRequest createProjectRequest ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        RequestBody req = RequestBody.create( objectMapper.writeValueAsString( createProjectRequest ), MediaType.parse( "application/json" ) );
        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( CREATE_PROJECT_ENDPOINT_URL )
                .put( req );

        CreateProjectResponse createProjectResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                createProjectResponse = objectMapper.readValue( responseBody.string(), CreateProjectResponse.class );
            }
        }

        return createProjectResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CreateProjectResponse(@JsonProperty("project") Project project,
                                        @JsonProperty("targets") List<Target> targets) {
    }

    public record CreateProjectRequest(@JsonProperty("workspace_id") String workspaceId,
                                       @JsonProperty("project") ProjectRequestBody project) {

        public record ProjectRequestBody(@JsonProperty("project_name") String projectName,
                                         @JsonProperty("source_lang") String sourceLang,
                                         @JsonProperty("file_hash") String fileHash,
                                         @JsonProperty("presets") Map<String, String> presets,
                                         @JsonProperty("original_file_name") String originalFileName,
                                         @JsonProperty("user_id") String userId,
                                         @JsonProperty("folder_id") String folderId,
                                         @JsonProperty("template") Template template) {

        }
    }
}
