
package com.api.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

//@JsonInclude(JsonInclude.Include.NSON_NULL)
@JsonPropertyOrder({
    "baseDate",
    "baseTime",
    "category",
    "fcstDate",
    "fcstTime",
    "fcstValue",
    "nx",
    "ny"
})
@Data
public class Item {

    @JsonProperty("baseDate")
    private String baseDate;
    @JsonProperty("baseTime")
    private String baseTime;
    @JsonProperty("category")
    private String category;
    @JsonProperty("fcstDate")
    private String fcstDate;
    @JsonProperty("fcstTime")
    private String fcstTime;
    @JsonProperty("fcstValue")
    private String fcstValue;
    @JsonProperty("nx")
    private Integer nx;
    @JsonProperty("ny")
    private Integer ny;
}
