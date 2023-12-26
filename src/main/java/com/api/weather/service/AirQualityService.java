package com.api.weather.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.weather.model.ListAirQualityByDistrictService;
import com.api.weather.model.row;
import com.thoughtworks.xstream.XStream;

/**
 * 실시간 대기환경정보
 */
@Service
public class AirQualityService {

//	public row xml() {
//		String xml = new RestTemplate().getForObject("http://openapi.seoul.go.kr:8088/6b42544f6d69326d3434746746546a/xml/ListAirQualityByDistrictService/1/100/",String.class);
//		XStream x = new XStream();
//		x.processAnnotations(ListAirQualityByDistrictService.class);
//		x.allowTypesByWildcard(new String[] { "com.api.weather.**", });
//		ListAirQualityByDistrictService s = (ListAirQualityByDistrictService) x.fromXML(xml);
//		List<row> rs = s.getRow();
//		row res = null;
//		for (row r : rs) {
//			if(r.getMSRSTENAME().contains("은평구")) {
//				res = r;
//			}
//		}
//		return res;
//	}

	public row getrow(String gu) {
		String xml = new RestTemplate().getForObject("http://openapi.seoul.go.kr:8088/6b42544f6d69326d3434746746546a/xml/ListAirQualityByDistrictService/1/100/",String.class);
		XStream x = new XStream();
		x.processAnnotations(ListAirQualityByDistrictService.class);
		x.allowTypesByWildcard(new String[] { "com.api.weather.**", });
		ListAirQualityByDistrictService s = (ListAirQualityByDistrictService) x.fromXML(xml);
		List<row> rs = s.getRow();
		row res = null;
		for (row r : rs) {
			if(r.getMSRSTENAME().contains(gu)) {
				res = r;
			}
		}
		return res;
	}
}
