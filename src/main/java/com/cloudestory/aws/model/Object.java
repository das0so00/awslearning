
package com.cloudestory.aws.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "key",
    "size",
    "eTag",
    "sequencer"
})
public class Object {

    @JsonProperty("key")
    private String key;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("eTag")
    private String eTag;
    @JsonProperty("sequencer")
    private String sequencer;
    @JsonIgnore
    private Map<String, java.lang.Object> additionalProperties = new HashMap<String, java.lang.Object>();

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    @JsonProperty("eTag")
    public String getETag() {
        return eTag;
    }

    @JsonProperty("eTag")
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @JsonProperty("sequencer")
    public String getSequencer() {
        return sequencer;
    }

    @JsonProperty("sequencer")
    public void setSequencer(String sequencer) {
        this.sequencer = sequencer;
    }

    @JsonAnyGetter
    public Map<String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }

}
