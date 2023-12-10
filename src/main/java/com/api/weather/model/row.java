package com.api.weather.model;

import lombok.Data;

@Data
public class row { 
	private String MSRDATE;     // 1	MSRDATE	측정날짜 
	private String MSRADMCODE;     // 2	MSRADMCODE	측정소 행정코드
	private String MSRSTENAME;  // 3	MSRSTENAME	측정소명
	private String MAXINDEX;    // 4	MAXINDEX	통합대기환경지수
	private String GRADE;       // 5	GRADE	통합대기환경지수 등급
	private String POLLUTANT;   // 6	POLLUTANT	지수결정물질
	private String NITROGEN;    // 7	NITROGEN	이산화질소(단위:ppm)
	private String OZONE;       // 9	OZONE	오존(단위:ppm)
	private String CARBON;      // 11	CARBON	일산화탄소(단위:ppm)
	private String SULFUROUS;   // 13	SULFUROUS	아황산가스(단위:ppm)
	private String PM10;           // 15	PM10	미세먼지(단위:㎍/㎥)
	private String PM25;           // 19	PM25	초미세먼지(단위:㎍/㎥)
	public String getMSRDATE() {
		return "측정날짜 " + MSRDATE;
	}
	public String getMSRADMCODE() {
		return "측정소 행정코드 " + MSRADMCODE;
	}
	public String getMSRSTENAME() {
		return "측정소명 "+ MSRSTENAME;
	}
	public String getMAXINDEX() {
		return "통합대기환경지수 " + MAXINDEX;
	}
	public String getGRADE() {
		return "통합대기환경지수 등급 " + GRADE;
	}
	public String getPOLLUTANT() {
		return "지수결정물질 " + POLLUTANT;
	}
	public String getNITROGEN() {
		return "이산화질소(단위:ppm) " + NITROGEN;
	}
	public String getOZONE() {
		return "오존(단위:ppm) "+ OZONE;
	}
	public String getCARBON() {
		return "일산화탄소(단위:ppm) " +CARBON;
	}
	public String getSULFUROUS() {
		return "	아황산가스(단위:ppm) " + SULFUROUS;
	}
	public String getPM10() {
		return "	미세먼지(단위:㎍/㎥) "  + PM10;
	}
	public String getPM25() {
		return "	초미세먼지(단위:㎍/㎥) " + PM25;
	}
	
}
