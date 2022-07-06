package com.matesub.examples.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class to generate the api key hash to request a Json Web Token.
 */
public final class ApiKeyUtils {
    private ApiKeyUtils() {
    }

    /**
     * It calculates the api key hash using today date.
     *
     * @param apiKey the api key associated to your account.
     * @return the apy key hash that can be used to request a Json Web Token
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String calculateHash( UUID apiKey ) throws NoSuchAlgorithmException, InvalidKeyException {

        String todayDate = LocalDate.now().format( DateTimeFormatter.ofPattern( "yyyyMMdd" ) );

        byte[] todayDateHash      = hmac( apiKey.toString().getBytes( UTF_8 ), todayDate.getBytes( UTF_8 ) );
        byte[] doubleHashedApiKey = hmac( todayDateHash, apiKey.toString().getBytes( UTF_8 ) );

        return new String( Base64.getEncoder().encode( doubleHashedApiKey ), StandardCharsets.UTF_8 );

    }

    private static byte[] hmac( byte[] key, byte[] message ) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance( "HmacSHA256" );
        mac.init( new SecretKeySpec( key, "HmacSHA256" ) );
        return mac.doFinal( message );
    }
}
