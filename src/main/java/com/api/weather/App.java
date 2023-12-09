package com.api.weather;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.api.weather.model.Grid;
import com.api.weather.service.RegionService;
import com.api.weather.service.WeatherDataService;

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
	
	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(App.class);
	}
	
	@SneakyThrows
	public void afterPropertiesSet() {
		Grid g = r.lookUpRegion("서울특별시", "은평구", "진관동");
		System.out.println(w.getWeatherData(g));
		BodyPart body = new MimeBodyPart(); 
		body.setText(w.getWeatherData(g).toString());
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(body);
		m.setContent(multipart);
		Transport.send(m);
	}
}
