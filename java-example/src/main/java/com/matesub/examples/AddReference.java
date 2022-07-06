package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.AddReference.AddReferenceRequest.Reference;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Use this class to invoke the Add Project Reference endpoint.
 */
public class AddReference {

    private static final String ADD_REFERENCE_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/project/reference";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public AddReference( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public Reference executeCall( AddReferenceRequest addReferenceRequest ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        RequestBody req = RequestBody.create( objectMapper.writeValueAsString( addReferenceRequest ), MediaType.parse( "application/json" ) );
        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( ADD_REFERENCE_ENDPOINT_URL )
                .patch( req );

        Reference reference = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                reference = objectMapper.readValue( responseBody.string(), Reference.class );
            }
        }

        return reference;

    }

    public record AddReferenceRequest(@JsonProperty("user_id") String userId,
                                      @JsonProperty("reference") Reference reference) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Reference(@JsonProperty("project_id") String projectId,
                                @JsonProperty("reference_type") String referenceType,
                                @JsonProperty("file") FileReference file) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record FileReference(@JsonProperty("reference_hash") String referenceHash,
                                    @JsonProperty("reference_lang") String referenceLang,
                                    @JsonProperty("reference_bucket") String referenceBucket,
                                    @JsonProperty("reference_name") String referenceName) {
        }
    }
}
