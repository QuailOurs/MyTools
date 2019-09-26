/*************************************************
 * @File: Find.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2018-11-21 15:11:51
 * @Description:
 *************************************************/
package com.mytools.find;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.log4j.Log4j2;

/**
 * @File Find.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-11-21 15:11:52
 * @Description
 */
@Log4j2
public class FindTianjingNgxLog {

	public static void main(String[] args) throws Exception {
		run("hit");
	}

//	 private final static String INPUT_PATH = "D:\\temp";
//	 private final static String OUTPUT_PATH = "D:\\temp\\hit";
	private static String INPUT_PATH = "./";
	private final static String OUTPUT_PATH = "./hit";
	private static String OUTPUT_FILE_NAME_FLAG = new Date().getTime() + "";
	private static String logFlag = "";


	public static void run(String... args) throws Exception {
		logFlag = args[0];
		INPUT_PATH = args[1];
		OUTPUT_FILE_NAME_FLAG = args[2];

		log.debug("logFlag: " + logFlag);
		if (StringUtils.isBlank(logFlag)) {
			log.error("logFlag is null");
		} else {
			filterUrl();
		}
		log.info("=========finish=========" + logFlag);
	}

	public static void filterUrl() throws Exception {
		File dataPath = new File(INPUT_PATH);
		File[] datas = filesFilter(dataPath, logFlag);
		log.debug("file: " + datas.length);
		int i = 1;
		for (File file : datas) {
			log.info(i + "_" + file.getName() + "=======");
			i++;
			readLine(file);
		}
	}

	public static String readLine(File file) throws Exception {

		Set<String> ipSet = new HashSet<>();
		int lineIndex = 1;
		int hitIndex = 1;
		try (
			LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		) {
			while (it.hasNext()) {
				String line = it.nextLine();
				String[] spStrings = StringUtils.split(line, "\t");
//				log.debug("line size: " + spStrings.length);
				if (spStrings.length >= 10) {

					JSONObject info = (JSONObject) JSONObject.parseObject("{" + spStrings[5] + "}").get("info");
					String domain = (String) info.get("domain");
					String http_referer = (String) info.get("http_referer");
					String request_uri = (String) info.get("request_uri");

					if (StringUtils.contains(domain, "stat.vip.58.com")
					 && StringUtils.contains(http_referer, "stat.vip.58.com/foot/get/visitor")
					 && StringUtils.contains(request_uri, "/foot/get/visitor")) {
						String ipstr = (String) info.get("userIp");
						if (hitIndex%100 == 0) {
							log.info("===hit " + hitIndex + " === " + line);
						}
						ipSet.add(ipstr);
					}

				}
				lineIndex++;
				hitIndex++;
			}
		}

		log.info("=== line count: " + lineIndex);
		log.info("=== hit count: " + hitIndex);

		write(ipSet, OUTPUT_PATH + "_" + logFlag + "_" + OUTPUT_FILE_NAME_FLAG + ".log");
		return "";
	}

	public static void write(String row, String dest) throws Exception {

		FileWriter fw = new FileWriter(dest, true);
		fw.write(row + "\r\n");
		fw.close();
	}

	public static void write(Set<String> set, String dest) throws Exception {

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String str = it.next();
			FileWriter fw = new FileWriter(dest, true);
			fw.write(str + "\r\n");
			fw.close();
		}
	}

	public static File[] filesFilter(File dir, final String fileNameEndsWith) {

		return dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				log.debug("file: " + name);

				if (StringUtils.startsWith(name, fileNameEndsWith)) {
					return true;
				} else {
					return false;
				}
			}
		});
	}
}
