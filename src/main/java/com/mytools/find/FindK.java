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

import lombok.extern.log4j.Log4j2;

/**
 * @File Find.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-11-21 15:11:52
 * @Description
 */
@Log4j2
public class FindK {

	public static void main(String[] args) throws Exception {
		run(new String[] {"nohup", "D:\\temp\\", "hit", "5"});
	}

//	 private final static String INPUT_PATH = "D:\\temp";
//	 private final static String OUTPUT_PATH = "D:\\temp\\hit";
	private static String INPUT_PATH = "./";
//	private final static String INPUT_PATH = "./";
	private final static String OUTPUT_PATH = "./hit";
	private static String OUTPUT_FILE_NAME_FLAG = new Date() + "";
	private static String logFlag = "";

	private static String strategy = "";

	static String uri = "";
	static String referer = "";

	public static void run(String... args) throws Exception {
		logFlag = args[0];
		INPUT_PATH = args[1];
		OUTPUT_FILE_NAME_FLAG = args[2];
		strategy = args[3];

		switch(strategy) {
			case "1":
				uri = "/foot/get/visitor";
				referer = "stat.vip.58.com/foot/get/visitor";
				break;
			case "2":
				uri = "/resumepaychat/ajax/beforepaychat";
				referer = "jianli.58.com/resumepaychat/ajax/beforepaychat";
				break;
			case "3":
				uri = "/api/community/homepage/list";
				referer = "app.58.com/api/community/homepage/list";
				break;
			case "4":
				uri = "/api/community/detail/answerlist";
				referer = "app.58.com/api/community/detail/answerlist";
				break;
			case "5":
				uri = "textresult,";
				break;
			default:
				log.error("策略为空");
				return;
		}

//		logFlag = "tes";
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
		int lineIndex = 0;
		int hitIndex = 0;
		try (
			LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		) {
			while (it.hasNext()) {
				lineIndex++;

				String line = it.nextLine();
				String[] spStrings = StringUtils.split(line, "\t");
				log.debug("line size: " + spStrings.length);
				if (spStrings.length > 0) {

					if (!(StringUtils.contains(line, uri))) {
//						log.info("***nohit " + lineIndex + " === " + line);
						continue;
					}
					log.info(line);
					ipSet.add(spStrings[0]);
					hitIndex++;
				}
			}
		}

		log.info("=== line count: " + lineIndex);
		log.info("=== hit count: " + hitIndex);
		log.info("=== result count: " + ipSet.size());
		log.info("=== file: " + file.getAbsolutePath());
		log.info("=== url: " + uri);
		log.info("=== referer: " + referer);

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
