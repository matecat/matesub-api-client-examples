package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Definition of the template entity.
 *
 * @param templateId                  identifier of the template
 * @param id                          int identifier for the template
 * @param name                        name of the template
 * @param minDuration                 min subtitle duration
 * @param minDurationUnit             unit of the minDuration
 * @param maxDuration                 max subtitle duration
 * @param maxDurationUnit             unit of the maxDuration
 * @param maxLines                    max lines in the subtitles
 * @param linesLimit
 * @param minGap                      min gap between subtitles
 * @param minGapUnit                  unit of the minGap
 * @param fontSizeRatio
 * @param distance
 * @param cpl                         default characters per line on the subtitles
 * @param cps                         default characters per seconds on the subtitles
 * @param settings                    additional metadata for the template
 * @param languageGuidelineExceptions override default cpl and cps by language code
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Template(@JsonProperty("template_id") String templateId,
                       @JsonProperty("id") Integer id,
                       @JsonProperty("name") String name,
                       @JsonProperty("min_duration") Integer minDuration,
                       @JsonProperty("min_duration_unit") String minDurationUnit,
                       @JsonProperty("max_duration") Integer maxDuration,
                       @JsonProperty("max_duration_unit") String maxDurationUnit,
                       @JsonProperty("max_lines") Integer maxLines,
                       @JsonProperty("lines_limit") Integer linesLimit,
                       @JsonProperty("min_gap") Integer minGap,
                       @JsonProperty("min_gap_unit") String minGapUnit,
                       @JsonProperty("font_size_ratio") Integer fontSizeRatio,
                       @JsonProperty("distance") String distance,
                       @JsonProperty("cpl") Integer cpl,
                       @JsonProperty("cps") Integer cps,
                       @JsonProperty("settings") Map<String, String> settings,
                       @JsonProperty("language_guidelines_exceptions") List<LanguageGuidelineExceptions> languageGuidelineExceptions
) {
    /**
     * Entity to override the default templates cps and cpl for the given language code.
     *
     * @param code language code to override
     * @param cpl  characters per line on the subtitles for the defined language code
     * @param cps  characters per seconds on the subtitles for the defined language code
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LanguageGuidelineExceptions(@JsonProperty("code") String code,
                                              @JsonProperty("cpl") Integer cpl,
                                              @JsonProperty("cps") Integer cps) {

    }
}
