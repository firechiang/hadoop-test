package com.firecode.hadooptest.flume.helloword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpTest {
	
	
	public static void main(String[] args) throws IOException {
	    String url = "http://192.168.83.137:8888/";
		String param = "[{\"headers\" : {\"timestamp\" : \"434324343\",\"host\" : \"random_host.example.com\" },\"body\" :\"random_body\"},{\"headers\" : {\"namenode\" : \"namenode.example.com\", \"datanode\" :\"random_datanode.example.com\"},\"body\" : \"really_random_body\"}]";
		                 
	    URL realUrl = new URL(url);
	    URLConnection conn = realUrl.openConnection();
	    // POST
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    OutputStream out = conn.getOutputStream();
	    out.write(param.getBytes());
	    out.flush();
	    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while (in.readLine() != null) {
	    	
	    }
	    out.close();
	    in.close();
	}

}
