package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Target Status entity definition.
 *
 * @param targetId            identifier of the target
 * @param progress            target progress information
 * @param lastAccess          last time someone accessed the target
 * @param subtitlingCompleted set to true if the automatic subtitling is completed
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TargetStatus(@JsonProperty("target_id") String targetId,
                           @JsonProperty("progress") Progress progress,
                           @JsonProperty("last_access") String lastAccess,
                           @JsonProperty("subtitling_completed") Boolean subtitlingCompleted) {
    
    /**
     * Target subtitling Progress entity definition.
     *
     * @param completedSubtitles the number of the subtitles marked as completed
     * @param totalSubtitles     the total number of subtitles for the target
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Progress(@JsonProperty("completed_subtitles") String completedSubtitles,
                           @JsonProperty("total_subtitles") String totalSubtitles) {

    }
}
