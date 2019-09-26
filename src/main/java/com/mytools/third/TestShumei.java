/*************************************************
 * @File: TestShumei.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2019-04-12 13:04:95
 * @Description:
 *************************************************/
package com.mytools.third;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.mytools.util.OkHttpUtil;

import lombok.extern.log4j.Log4j2;

/**
 * @File TestShumei.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2019-04-12 13:04:95
 * @Description
 */
@Log4j2
public class TestShumei {
	public static void main(String[] args) throws Exception {
		run(new String[] {"shu", "D:\\temp\\", "hit5"});
	}

//	 private final static String INPUT_PATH = "D:\\temp";
	 private final static String OUTPUT_PATH = "D:\\temp\\hit";
	private static String INPUT_PATH = "./";
//	private final static String INPUT_PATH = "./";
//	private final static String OUTPUT_PATH = "./hit";
	private static String OUTPUT_FILE_NAME_FLAG = new Date() + "";
	private static String logFlag = "";

	public static void run(String... args) throws Exception {
		logFlag = args[0];
		INPUT_PATH = args[1];
		OUTPUT_FILE_NAME_FLAG = args[2];

		File dataPath = new File(INPUT_PATH);
		File[] datas = filesFilter(dataPath, logFlag);
		log.debug("file: " + datas.length);
		int i = 1;
		for (File file : datas) {
			log.info(i + "_" + file.getName() + "=======");
			i++;
			readLine(file);
		}

		log.info("=========finish=========" + logFlag);
	}

	private static ExecutorService es = Executors.newFixedThreadPool(50,
            (Runnable r) -> {
                Thread t = new Thread(r, "Log-Executor-");
//                t.setPriority(10);
                return t;
            });

	final static CountDownLatch cd = new CountDownLatch(3688);
	static List<String> list = Collections.synchronizedList(new ArrayList<String>());

	public static String readLine(File file) throws Exception {

		int lineIndex = 0;
		int hitIndex = 0;
		try (
			LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		) {
			while (it.hasNext()) {
				lineIndex++;
				String line = it.nextLine();
				String[] spStrings = StringUtils.split(line, "\t");
				log.debug(lineIndex + " line size: " + spStrings.length);

				es.submit(() -> {
					String result = "=====";
					while (true) {

    					try {
    						result = shumei(spStrings[0], spStrings[1]);
    						list.add(line + "\t" + result);
    						synchronized (cd) {
    							cd.countDown();
    							log.info("剩余={}", cd.getCount());
							}

    						break;
    					} catch (Exception e) {
//    						log.error("查询失败", e);
							continue;
    					}
					}
		        });
				hitIndex++;
			}
		}

		cd.await();

		log.info("=== line count: " + lineIndex);
		log.info("=== hit count: " + hitIndex);
		log.info("=== result count: " + list.size());
		log.info("=== file: " + file.getAbsolutePath());

		write(list, OUTPUT_PATH + "_" + logFlag + "_" + OUTPUT_FILE_NAME_FLAG + ".log");
		return "";
	}

	public static void write(List<String> set, String dest) throws Exception {

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

	public static String shumei(String userid, String url) throws IOException {

        Map<String, Object> conditionMap = new HashMap<>();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("img", url);
        dataMap.put("tokenId", userid);

    	conditionMap.put("accessKey", "exMVSsmVS620uaPRkfHk");
    	conditionMap.put("type", "DEFAULT");
    	conditionMap.put("data", dataMap);

    	String condition = JSONObject.toJSONString(conditionMap);

    	String resultStr = OkHttpUtil.doPost("http://img-api.fengkongcloud.com/v2/saas/anti_fraud/img", condition, null);

    	JSONObject result = JSONObject.parseObject(resultStr);
    	String score = result.getString("score");
		String riskLevel = result.getString("riskLevel");
		JSONObject detail = result.getJSONObject("detail");
		String riskType = detail.getString("riskType");
		String description = detail.getString("description");

		return StringUtils.join(new String[] {score, riskLevel, riskType, description}, '\t');
	}
}
