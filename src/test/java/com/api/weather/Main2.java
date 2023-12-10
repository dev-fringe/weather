package com.api.weather;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.api.weather.model.ListAirQualityByDistrictService;
import com.api.weather.model.row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thoughtworks.xstream.XStream;

public class Main2 {
	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		String xml = new RestTemplate().getForObject("http://openapi.seoul.go.kr:8088/6b42544f6d69326d3434746746546a/xml/ListAirQualityByDistrictService/1/100/",String.class);
		XStream x = new XStream();
		x.processAnnotations(ListAirQualityByDistrictService.class);
		x.allowTypesByWildcard(new String[] { "com.api.weather.**", });
		ListAirQualityByDistrictService s = (ListAirQualityByDistrictService) x.fromXML(xml);
		List<row> rs = s.getRow();
		for (row r : rs) {
			if(r.getMSRSTENAME().contains("은평구")) {
				System.out.println(r);
			}
		}
	}
}
