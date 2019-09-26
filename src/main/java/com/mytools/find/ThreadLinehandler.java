/*************************************************
 * @File: ThreadLinehandler.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2018-12-21 12:12:89
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
 * @File ThreadLinehandler.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-12-21 12:12:89
 * @Description
 */
@Log4j2
public class ThreadLinehandler implements Runnable {

	private final static String OUTPUT_PATH = "./hit";
	private static String OUTPUT_FILE_NAME_FLAG = new Date() + "";
	private static String logFlag = "";

	private File file = null;

	public ThreadLinehandler(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		if(this.file != null) {
			try {
				readLine(this.file);
			} catch (Exception e) {
				log.error("文件处理失败: " + this.file.getPath(), e);
			}
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
				log.debug("line size: " + spStrings.length);
				if (spStrings.length >= 16) {

					String uri = "/foot/get/visitor";
					String referer = "stat.vip.58.com/foot/get/visitor";

//					String uri = "/resumepaychat/ajax/beforepaychat";
//					String referer = "jianli.58.com/resumepaychat/ajax/beforepaychat";

//					String uri = "/api/community/detail/answerlist";
//					String referer = "app.58.com/api/community/detail/answerlist";

					if (!(StringUtils.contains(spStrings[6], uri)
						&& StringUtils.contains(spStrings[10], referer))) {
						log.info("***src " + lineIndex + " === " + line);
						continue;
					}
					log.info("===hit " + hitIndex + " === " + line);
					ipSet.add(spStrings[1]);
				}
				lineIndex++;
				hitIndex++;
			}
		}

		log.info("=== line count: " + lineIndex);
		log.info("=== hit count: " + hitIndex);

		write(ipSet, OUTPUT_PATH + "_" + file.getName() + "_" + logFlag + "_" + OUTPUT_FILE_NAME_FLAG + ".log");
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
