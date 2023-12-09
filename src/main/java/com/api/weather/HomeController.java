package com.api.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.api.weather.model.Grid;
import com.api.weather.service.RegionService;
import com.api.weather.service.WeatherDataService;
import lombok.Data;

@Data
@Controller
public class HomeController {
	@Autowired RegionService r;
	@Autowired WeatherDataService w;
	
	@GetMapping(value = "/")
	public String h() {
		return "home";
	}
	@RequestMapping(value="/{city}/{gu}/{dong}")
	public @ResponseBody Object w(@PathVariable String city, @PathVariable String gu, @PathVariable String dong, Model m) {
		Grid g = r.lookUpRegion(city, gu, dong);
		return w.getWeatherData(g);
	}
	
}

