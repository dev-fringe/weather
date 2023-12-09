
package com.api.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonPropertyOrder({
    "header",
    "body"
})
@Data
public class Response {

    @JsonProperty("header")
    private Header header;
    @JsonProperty("body")
    private Body body;
}
