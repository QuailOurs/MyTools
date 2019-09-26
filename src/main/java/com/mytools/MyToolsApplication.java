package com.mytools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyToolsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MyToolsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		RedisImport.run();
//		FindJson.run();
//		FindK.run(args);
//		FindTianjingNgxLog.run(args);
	}
}
