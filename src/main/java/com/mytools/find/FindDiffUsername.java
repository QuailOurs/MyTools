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
public class FindDiffUsername {

	// private final static String INPUT_PATH = "D:\\temp";
	// private final static String OUTPUT_PATH = "D:\\temp\\hit\\hit.log";
	private final static String INPUT_PATH = "./";
	private final static String OUTPUT_PATH = "./hit/hit";
	private static String logFlag = "";

	public void run(String... args) throws Exception {
		logFlag = args[0];
		log.info("logFlag: " + logFlag);
		if (StringUtils.isBlank(logFlag)) {
			log.info("logFlag is null");
		} else {
			filterUrl();
		}
		log.info("=========finish=========" + logFlag);
	}

	// "/data6/58dun/8_151/log"
	public static void filterUrl() throws Exception {
		File dataPath = new File(INPUT_PATH);
		File[] datas = filesFilter(dataPath, "");

		int i = 1;
		for (File file : datas) {
			log.info(i + file.getName() + "=======");
			i++;
			readLine(file);
		}
	}

	public static String readLine(File file) throws Exception {
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		int lineIndex = 1;
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				String[] spStrings = StringUtils.split(line, "\t");
				// log.info(spStrings.length);
				if (spStrings.length == 6) {
					// String dun_username = spStrings[3];
					// String sso_username = spStrings[4];
					System.out.print(lineIndex + " ");
					if (!(StringUtils.contains(spStrings[3], '@') || StringUtils.contains(spStrings[4], '@'))) {
						if (!spStrings[3].equals(spStrings[4])) {
							write(line, OUTPUT_PATH + logFlag);
							return line;
						}
					}
				}
				lineIndex++;
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		System.out.println();
		return "";
	}

	public static void write(String row, String dest) throws Exception {

		log.info(row);
		FileWriter fw = new FileWriter(dest, true);
		fw.write(row + "\r\n");
		fw.close();

	}

	public static void write(Set<String> set) throws Exception {

		String dest = OUTPUT_PATH;

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String str = it.next();
			System.out.println(str);
			FileWriter fw = new FileWriter(dest, true);
			fw.write(str + "\r\n");
			fw.close();
		}
	}

	public static File[] filesFilter(File dir, final String fileNameEndsWith) {

		return dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (StringUtils.startsWith(name, logFlag)) {
					return true;
				} else {
					return false;
				}
			}
		});
	}
}
