package com.api.weather;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.api.weather.model.LegalDong;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;

public class Main {
	@SneakyThrows
	public static void main(String[] args)  {
//		byte[] b = new RestTemplate().getForObject("", byte[].class);
//		FileUtils.writeByteArrayToFile(new File("pathname"), b);// https://www.code.go.kr/stdcodesrch/codeAllDownloadL.do 법정도 다운로드 및 unzip
		//unzip
		ZipFile z = new ZipFile("/download/법정동코드 전체자료.zip");
		z.setCharset(Charset.forName("euc-kr"));
		z.extractAll("/tmp");  
		List<String> ls = IOUtils.readLines(new FileInputStream(new File("/tmp/법정동코드 전체자료.txt")), "euc-kr");
		List<LegalDong> lds = new ArrayList<>();
		for (String l : ls) {
			if(ls.indexOf(l) != 0) {
				String[] d = l.split("	");
				lds.add(new LegalDong(d[0], d[1], d[2]));
			}
		}
		String code = null;
		for (LegalDong ld : lds) {
			if(ld.getName().contains("서울특별시") && ld.getName().contains("은평구") && ld.getName().contains("진관동")) {
				if(ld.getUsage().equals("존재")) {
					code = ld.getCode();
					break;
				}
			}
		}
		System.out.println(code);
	}
}
