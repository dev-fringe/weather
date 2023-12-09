package com.api.weather.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.api.weather.model.Body;
import com.api.weather.model.Grid;
import com.api.weather.model.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Service
public class WeatherDataService {
	// api
	@SneakyThrows
	public Map getWeatherData(Grid g) {
		String serviceKey = "iwEVyz24MuJlBvwmLqeWm63lq2lIjQRKvr6ExVoAt925m6B4ypQ2gEOieibt1pxcu9JJ6bYIvUodaZUNU+JIRg==";
		String cburl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		String[] base = getBaseDateTime(); // 현재 날짜, 시간을 기준으로 조회할 날짜, 시간 가져옴
		String u = String.format("%s?ServiceKey=%s&pageNo=%s&numOfRows=%s&dataType=%s&base_date=%s&base_time=%s&nx=%s&ny=%s", cburl,URLEncoder.encode(serviceKey, "utf-8"), "1", "11", "json", base[0], base[1], g.getX(), g.getY());
		HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(URI.create(u)).build(), HttpResponse.BodyHandlers.ofString());
		Map<String, Map> map = new ObjectMapper().readValue(response.body(), Map.class);
		Map m = map.get("response");
		Body r = new ObjectMapper().convertValue(m.get("body"), Body.class);
		return customizingData(r.getItems().getItem());
	}

	// 발표 날짜, 시각 구하기
	public String[] getBaseDateTime() {
		String[] dateTime = new String[2]; // 발표 날짜, 시각 저장
		Date date = new Date(); // 오늘 날짜
		String today = new SimpleDateFormat("yyyyMMdd").format(date);
		int hh = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
		int mm = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));

		// 어제 날짜
		date = new Date(date.getTime() + (1000 * 60 * 60 * 24 * -1));
		String yesterday = new SimpleDateFormat("yyyyMMdd").format(date);

		System.out.println("today: " + today);
		// 시간대별 발표 날짜, 시각 처리
		if (hh < 2 || (hh == 2 && mm <= 10)) { // 0 ~ 2시 10분 사이
			dateTime[0] = yesterday;
			dateTime[1] = "2300";
		} else if (hh < 5 || (hh == 5 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0200";
		} else if (hh < 8 || (hh == 8 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0500";
		} else if (hh < 11 || (hh == 11 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0800";
		} else if (hh < 14 || (hh == 14 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1100";
		} else if (hh < 17 || (hh == 17 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1400";
		} else if (hh < 20 || (hh == 20 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1700";
		} else if (hh < 23 || (hh == 23 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "2000";
		} else {
			dateTime[0] = today;
			dateTime[1] = "2300";
		}
		return dateTime;
	}

	// 데이터 커스터마이징
	@SneakyThrows
	public Map customizingData(List<Item> items) {
		Map map = new HashMap();
		for (Item i : items) {
			map.put("forecastDateTime", i.getFcstDate()+i.getFcstTime());
			String c = getCategory(i.getCategory());
			if(c != null) {
				map.put(c, getFcstValue(i.getCategory(), i.getFcstValue()));
			}
		}
		// 최종 결과값 출력
		return map;
	}

	// 카테고리 항목과 항목명 매칭
	public String getCategory(String key) {
			if (key.equals("POP"))
				return "Rainfall";
			else if (key.equals("PCP"))
				return "Precipitation";
			else if (key.equals("PTY"))
				return "Precipitation types";
			else if (key.equals("REH"))
				return "Humidity";
			else if (key.equals("SNO"))
				return "Snowfall";
			else if (key.equals("SKY"))
				return "Sky condition";
			else if (key.equals("TMP"))
				return "Temperature";
			else if (key.equals("UUU"))
				return "Wind speed (East-West component)";
			else if (key.equals("VVV"))
				return "Wind Speed (South-North Components)";
			else if (key.equals("VEC"))
				return "wind direction";
			else if (key.equals("WSD"))
				return "wind speed";
			else
				return null;
	}
	// 카테고리에 따른 값 커스터마이징
	public String getFcstValue(String category, String value) {
		String result = value;
		float val = 0;
		if (result.matches("[+-]?\\d*(\\.\\d+)?"))// 숫자일 때
			val = Float.parseFloat(value);

		if (category.equals("POP"))
			result += "%";
		else if (category.equals("PTY")) {
			if (result.equals("0"))
				result = "no";
			else if (result.equals("1"))
				result = "rain";
			else if (result.equals("2"))
				result = "rain/snow";
			else if (result.equals("3"))
				result = "snow";
			else
				result = "shower";
		} else if (category.equals("REH"))
			result += "%";
		else if (category.equals("SKY")) {
			if (val >= 0.0f && val < 6.0f)
				result = "sunny";// 맑음
			else if (val >= 6.0f && val < 9.0f)
				result = "cloudy(a lot of clouds)";// 구름많음
			else if (val >= 9.0f && val < 11.0f)
				result = "cloudy(rain)";// 흐림
		} else if (category.equals("TMP"))
			result += "℃";
		else if (category.equals("UUU")) {
			if (val > 0)
				result = "East " + Float.toString(Math.abs(val)) + "m/s";
			else
				result = "West " + Float.toString(Math.abs(val)) + "m/s";
		} else if (category.equals("VVV")) {
			if (val > 0)
				result = "North " + Float.toString(Math.abs(val)) + "m/s";
			else
				result = "South " + Float.toString(Math.abs(val)) + "m/s";
		} else if (category.equals("VEC")) {
			if (val >= 0.0f && val < 45.0f)
				result = "N-NE";
			else if (val >= 45.0f && val < 90.0f)
				result = "NE-E";
			else if (val >= 90.0f && val < 135.0f)
				result = "E-SE";
			else if (val >= 135.0f && val < 180.0f)
				result = "SE-S";
			else if (val >= 180.0f && val < 225.0f)
				result = "S-SW";
			else if (val >= 225.0f && val < 270.0f)
				result = "SW-W";
			else if (val >= 270.0f && val < 315.0f)
				result = "W-NW";
			else
				result = "NW-N";
		} else if (category.equals("WSD")) {
			if (val >= 0.0f && val < 4.0f)
				result += "m/s";
			else if (val >= 4.0f && val < 9.0f)
				result += "m/s (little strong)";
			else if (val >= 9.0f && val < 14.0f)
				result += "m/s (strong)";
			else
				result += "m/s (very strong)";
		} else {
			if (value.equals("강수없음"))
				result = "TYPE_RAIN_NO";
			else if (value.equals("적설없음"))
				result = "TYPE_SNOW_NO";
		}
		return result;
	}

}
