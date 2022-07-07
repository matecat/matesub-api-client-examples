package com.matesub.examples.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Workspace entity definition.
 *
 * @param userId      identifier of the user
 * @param owner       boolean value to check if the user identified by the userId is the workspace owner
 * @param workspaceId identifier of the workspace
 * @param createDate  workspace creation date
 * @param type        workspace type (personal, shareable)
 * @param name        workspace name
 * @param folders     list of {@link Folder} associated with the workspace
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Workspace(@JsonProperty("user_id") String userId,
                        @JsonProperty("is_owner") Boolean owner,
                        @JsonProperty("workspace_id") String workspaceId,
                        @JsonProperty("create_date") Instant createDate,
                        @JsonProperty("type") String type,
                        @JsonProperty("name") String name,
                        @JsonProperty("folders") List<Folder> folders) {
}
