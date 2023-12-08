package com.api.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.api.weather.service.RegionService;
import com.api.weather.service.WeatherDataService;

import lombok.Data;
import lombok.SneakyThrows;

@Data
@Controller
public class HomeController {
	@Autowired RegionService r;
	@Autowired WeatherDataService w;
	
	@RequestMapping(value = "/")
	public String h() {
		return "home";
	}
	@RequestMapping(value="/{city}/{gu}/{dong}")
	@SneakyThrows
	public String w(@PathVariable String city, @PathVariable String gu, @PathVariable String dong, Model m) {
		String key = "iwEVyz24MuJlBvwmLqeWm63lq2lIjQRKvr6ExVoAt925m6B4ypQ2gEOieibt1pxcu9JJ6bYIvUodaZUNU+JIRg==";
		String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		m.addAttribute("weather",w.getWeatherData(key, url, r.lookUpRegion(city, gu, dong)));
		return "viewWeather";
	}
	
	@ExceptionHandler(Exception.class)
	public String e(Exception ex) {
		return "error";
	}
	
}

