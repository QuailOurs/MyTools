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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @File Find.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-11-21 15:11:52
 * @Description
 */
public class Find {

	public static void main(String[] args) throws IOException {
		filterUrl();
	}

	public void filterUser() throws IOException {
		String dataSourcePath = "D:\\all";
		String dest = "D:\\user.txt";
		List<String> allLines = Files.readAllLines(Paths.get(dataSourcePath), StandardCharsets.UTF_8);

		Set<String> user = new HashSet<>();

		for(String str : allLines) {
			String[] spStrings = StringUtils.split(str, "	");
			if (spStrings.length>5) {
				String userTemp = spStrings[4];
				System.out.println(userTemp);
				if (StringUtils.contains(userTemp, "58dun_username:")) {
					String[] sp = StringUtils.split(userTemp, ':');
					userTemp = sp[1];
				}
				user.add(userTemp);
			}
		}

        Iterator<String> it = user.iterator();
        while(it.hasNext()) {
        	String userString = it.next();
			System.out.println(userString);

			if (StringUtils.contains(userString, "58dun_username:")) {
				String[] sp = StringUtils.split(userString, ':');
				userString = sp[1];
			}

			FileWriter fw = new FileWriter(dest,true);
            fw.write(userString + "\r\n");
            fw.close();
        }
	}

	public static void filterUrl() throws IOException {
		File dataPath = new File("D:\\hit\\");
		File[] datas = filesFilter(dataPath, "");
		// 规则关键字
		String[] ruleKey = new String[]{"EP:"};
		Set<String> urlSet = new HashSet<>();
		int i = 1;
		for (File file : datas) {

			List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			for (String data : allLines) {
				if (StringUtils.containsAny(data, ruleKey)) {
					data = StringUtils.split(data, '|')[1];
					// 记录命中规则日志
					JSONObject object = (JSONObject) JSONObject.parse(data);
					String url = object.getString("domain") + object.getString("request_uri");
					url = StringUtils.split(url, '?')[0];
					urlSet.add(url);


				}System.out.println(i + file.getName()); i++;
			}
		}

		write(urlSet);
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
