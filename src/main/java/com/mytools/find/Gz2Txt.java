/*************************************************
 * @File: Gz2Txt.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2018-12-21 16:12:36
 * @Description:
 *************************************************/
package com.mytools.find;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j2;

/**
 * @File Gz2Txt.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-12-21 16:12:36
 * @Description
 */
@Log4j2
public class Gz2Txt {

//	 private final static String INPUT_PATH = "D:\\temp";
//	 private final static String OUTPUT_PATH = "D:\\temp\\hit";
	private static String INPUT_PATH = "./";
//	private final static String INPUT_PATH = "./";
	private final static String OUTPUT_PATH = "./hit";
	private static String OUTPUT_FILE_NAME_FLAG = new Date() + "";
	private static String logFlag = "";

	public void run(String[] args) throws InterruptedException, IOException {
		String path = args[0];
		String grep = args[1];

		String fileName;

		File dataPath = new File(INPUT_PATH);
		File[] datas = filesFilter(dataPath, logFlag);
		log.debug("file: " + datas.length);
		int i = 1;
		for (File file : datas) {
			log.info(i + "_" + file.getName() + "=======");
			i++;

			String command = "zcat " + file.getName() + " | grep \"stat.vip.58.com/foot/get/visitor\" > all.log &";
			Runtime.getRuntime().exec(command).waitFor();
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
