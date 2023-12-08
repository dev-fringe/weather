package com.api.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.api.weather.model.LegalDong;

public class Region {
	// 입력한 지역의 행정 코드 찾는 메서드(카카오 api 사용)
	public JSONObject lookUpRegion(String city, String gu, String dong) throws Exception {
		// 주소안에 띄어쓰기때문에 400에러가 나는것을 해결
//			byte[] b = new RestTemplate().getForObject("", byte[].class);
//			FileUtils.writeByteArrayToFile(new File("pathname"), b);// https://www.code.go.kr/stdcodesrch/codeAllDownloadL.do 법정도 다운로드 및 unzip
		// unzip
//		ZipFile z = new ZipFis
		List<String> ls = IOUtils.readLines(new FileInputStream(new File("/tmp/법정동코드 전체자료.txt")), "euc-kr");
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
//					code = ld.getCode();
					l = ld;
				}
			}
		}
		return getGridXY(l, dong);
	}

	// 행정구역코드로 기상청 격자좌표 구하는 메서드
	public JSONObject getGridXY(LegalDong ld, String dong) {
		String hCode = ld.getCode();
		
		System.out.println("행정코드: " + hCode);

		String code = hCode.substring(0, 5); // 격자 좌표가 행정코드 맨 앞부터 5자리까지만 이용하여 조회됨.

		try {
			JSONObject resultObj = new JSONObject();
			URL url = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + code + ".json.txt");
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String result = br.readLine().toString();
			br.close();

			JSONParser parser = new JSONParser();
			JSONArray jarr = (JSONArray) parser.parse(result);

			System.out.println(jarr.toJSONString());

			for (int i = 0; i < jarr.size(); i++) {
				resultObj = (JSONObject) jarr.get(i);
				if (resultObj.get("value").equals(dong)) {
					String x = (String) resultObj.get("x");
					String y = (String) resultObj.get("y");
					System.out.println("x값 : " + x + ", y값 :" + y);
					return resultObj;
				}
			}
			return resultObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
