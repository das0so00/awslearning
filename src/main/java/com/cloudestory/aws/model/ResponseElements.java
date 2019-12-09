
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
    "x-amz-request-id",
    "x-amz-id-2"
})
public class ResponseElements {

    @JsonProperty("x-amz-request-id")
    private String xAmzRequestId;
    @JsonProperty("x-amz-id-2")
    private String xAmzId2;
    @JsonIgnore
    private Map<String, java.lang.Object> additionalProperties = new HashMap<String, java.lang.Object>();

    @JsonProperty("x-amz-request-id")
    public String getXAmzRequestId() {
        return xAmzRequestId;
    }

    @JsonProperty("x-amz-request-id")
    public void setXAmzRequestId(String xAmzRequestId) {
        this.xAmzRequestId = xAmzRequestId;
    }

    @JsonProperty("x-amz-id-2")
    public String getXAmzId2() {
        return xAmzId2;
    }

    @JsonProperty("x-amz-id-2")
    public void setXAmzId2(String xAmzId2) {
        this.xAmzId2 = xAmzId2;
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
