package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Definition of Project entity
 *
 * @param projectName             the name of the project
 * @param projectId               the project unique identifier
 * @param createDate              the project creation date
 * @param sourceLanguageName      the language name of the video
 * @param sourceLanguageDirection direction of the video source language code (ltr, rtl)
 * @param bucketName              the S3 bucket hosting the optimized video file
 * @param publicUrl               public base url of the S3 bucket hosting the optimized video file
 * @param fileHash                ETag of the video file
 * @param sourceLang              the video language code
 * @param duration                video duration in seconds
 * @param durationMillis          video duration in milliseconds
 * @param segmentDuration         video segment duration (HLS)
 * @param frameRate               video frame rate
 * @param width                   video width pixels
 * @param height                  video height pixels
 * @param fileSize                optimized file size in bytes
 * @param presets                 map containing projects metadata
 * @param status                  project status
 * @param originalFileName        original video file name
 * @param userId                  identifier of the user that created the project
 * @param folderId                identifier of the folder containing the project
 * @param totalRawWords           the sum of the total words in all projects targets
 * @param targetLanguages         the list of language codes of the projects targets
 * @param template                the template associated with the project
 * @param targets                 list of targets for the project
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Project(@JsonProperty("project_name") String projectName,
                      @JsonProperty("project_id") String projectId,
                      @JsonProperty("create_date") Instant createDate,
                      @JsonProperty("source_language_name") String sourceLanguageName,
                      @JsonProperty("source_language_direction") String sourceLanguageDirection,
                      @JsonProperty("bucket_name") String bucketName,
                      @JsonProperty("public_url") String publicUrl,
                      @JsonProperty("file_hash") String fileHash,
                      @JsonProperty("source_lang") String sourceLang,
                      @JsonProperty("duration") Integer duration,
                      @JsonProperty("duration_millis") Integer durationMillis,
                      @JsonProperty("segment_duration") String segmentDuration,
                      @JsonProperty("frame_rate") String frameRate,
                      @JsonProperty("width") Integer width,
                      @JsonProperty("height") Integer height,
                      @JsonProperty("file_size") Integer fileSize,
                      @JsonProperty("presets") Map<String, String> presets,
                      @JsonProperty("status") String status,
                      @JsonProperty("original_file_name") String originalFileName,
                      @JsonProperty("user_id") String userId,
                      @JsonProperty("folder_id") String folderId,
                      @JsonProperty("total_raw_words") Integer totalRawWords,
                      @JsonProperty("target_languages") List<String> targetLanguages,
                      @JsonProperty("template") Template template,
                      @JsonProperty("targets") List<Target> targets) {
}
