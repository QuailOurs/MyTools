/*************************************************
 * @File name: a.java  
 * @Author: QuailOurs(yifei.wu) 
 * @Version: 1.0 
 * @Date: 2019-02-20 12:02:14
 * @Description: 
 *************************************************/
package com.bj58.sec.test.demo;


/**
 * @File a.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2019-02-20 12:02:15
 * @Description 
 */
public class a {
public static void main(String[] args) {
	EsClient.getInstance().init("10.126.126.84", 9300, "sec_es");
	EsClient.getInstance().insert("test_test", "test", "{\r\n" + 
			"     \"Code\": \"414500\",\r\n" + 
			"     \"Name\": \"二七区\",\r\n" + 
			"     \"sort\": 1\r\n" + 
			"}");
}
}
