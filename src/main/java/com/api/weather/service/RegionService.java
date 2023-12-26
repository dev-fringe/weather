package com.api.weather.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.weather.model.Grid;
import com.api.weather.model.LegalDong;

import lombok.SneakyThrows;

@Service
public class RegionService {
	// 입력한 지역의 행정 코드 찾는 메서드(카카오 api 사용)
	@SneakyThrows
	public Grid lookUpRegion(String city, String gu, String dong) {
		// 주소안에 띄어쓰기때문에 400에러가 나는것을 해결
//			byte[] b = new RestTemplate().getForObject("", byte[].class);
//			FileUtils.writeByteArrayToFile(new File("pathname"), b);// https://www.code.go.kr/stdcodesrch/codeAllDownloadL.do 법정도 다운로드 및 unzip
		// unzip
//		ZipFile z = new ZipFis
		String userHomeDir = System.getProperty("user.home");
		List<String> ls = IOUtils.readLines(new FileInputStream(new File(userHomeDir + "법정동코드 전체자료.txt")), "euc-kr");
		List<LegalDong> lds = new ArrayList<>();
		for (String l : ls) {
			if (ls.indexOf(l) != 0) {
				String[] d = l.split("	");
				lds.add(new LegalDong(d[0], d[1], d[2]));
			}
		}
		LegalDong l = null;
		for (LegalDong ld : lds) {
			if (ld.getName().contains(city) && ld.getName().contains(gu) && ld.getName().contains(dong)) {
				if (ld.getUsage().equals("존재")) {
					l = ld;
				}
			}
		}
		return getGridXY(l, dong);
	}

	// 행정구역코드로 기상청 격자좌표 구하는 메서드
	@SneakyThrows
	public Grid getGridXY(LegalDong ld, String dong) {
		System.out.println(ld);
		System.out.println(dong);
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
		restTemplate.getMessageConverters().add(0, converter);
		Grid[] gs = restTemplate.getForObject("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + ld.getCode().substring(0, 5) + ".json.txt", Grid[].class);
		System.out.println(gs.toString());
		for (Grid g : gs) {
			if (g.getValue().equals(dong)) {
				System.out.println("x값 : " + g.getX() + ", y값 :" + g.getY());
				return g;
			}
		}
		throw new RuntimeException("행정구역으로 기상청 격자자표를 찾을 수 없습니다");
	}
}
