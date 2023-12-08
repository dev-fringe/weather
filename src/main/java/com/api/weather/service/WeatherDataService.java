package com.api.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import com.api.weather.model.Grid;

import lombok.SneakyThrows;

@Service
public class WeatherDataService {
	// api
	@SneakyThrows
	public JSONObject getWeatherData(String serviceKey, String cburl, Grid g){
		System.out.println("getWeatherData_location: "+g);
		
		String numOfRows = "11";	// 페이지 당 출력 행 수
		String dataType = "json";	// 요청할 데이터 타입
		String[] base = getBaseDateTime();	//	현재 날짜, 시간을 기준으로 조회할 날짜, 시간 가져옴
		
		System.out.println(">>> baseDate: "+base[0]);	// 발표 날짜
		System.out.println(">>> baseDate: "+base[1]);	// 발표 시각(예보 시간 = 발표 시각 + 1)
		
		StringBuilder urlBuilder = new StringBuilder(cburl); // URL
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(serviceKey, "UTF-8")); // 공공데이터포털에서 받은 인증키
		urlBuilder
				.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); // 출력할 페이지번호
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode(numOfRows, "UTF-8")); // 한 페이지 결과 수
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "="
				+ URLEncoder.encode(dataType, "UTF-8")); // 요청자료형식(XML/JSON) Default: XML
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "="
				+ URLEncoder.encode(base[0], "UTF-8")); // 현재 시간 기준 발표 날짜
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "="
				+ URLEncoder.encode(base[1], "UTF-8")); // 현재 시간 기준 발표 시각(정시단위, 02:00~23:00, 3시간마다 업데이트)
		urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "="
				+ URLEncoder.encode(g.getX().toString(), "UTF-8")); // 예보지점의 X 좌표값(기상청 격자 좌표)
		urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "="
				+ URLEncoder.encode(g.getY().toString(), "UTF-8")); // 예보지점의 Y 좌표값(기상청 격자 좌표)
		
		System.out.println(urlBuilder.toString());
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
//		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		
		// 필요한 데이터 추출
		// response-body-items-item:[{},{},...]
		String wData = sb.toString();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(wData);
		JSONObject responseObj = (JSONObject) obj.get("response");
		if(responseObj.containsKey("body")) {
			JSONObject bodyObj = (JSONObject) responseObj.get("body");
			JSONObject itemsObj = (JSONObject) bodyObj.get("items");
			
			JSONArray itemJarr = (JSONArray) itemsObj.get("item");
			
			return customizingData(itemJarr);
		}
		return obj;
	}
	// 발표 날짜, 시각 구하기
	public String[] getBaseDateTime() {
		String[] dateTime = new String[2];	// 발표 날짜, 시각 저장
		Date date = new Date();	// 오늘 날짜
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm");
		String today = dFormat.format(date);
		String baseTime = tFormat.format(date);
		int hh = Integer.parseInt(baseTime.split(":")[0]);
		int mm = Integer.parseInt(baseTime.split(":")[1]);
		
		// 어제 날짜
		date = new Date(date.getTime()+(1000*60*60*24*-1));
		String yesterday = dFormat.format(date);
		
		System.out.println("today: "+today);
		System.out.println("baseTime: "+baseTime);
		// 시간대별 발표 날짜, 시각 처리
		if(hh < 2 || (hh == 2 && mm <= 10)) {	// 0 ~ 2시 10분 사이
			dateTime[0] = yesterday;
			dateTime[1] = "2300";
		} else if(hh < 5 || (hh == 5 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0200";
		} else if(hh < 8 || (hh == 8 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0500";
		} else if(hh < 11 || (hh == 11 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "0800";
		} else if(hh < 14 || (hh == 14 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1100";
		} else if(hh < 17 || (hh == 17 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1400";
		} else if(hh < 20 || (hh == 20 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "1700";
		} else if(hh < 23 || (hh == 23 && mm <= 10)) {
			dateTime[0] = today;
			dateTime[1] = "2000";
		} else {
			dateTime[0] = today;
			dateTime[1] = "2300";
		}
		
		return dateTime;
	}
	// 데이터 커스터마이징
	public JSONObject customizingData(JSONArray dataJarr) {
		SimpleDateFormat fDate = new SimpleDateFormat("yyyyMMdd");	// 예보 날짜
		SimpleDateFormat fTime = new SimpleDateFormat("HHmm");	// 예보 시각
		Date d;
		try {
			JSONArray resultJarr = new JSONArray();
			JSONObject resultObj = new JSONObject();
			JSONObject tmp;
			JSONObject category = getCategory(dataJarr);
			
			// fcstTime 당 11개의 데이터 나옴 [{"":"","":"",..."":""},{},...{}]
			for(int i = 0; i < dataJarr.size(); i++) {
				tmp = (JSONObject) dataJarr.get(i);
				if(i == 0 || resultJarr.size() > 0) {	// 시간대별 시작 날짜, 시간 저장
					// 예보 날짜, 시간 저장
					String date = tmp.get("fcstDate").toString();
					String time = tmp.get("fcstTime").toString();
					// 예보 날짜
					d = fDate.parse(date);
					fDate = new SimpleDateFormat("yyyy-MM-dd");
					date = fDate.format(d);
					resultObj.put("forecastDate", date);
					// 예보 시간
					d = fTime.parse(time);
					fTime = new SimpleDateFormat("HH:mm");
					time = fTime.format(d);
					resultObj.put("forecastTime", time);
				}
				// 카테고리에 따른 값 커스터마이징
				for(int j = 0; j < tmp.size(); j++) {
					// 카테고리 항목을 항목명으로 바꾸고 저장
					String ctg = tmp.get("category").toString();	// 카테고리 항목
					String fcstVal = tmp.get("fcstValue").toString();	// 예보 값
					resultObj.put(category.get(ctg).toString(), getFcstValue(ctg, fcstVal));
				}
			}
			// 최종 결과값 출력
			String result = resultObj.toJSONString();
			result = result.replace(',', '\n');
			System.out.println(result);
			
			return resultObj;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// 카테고리 항목과 항목명 매칭
	public JSONObject getCategory(JSONArray dataJarr) {
		try {
			JSONObject category = new JSONObject();	// {카테고리 항목:항목명}
			JSONObject tmp;	// 데이터 임시 저장
			
			for(int i = 0; i < dataJarr.size(); i++) {
				tmp = (JSONObject) dataJarr.get(i);
				String key = tmp.get("category").toString();
				if(category.size() < 1 || !category.containsKey(key)) {
					String val = "";
					
					if(key.equals("POP")) val = "Rainfall";
					else if(key.equals("PCP")) val = "Precipitation";
					else if(key.equals("PTY")) val = "Precipitation types";
					else if(key.equals("REH")) val = "Humidity";
					else if(key.equals("SNO")) val = "Snowfall";
					else if(key.equals("SKY")) val = "Sky condition";
					else if(key.equals("TMP")) val = "Temperature";
					else if(key.equals("UUU")) val = "Wind speed (East-West component)";
					else if(key.equals("VVV")) val = "Wind Speed (South-North Components)";
					else if(key.equals("VEC")) val = "wind direction";
					else if(key.equals("WSD")) val = "wind speed";
					
					category.put(key, val);
				}
			}
			return category;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// 카테고리에 따른 값 커스터마이징
	public String getFcstValue(String category, String value) {
		String result = value;
		float val = 0;
		if(result.matches("[+-]?\\d*(\\.\\d+)?"))// 숫자일 때
			val = Float.parseFloat(value);
		
		if(category.equals("POP")) result += "%";
		else if(category.equals("PTY")) {
			if(result.equals("0")) result = "no";
			else if(result.equals("1")) result = "rain";
			else if(result.equals("2")) result = "rain/snow";
			else if(result.equals("3")) result = "snow";
			else result = "shower";
		} else if(category.equals("REH")) result += "%";
		else if(category.equals("SKY")) {
			if(val >= 0.0f && val < 6.0f) result = "sunny";//맑음
			else if(val >= 6.0f && val < 9.0f) result = "cloudy(a lot of clouds)";//구름많음
			else if(val >= 9.0f && val < 11.0f) result = "cloudy(rain)";//흐림
		} else if(category.equals("TMP")) result += "℃";
		else if(category.equals("UUU")) {
			if(val > 0) result = "East " + Float.toString(Math.abs(val)) + "m/s";
			else result = "West " + Float.toString(Math.abs(val)) + "m/s";
		} else if(category.equals("VVV")) {
			if(val > 0) result = "North " + Float.toString(Math.abs(val)) + "m/s";
			else result = "South " + Float.toString(Math.abs(val)) + "m/s";
		} else if(category.equals("VEC")) {
			if(val >= 0.0f && val < 45.0f) result = "N-NE";
			else if(val >= 45.0f && val < 90.0f) result = "NE-E";
			else if(val >= 90.0f && val < 135.0f) result = "E-SE";
			else if(val >= 135.0f && val < 180.0f) result = "SE-S";
			else if(val >= 180.0f && val < 225.0f) result = "S-SW";
			else if(val >= 225.0f && val < 270.0f) result = "SW-W";
			else if(val >= 270.0f && val < 315.0f) result = "W-NW";
			else result = "NW-N";
		} else if(category.equals("WSD")) {
			if(val >= 0.0f && val < 4.0f) result += "m/s";
			else if(val >= 4.0f && val < 9.0f) result += "m/s (little strong)";
			else if(val >= 9.0f && val < 14.0f) result += "m/s (strong)";
			else result += "m/s (very strong)";
		} else {
			if(value.equals("강수없음")) result = "TYPE_RAIN_NO";
			else if(value.equals("적설없음")) result = "TYPE_SNOW_NO";
		}
		return result;
	}

}
