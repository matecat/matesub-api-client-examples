package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * Use this class to retrieve a Json Web Token to authenticate against the API.
 */
public class JsonWebToken {

    private static final String JSON_WEB_TOKEN_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/token";

    private final OkHttpClient okHttpClient;

    public JsonWebToken( OkHttpClient okHttpClient ) {
        this.okHttpClient = okHttpClient;
    }

    public JsonWebTokenResponse executeCall( JsonWebTokenRequest jsonWebTokenRequest ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RequestBody     req            = RequestBody.create( objectMapper.writeValueAsString( jsonWebTokenRequest ), MediaType.parse( "application/json" ) );
        Request.Builder requestBuilder = new Request.Builder().url( JSON_WEB_TOKEN_ENDPOINT_URL ).post( req );

        JsonWebTokenResponse jsonWebTokenResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                jsonWebTokenResponse = objectMapper.readValue( responseBody.string(), JsonWebTokenResponse.class );
            }
        }

        return jsonWebTokenResponse;
    }

    public record JsonWebTokenRequest(@JsonProperty("email") String email, @JsonProperty("api_key_hash") String apiKeyHash) {
    }

    public record JsonWebTokenResponse(@JsonProperty("user_id") String userId, @JsonProperty("jwt") String jwt) {
    }
}
