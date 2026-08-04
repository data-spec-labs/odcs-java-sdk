package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Support / communication channel for the data contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SupportChannel(
        @JsonProperty("id") String id,
        @JsonProperty("channel") String channel,
        @JsonProperty("tool") SupportTool tool,
        @JsonProperty("scope") SupportScope scope,
        @JsonProperty("url") String url,
        @JsonProperty("invitationUrl") String invitationUrl,
        @JsonProperty("description") String description,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public SupportChannel {
    }
}
