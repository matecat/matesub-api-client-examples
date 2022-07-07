package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Definition of the target entity.
 *
 * @param targetId                identifier of the target
 * @param hashId
 * @param targetHashes            the hashes used to reach the target with a URL
 * @param targetLanguage          the language of the target
 * @param targetStatus            the status of the target
 * @param targetLanguageDirection the direction of the targets language (ltr or rtl)
 * @param targetLanguageName      the name of the targets language
 * @param createDate              targets creation date
 * @param projectId               identifier of the project
 * @param duration                video duration in seconds
 * @param durationMillis          video duration in milliseconds
 * @param segmentDuration         duration of the video segment (hls)
 * @param frameRate               video frame rate
 * @param width                   video width pixels
 * @param height                  video height pixels
 * @param fileSize                video file size
 * @param fileHash                video Etag
 * @param fileName                video file name
 * @param rawWords                count of the video raw words
 * @param targetLink              link to access the target
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Target(@JsonProperty("target_id") String targetId,
                     @JsonProperty("hash_id") String hashId,
                     @JsonProperty("target_hashes") List<String> targetHashes,
                     @JsonProperty("target_language") String targetLanguage,
                     @JsonProperty("target_status") String targetStatus,
                     @JsonProperty("target_language_direction") String targetLanguageDirection,
                     @JsonProperty("target_language_name") String targetLanguageName,
                     @JsonProperty("create_date") Instant createDate,
                     @JsonProperty("project_id") String projectId,
                     @JsonProperty("duration") Integer duration,
                     @JsonProperty("duration_millis") Integer durationMillis,
                     @JsonProperty("segment_duration") String segmentDuration,
                     @JsonProperty("frame_rate") String frameRate,
                     @JsonProperty("width") Integer width,
                     @JsonProperty("height") Integer height,
                     @JsonProperty("file_size") Integer fileSize,
                     @JsonProperty("file_hash") String fileHash,
                     @JsonProperty("file_name") String fileName,
                     @JsonProperty("raw_words") Integer rawWords,
                     @JsonProperty("target_link") String targetLink
) {
}
