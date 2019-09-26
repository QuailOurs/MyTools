package com.mytools.dbImport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;

/**
 * RedisImport
 *
 * @author QuailOurs(yifei.wu)
 * @version 1.0
 * @since 2019/6/10 20:31
 */
@Slf4j
public class RedisImport {

    public static void run() throws Exception {
        RedisImport main = new RedisImport();
        main.save();
    }

    public void save() throws Exception {

        RedisClient.getInstance().init(64,
                20,
                10,
                true,
                false,
                true,
                10,
                120000,
                200,
                5000,
                "engine.rdb.58dns.org",
                9414,
                "2ab452944a765227");


//        File dataPath = new File("D:\\temp\\ip_w.txt");
        File dataPath = new File("/opt/ip_w.txt");
        int lineIndex = 0;
        int hitIndex = 0;
        try (
                LineIterator it = FileUtils.lineIterator(dataPath, "UTF-8")
        ) {
            while (it.hasNext()) {
                lineIndex++;

                String line = it.nextLine().trim();
//                RedisClient.getInstance().sadd("58_ip_white_list", line);
                RedisClient.getInstance().sadd("ajk_ip_white_list", line);
                RedisClient.getInstance().sadd("ganji_ip_white_list", line);
                log.info("===hit " + hitIndex + " === " + line);

                hitIndex++;
            }
        } catch (Exception e) {
            log.error("RedisImport", e);
        }
    }
}
