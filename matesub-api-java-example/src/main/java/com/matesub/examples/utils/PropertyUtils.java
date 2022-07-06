package com.matesub.examples.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class to read the application.properties file.
 */
public final class PropertyUtils {

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load( new FileInputStream( new File( "src/main/resources/application.properties" ) ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private PropertyUtils() {
    }

    /**
     * Reads the value of the matesub.api.base.url from the application.properties file.
     *
     * @return the value associated to the matesub.api.base.url property
     */
    public static String mateSubBaseUrl() {
        return getProperty( "matesub.api.base.url" );
    }

    /**
     * Reads the value of the video.file.name from the application.properties file.
     *
     * @return the value associated to the video.file.name property
     */
    public static String videoFileName() {
        return getProperty( "video.file.name" );
    }

    /**
     * Reads the value of the video.file.source.language from the application.properties file.
     *
     * @return the value associated to the video.file.source.language property
     */
    public static String videoFileSourceLanguage() {
        return getProperty( "video.file.source.language" );
    }

    /**
     * Reads the value of the project.name from the application.properties file.
     *
     * @return the value associated to the project.name property
     */
    public static String projectName() {
        return getProperty( "project.name" );
    }

    /**
     * Reads the value of the api.key from the application.properties file.
     *
     * @return the value associated to the api.key property
     */
    public static String apiKey() {
        return getProperty( "api.key" );
    }

    /**
     * Reads the value of the api.account.email from the application.properties file.
     *
     * @return the value associated to the api.account.email property
     */
    public static String apiAccountEmail() {
        return getProperty( "api.account.email" );
    }

    /**
     * Reads the value of the project.targets from the application.properties file.
     *
     * @return the list of values associated to the project.targets property
     */
    public static List<String> projectTargets() {
        String commaSeparatedTargets = getProperty( "project.targets" );

        List<String> targets = List.of();

        if( commaSeparatedTargets != null ){
            targets = Arrays.stream( commaSeparatedTargets.split( "," ) ).collect( Collectors.toList() );
        }

        return targets;
    }

    /**
     * Reads the value of the reference.file.name from the application.properties file.
     *
     * @return the optional value associated to the reference.file.name property
     */
    public static Optional<String> referenceFileName() {
        return getOptionalProperty( "reference.file.name" );
    }

    /**
     * Reads the value of the reference.file.language from the application.properties file.
     *
     * @return the optional value associated to the reference.file.language property
     */
    public static Optional<String> referenceFileLanguage() {
        return getOptionalProperty( "reference.file.language" );
    }

    private static Optional<String> getOptionalProperty( String propertyName ) {
        String propertyValue = getProperty( propertyName );

        return propertyValue == null || "".equals( propertyValue ) ? Optional.empty() : Optional.of( propertyValue );
    }

    private static String getProperty( String propertyKey ) {
        return properties.getProperty( propertyKey );
    }
}
