package com.example.markapp.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpConnection {

	HttpClient client;
	String url;
	
	public HttpConnection(String url){
		client = new DefaultHttpClient();
		this.url=url;
	}
	
	public String requestByGet(String params) throws ClientProtocolException, IOException{
		HttpGet request= new HttpGet(url+"?"+params);
		HttpResponse response=client.execute(request);
		return processStringResponse(response);
	}
	
	public String requestByPOST(List<NameValuePair> params) throws ClientProtocolException, IOException{
		HttpPost request = new HttpPost(url);
		UrlEncodedFormEntity form= new UrlEncodedFormEntity(params);
				
		request.setEntity(form);
		HttpResponse response= client.execute(request);
		return processStringResponse(response);
	}
	
	public String processStringResponse(HttpResponse response) throws ParseException, IOException{
		String rta= EntityUtils.toString(response.getEntity(),"UTF-8");		
		return rta;
	}
}
