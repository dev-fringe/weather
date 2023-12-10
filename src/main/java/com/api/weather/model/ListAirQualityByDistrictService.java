package com.api.weather.model;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;

@XStreamAlias("ListAirQualityByDistrictService")
@Data
public class ListAirQualityByDistrictService { 
	private int list_total_count;
	private RESULT RESULT;
	@XStreamImplicit
	private List<row> row;
}
