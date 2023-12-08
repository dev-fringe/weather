package com.api.weather;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		
		return "home";
	}
	@RequestMapping(value="/{city}/{gu}/{dong}")
	public String searchWeather(@PathVariable String city, @PathVariable String gu, @PathVariable String dong, Model model) throws Exception {
		// 기상청 api key(decode)
		String serviceKey = "iwEVyz24MuJlBvwmLqeWm63lq2lIjQRKvr6ExVoAt925m6B4ypQ2gEOieibt1pxcu9JJ6bYIvUodaZUNU+JIRg==";
		// 기상청 단기예보 api url
		String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		// 입력한 지역의 경도, 위도 -> 행정구역코드 -> 격자 좌표 구할 클래스
		Region region = new Region();
		// 기상청 api를 통해 오늘의 날씨를 가져오는 클래스
		WeatherData wd = new WeatherData();
		// 결과를 model을 통해 view에 전달
		model.addAttribute("weather", wd.getWeatherData(serviceKey, url, region.lookUpRegion(city, gu, dong)));
		
		return "viewWeather";
	}
	// 에러 페이지 처리
	@ExceptionHandler(Exception.class)
	public String handle404(Exception ex) {
		
		return "error";
	}
	
}

