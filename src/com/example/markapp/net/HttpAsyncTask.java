package com.example.markapp.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class HttpAsyncTask extends AsyncTask<String, Integer, String> {
	
	static final int GET = 0;
	static final int POST = 1;
	
	String dataGET;
	List<NameValuePair> dataPOST;
	int httpMethod;
	
	ProgressDialog Dialog;
	
	public interface HttpAsyncInterface {
		public void setResponse(String rta);		
	}
	
	HttpAsyncInterface httpAsyncI;
	
	public HttpAsyncTask(HttpAsyncInterface httpAsyncI,String dataGET) {
		this.dataGET = dataGET;
		this.httpAsyncI = httpAsyncI;
		httpMethod = GET;
	}

	public HttpAsyncTask(HttpAsyncInterface httpAsyncI, List<NameValuePair> dataPOST) {
		this.dataPOST = dataPOST;
		this. httpAsyncI = httpAsyncI;
		httpMethod = POST;
	}

	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();                
    }
	
	@Override
	protected String doInBackground(String... params) {
		
		String url = params[0];
		
		HttpConnection httpCx = new HttpConnection(url);
		String rta = null;
		try {
			if(httpMethod == GET){
			rta = httpCx.requestByGet(dataGET);
			}
			else rta = httpCx.requestByPOST(dataPOST);
			
		} catch (ClientProtocolException e) { e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		}
		
		return rta;
	}

	//Se ejecuta en el UIthread y al retornar el doInBackgraind
	@Override
	protected void onPostExecute(String result) {
		httpAsyncI.setResponse(result);
	}
	
	//Este metodo se ejecuta en el hilo principal
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
}
