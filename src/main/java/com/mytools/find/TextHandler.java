/*************************************************
 * @File: Find.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2018-11-21 15:11:51
 * @Description:
 *************************************************/
package com.mytools.find;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @File Find.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-11-21 15:11:52
 * @Description
 */
public class TextHandler {

	public static void main(String[] args) throws IOException {
//		JSONObject.parse("{\"theme\":\"cid\",\"openKey\":\"2051B8DC1F330E08A448EBEB320EBEE9\",\"data\":\"169588cc95054dac89dba9445acbb172\",\"type\":\"7\",\"srcTheme\":\"wuba\",\"srcData\":\"\",\"status\":\"1\"}");
		filterUser();
	}

	public static void filterUser() throws IOException {
		String dataSourcePath = "/opt/test/test.log";
		String dest = "/opt/test/test_data.log";
		List<String> allLines = Files.readAllLines(Paths.get(dataSourcePath), StandardCharsets.UTF_8);

		Set<String> user = new HashSet<>();

		for(String str : allLines) {
			String[] spStrings = StringUtils.split(str, "	");

			if (spStrings.length>0 && !spStrings[0].startsWith("testDataMap")) {

				JSONObject data = JSONObject.parseObject(spStrings[1].trim());

				// 58
				if ("iuNV4XUy_portrait".equals(spStrings[0])) {
					data.put("ip", "0.0.0.0");
					data.put("reqip", "0.0.0.0");
					data.put("userid", "0");
				}

				// ajk
				if ("VEZH8YVq_portrait".equals(spStrings[0])) {
					data.put("reqip", "0.0.0.0");
					data.put("userid", "0");
				}

				//ganji
				if ("9kU3jiXv_portrait".equals(spStrings[0])) {
					data.put("reqip", "0.0.0.0");
					data.put("userid", "0");
				}

				user.add(spStrings[0] + "	" + data.toJSONString());
			}

		}

        Iterator<String> it = user.iterator();
        while(it.hasNext()) {
        	String userString = it.next();
			System.out.println(userString);

			FileWriter fw = new FileWriter(dest,true);
            fw.write(userString + "\r\n");
            fw.close();
        }
	}

	public static void write(Set<String> set) throws IOException {

		String dest = "D:\\hit\\nginx_url.txt";

        Iterator<String> it = set.iterator();
        while(it.hasNext()) {
        	String str = it.next();
			System.out.println(str);
			FileWriter fw = new FileWriter(dest,true);
            fw.write(str + "\r\n");
            fw.close();
        }
	}

	public static File[] filesFilter(File dir, final String fileNameEndsWith) {

		return dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return true;
			}
    	});
	}
}
