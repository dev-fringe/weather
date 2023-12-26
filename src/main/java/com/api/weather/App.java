package com.api.weather;

import java.io.StringWriter;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.api.weather.model.Grid;
import com.api.weather.model.row;
import com.api.weather.service.AirQualityService;
import com.api.weather.service.RegionService;
import com.api.weather.service.WeatherDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import config.MailConfig;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.Data;
import lombok.SneakyThrows;

@ImportResource("classpath:root-context.xml")
@Data
@Import(MailConfig.class) 
public class App implements InitializingBean{

	final RegionService r;
	final WeatherDataService w;
	final MimeMessage m;
	final AirQualityService a;
	
	public static void main(String[] args) {
		try (var context = new AnnotationConfigApplicationContext(App.class)){}
	}
	
	@SneakyThrows
	public void afterPropertiesSet() {
		row row = a.getrow("은평구");
		Grid g = r.lookUpRegion("서울특별시", "은평구", "진관동");
		Map<?,?> map = w.getWeatherData(g);
		BodyPart body = new MimeBodyPart(); 
		System.out.println(new ObjectMapper().writeValueAsString(map));
	    StringWriter stringWriter = new StringWriter();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("template.mustache");
		mustache.execute(stringWriter, map).flush();
	    StringWriter stringWriter2 = new StringWriter();
		MustacheFactory mf2 = new DefaultMustacheFactory();
		Mustache mustache2 = mf2.compile("template2.mustache");
		mustache2.execute(stringWriter2, row).flush();		
		System.out.println(stringWriter2.toString());
		body.setText(stringWriter.toString() + stringWriter2.toString().replaceAll("&#9;", ""));
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(body);
		m.setContent(multipart);
		m.setSubject("서울특별시"+ " 은평구"+ " 진관동");
		Transport.send(m);
	}
}
