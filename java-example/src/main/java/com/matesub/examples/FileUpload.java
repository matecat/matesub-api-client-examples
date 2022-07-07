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

import java.io.File;

/**
 * Use this class to upload a video or an SRT file from the videos or reference directory.
 */
public class FileUpload {

    private static final String FILE_UPLOAD_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/upload/";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public FileUpload( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public FileUploadResponse uploadVideo( String fileName, String fileLocation ) throws Exception {
        File video = new File( fileLocation );

        MediaType mediaType = MediaType.parse( "video/mp4" );

        return executeCall( video, fileName, mediaType );
    }

    public FileUploadResponse uploadSrt( String fileName, String fileLocation ) throws Exception {
        File srt = new File( fileLocation );

        MediaType mediaType = MediaType.parse( "text/plain" );

        return executeCall( srt, fileName, mediaType );
    }

    private FileUploadResponse executeCall( File file, String fileName, MediaType mediaType ) throws Exception {

        RequestBody requestBody = RequestBody.create( file, mediaType );

        Request request = new Request.Builder()
                .url( "https://beta.matesub.com/v1/upload/" + fileName )
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .put( requestBody )
                .build();

        FileUploadResponse fileUploadResponse = null;

        try ( Response response = this.okHttpClient.newCall( request ).execute() ) {
            ObjectMapper objectMapper = new ObjectMapper();

            ResponseBody responseBody = response.body();

            if( responseBody != null ){
                fileUploadResponse = objectMapper.readValue( responseBody.string(), FileUploadResponse.class );
            }
        }

        return fileUploadResponse;
    }

    public record FileUploadResponse(@JsonProperty("ETag") String eTag,
                                     @JsonProperty("Location") String location,
                                     @JsonProperty("Bucket") String bucket,
                                     @JsonProperty("Key") String key) {
    }
}
