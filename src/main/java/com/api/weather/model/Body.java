
package com.api.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonPropertyOrder({
    "dataType",
    "items",
    "pageNo",
    "numOfRows",
    "totalCount"
})
@Data
public class Body {

    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("items")
    private Items items;
    @JsonProperty("pageNo")
    private Integer pageNo;
    @JsonProperty("numOfRows")
    private Integer numOfRows;
    @JsonProperty("totalCount")
    private Integer totalCount;



}
