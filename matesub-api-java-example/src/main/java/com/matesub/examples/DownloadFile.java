package com.matesub.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matesub.examples.utils.JsonUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * Use this class to download the vtt file associated to the given target hash.
 */
public class DownloadFile {

    private static final String DOWNLOAD_FILE_ENDPOINT_URL = PropertyUtils.mateSubBaseUrl() + "/v1/export/";

    private final OkHttpClient okHttpClient;
    private final String       jsonWebToken;

    public DownloadFile( OkHttpClient okHttpClient, String jsonWebToken ) {
        this.okHttpClient = okHttpClient;
        this.jsonWebToken = jsonWebToken;
    }

    public void downloadVtt( String targetHash, String targetLanguage ) throws Exception {
        this.downloadFile( targetHash, targetLanguage, "vtt" );
    }

    public void downloadSrt( String targetHash, String targetLanguage ) throws Exception {
        this.downloadFile( targetHash, targetLanguage, "srt" );
    }

    private void downloadFile( String targetHash, String targetLanguage, String extension ) throws Exception {
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();

        Request.Builder requestBuilder = new Request.Builder()
                .header( "authorization", "Bearer " + this.jsonWebToken )
                .url( DOWNLOAD_FILE_ENDPOINT_URL + extension + "/" + targetHash )
                .get();

        try ( Response response = this.okHttpClient.newCall( requestBuilder.build() ).execute() ) {
            ResponseBody responseBody = response.body();

            int length = Integer.parseInt( Objects.requireNonNull( response.header( "CONTENT_LENGTH", "1" ) ) );

            File file = new File( "src/main/resources/downloads/" + targetLanguage + "_" + targetHash + "." + extension );

            InputStream inputStream = responseBody.byteStream();

            try ( FileOutputStream outputStream = new FileOutputStream( file, false ) ) {
                int    read;
                byte[] bytes = new byte[ length ];
                while ( ( read = inputStream.read( bytes ) ) != -1 ){
                    outputStream.write( bytes, 0, read );
                }
            }
        }
    }
}
