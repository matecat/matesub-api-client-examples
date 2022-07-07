package com.matesub.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.List;

/**
 * Use this class to retrieve the list of accepted languages. Those are divided into target and source languages and into default and autospotting languages.
 * The autospotting languages are those that support automatic translation.
 */
public class GetLanguages {

    private static final String GET_LANGUAGES_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/languages";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public GetLanguages( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public GetLanguagesResponse executeCall() throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( GET_LANGUAGES_ENDPOINT_URL )
                .get();

        GetLanguagesResponse getLanguagesResponse = null;

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                getLanguagesResponse = objectMapper.readValue( responseBody.string(), GetLanguagesResponse.class );
            }
        }

        return getLanguagesResponse;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GetLanguagesResponse(@JsonProperty("source") Languages source, @JsonProperty("target") Languages target) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Languages(@JsonProperty("autospotting") List<Language> autospotting, @JsonProperty("default") List<Language> defaultLanguages) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Language(@JsonProperty("id") String id, @JsonProperty("name") String name) {
        }
    }

}
