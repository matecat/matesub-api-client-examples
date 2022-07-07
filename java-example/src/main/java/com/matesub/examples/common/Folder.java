package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Definition of Folder entity.
 *
 * @param workspaceId   the identifier of the workspace associated with the folder
 * @param folderId      the identifier of the folder
 * @param createDate    the folder creation date
 * @param type          the folder type, it can be primary, custom, trash
 * @param name          the name of the folder
 * @param projectsCount the number of projects inside the folder
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Folder(@JsonProperty("workspace_id") String workspaceId,
                     @JsonProperty("folder_id") String folderId,
                     @JsonProperty("create_date") Instant createDate,
                     @JsonProperty("type") String type,
                     @JsonProperty("name") String name,
                     @JsonProperty("projects_count") Integer projectsCount) {
}
