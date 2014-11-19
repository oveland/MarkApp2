package com.example.markapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.markapp.net.HttpAsyncTask;
import com.example.markapp.net.HttpAsyncTask.HttpAsyncInterface;
import com.example.markapp.R;

public class UserRegister extends ActionBarActivity implements OnClickListener, HttpAsyncInterface {
	
	// Propiedades de Registro:
	EditText fname, lname, nkname, pass;
	Button btn;
	
	// Propiedades de Tareas HTTP:
	private ProgressDialog Dialog;
	JSONObject json_rta;
	String result;
	int success = 0;
	
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		
		fname = (EditText) findViewById(R.id.txt_name);
		lname = (EditText) findViewById(R.id.txt_last_name);
		nkname = (EditText) findViewById(R.id.txt_nickname);
		pass = (EditText) findViewById(R.id.txt_paswword);
		
		btn = (Button) findViewById(R.id.btn_register);
		btn.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		if(fname.getText().toString().equals("") || lname.getText().toString().equals("") || nkname.getText().toString().equals("") || pass.getText().toString().equals("")){
			Toast.makeText(this, "Campos incompletos!", Toast.LENGTH_SHORT).show();
		}
		else{
			params.add(new BasicNameValuePair("firstname", fname.getText().toString()));
	        params.add(new BasicNameValuePair("lastname", lname.getText().toString()));
	        params.add(new BasicNameValuePair("nickname", nkname.getText().toString()));
	        params.add(new BasicNameValuePair("password", pass.getText().toString()));
	        
	        //Se muestra un dialogo de espera mientras se ejecuta la tarea en segundo plano:
	        Dialog = new ProgressDialog(this);
	        Dialog.setMessage("Registrando usuario...");
	        Dialog.show();
			
	        HttpAsyncTask login_task = new HttpAsyncTask(this, params);
	        login_task.execute(Constant.URL_REGISTER);
	        
		}
		
	}
	
@Override
public void setResponse(String rta) {
	String msg_toast;
	//Se cierra el diálogo de espera:
	Dialog.dismiss();
	
	try {
		json_rta = new JSONObject(rta);
		success = json_rta.getInt(Constant.SUCCESS);
		msg_toast = json_rta.getString(Constant.MESSAGE);
		Toast.makeText(UserRegister.this, msg_toast, Toast.LENGTH_LONG ).show();
		
		if(success == Constant.TASK_OK){   			
	        	MainActivity.nickname.setText("");
	        	MainActivity.password.setText("");
	        		
		Intent i = new Intent(getApplicationContext(), UserMenu.class);
		
		ArrayList<String> user_data = new ArrayList<String>();
		user_data.add(fname.getText().toString());
		user_data.add(lname.getText().toString());
		user_data.add(nkname.getText().toString());
		user_data.add(pass.getText().toString());
		i.putExtra("User_data",user_data);
		
        startActivity(i);
        finish();
        
		}
		
	} catch (JSONException e) {
		e.printStackTrace();
	}
}

	
}
