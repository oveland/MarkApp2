package com.example.markapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.markapp.net.HttpAsyncTask;
import com.example.markapp.net.HttpAsyncTask.HttpAsyncInterface;

import com.example.markapp.models.Users;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener, HttpAsyncInterface {
	
	// Propiedades de Login
	static EditText nickname;
	static EditText password;
	TextView txt_new;
	Button btn_login;
	
	// Propiedades de Usuario:
	static Users users = new Users();
	
	// Propiedades de Tareas HTTP:	
	
	private ProgressDialog Dialog;
	
	String fisrtname,lastname;
	int success = 0;
	    
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        nickname = (EditText) findViewById(R.id.txt_nickname);
        password = (EditText) findViewById(R.id.txt_paswword);
        nickname.setText("");
		password.setText("");
		
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        
        txt_new = (TextView) findViewById(R.id.txt_register);
        txt_new.setOnClickListener(this); 
        
        
    }

    @Override
	public void onClick(View v) {
		// Para verificar datos de login:
		if(v == btn_login){
			if(nickname.getText().toString().equals("") || password.getText().toString().equals("")){
				Toast.makeText(MainActivity.this, "Campos incompletos", Toast.LENGTH_SHORT).show();
			}
			else{
		        //Se muestra un diálogo de espera mientras se ejecuta la tarea en segundo plano:
				Dialog = new ProgressDialog(this);
		        Dialog.setMessage("Verificando datos. Espere...");
		        Dialog.show();
				
				params.add(new BasicNameValuePair("nickname", nickname.getText().toString()));
		        params.add(new BasicNameValuePair("password", password.getText().toString()));
		        
		        HttpAsyncTask login_task = new HttpAsyncTask(this, params);
		        login_task.execute(Constant.URL_LOGIN);
				}				
			
			
		}
		
		// Para registrar un nuevo usuario:
		if(v == txt_new){
			Intent i = new Intent(this, UserRegister.class);
			startActivity(i);
		}
	}

@Override
public void setResponse(String rta) {
	// Se cierra el dialog de espera:
	Dialog.dismiss();
	/////////////////////////////////////////////////////////////////////
	Log.d("RTA str login: ", rta);
		try {
			JSONObject json_rta = new JSONObject(rta);
			
			success = json_rta.getInt(Constant.SUCCESS);  
			if(success == 1){
				JSONArray user = 	json_rta.getJSONArray(Constant.USER);
				JSONObject objson = user.getJSONObject(0);
				fisrtname = objson.getString(Constant.FIRSTNAME);
				lastname = objson.getString(Constant.LASTNAME);
				
				
				Toast.makeText(this, "Bienvenid@ "+fisrtname+" "+lastname, Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(), UserMenu.class);
				ArrayList<String> user_data = new ArrayList<String>();
				user_data.add(fisrtname);
				user_data.add(lastname);
				user_data.add(nickname.getText().toString());
				user_data.add(password.getText().toString());
				
				password.setText("");
				
				i.putExtra("User_data",user_data);
				startActivity(i);
			}
			else{
				Toast.makeText(this, json_rta.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();			
		}
		
	/////////////////////////////////////////////////////////////////////
	
}	

}