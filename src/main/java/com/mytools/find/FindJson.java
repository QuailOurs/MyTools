package com.mytools.find;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * FindJson
 *
 * @author QuailOurs(yifei.wu)
 * @version 1.0
 * @since 2019/6/14 17:40
 */
@Slf4j
public class FindJson {

    public static void main(String[] args) throws Exception {
        run(new String[] {});
    }

    // 解析es数据t_portrait_user_58中的uid
    public static void run(String... args) throws Exception {

        String dataSourcePath = "D:\\doudou.txt";
        List<String> allLines = Files.readAllLines(Paths.get(dataSourcePath), StandardCharsets.UTF_8);
        String str = allLines.get(0);
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject hits = jsonObject.getJSONObject("hits");
        JSONArray hits2 = hits.getJSONArray("hits");


        for (Object hit : hits2) {
            JSONObject hitJson = (JSONObject) hit;
            JSONObject _source = hitJson.getJSONObject("_source");
            String uid = _source.getString("uid");
            log.info(uid);
        }
    }
}
