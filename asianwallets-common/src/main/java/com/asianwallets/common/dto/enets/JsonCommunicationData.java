package com.asianwallets.common.dto.enets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@JsonInclude(Include.NON_NULL)
@Data
public class JsonCommunicationData {

    @JsonProperty("type")
    private String communicationType;

    @JsonProperty("category")
    private String communicationCategory;

    @JsonProperty("destination")
    private String communicationDestination;

    @JsonProperty("addon")
    private Map<String, String> addon;

    public JsonCommunicationData() {
    }
}