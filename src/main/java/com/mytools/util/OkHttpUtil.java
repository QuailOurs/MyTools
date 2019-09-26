/*************************************************
 * @File: OkHttpUtil.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2019-04-12 14:32:70
 * @Description:
 *************************************************/
package com.mytools.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.MoreObjects;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @File OkHttpUtil.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-09-29 17:09:70
 * @Description
 */
public class OkHttpUtil {

	private enum OkHttpSinglton {

		INSTANCE;

		private OkHttpClient client = null;

		private OkHttpSinglton() {
			client = new OkHttpClient.Builder()
					// 连接超时时间
					.connectTimeout(5, TimeUnit.MINUTES)
					// 读的时间
					.readTimeout(5, TimeUnit.MINUTES)
					// 写的时间
					.writeTimeout(5, TimeUnit.MINUTES)
					.build();
		}

		public OkHttpClient getInstance() {
			return client;
		}
	}

	public static String doPost(String url, String body, Map<String, String> headers) throws IOException {

		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

		Builder requestBuilder = new Request.Builder()
				.url(url)
				.post(RequestBody.create(mediaType, MoreObjects.firstNonNull(body, "{}")));

		if (null != headers) {
			requestBuilder.headers(setHeaders(headers));
		}

		final Request request = requestBuilder.build();
		return execute(request);
	}

	public static String doGet(String url) throws IOException {

		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();

		return execute(request);
	}

	/**
	 * 设置请求头
	 *
	 * @param headersMap
	 * @return Headers
	 */
	private static Headers setHeaders(Map<String, String> headersMap) {

		Headers headers = null;
		okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();

		if (headersMap.size() > 0) {
			Iterator<String> iterator = headersMap.keySet().iterator();
			String key = "";
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				headersbuilder.add(key, headersMap.get(key));
			}
		}

		headers = headersbuilder.build();
		return headers;
	}

	private static String execute(Request request) throws IOException {

		Call call = OkHttpSinglton.INSTANCE.getInstance().newCall(request);
		Response response = call.execute();
		return response.body().string();
	}
}
