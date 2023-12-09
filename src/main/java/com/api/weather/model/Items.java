
package com.api.weather.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonPropertyOrder({
    "item"
})
@Data
public class Items {

    @JsonProperty("item")
    private List<Item> item;
}
