package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.common.Template;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.List;

/**
 * Use this class to retrieve the list of templates associated with your workspace.
 */
public class GetTemplates {

    private static final String GET_TEMPLATES_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/templates/";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public GetTemplates( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public GetTemplatesResponse executeCall( String workspaceId ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( GET_TEMPLATES_ENDPOINT_URL + workspaceId )
                .get();

        GetTemplatesResponse getTemplatesResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                getTemplatesResponse = objectMapper.readValue( responseBody.string(), GetTemplatesResponse.class );
            }
        }

        return getTemplatesResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GetTemplatesResponse(@JsonProperty("templates") List<TemplateWrapper> templates) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TemplateWrapper(@JsonProperty("template_id") String templateId,
                                      @JsonProperty("workspace_id") String workspaceId,
                                      @JsonProperty("type") String type,
                                      @JsonProperty("active") Boolean active,
                                      @JsonProperty("template") Template template) {
        }
    }
}
