package com.example.markapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.markapp.adapters.NotesAdpater;
import com.example.markapp.models.Notes;
import com.example.markapp.net.HttpAsyncTask;
import com.example.markapp.net.HttpAsyncTask.HttpAsyncInterface;
import com.example.markapp.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMenu extends ActionBarActivity implements OnClickListener, OnItemClickListener, HttpAsyncInterface{

	// Propiedades de usuario:
	TextView txt_user;
	String nickname_user, password_user;
	ArrayList<String> user_data;
	
	String notes[],content[],date[];
	ListView list;
	View dialogNewNote,dialogEditNote;
	NotesAdpater adapter;
	List<Notes> data;	
	
	Notes note;
	
	AlertDialog dialog_edit;
	
	EditText edit_title;
	
	Date Current_Date=new Date();
	
	JSONObject json_rta;
	JSONArray json_notes;
	
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	AdapterContextMenuInfo info_note;
	
	@Override
	protected void onRestart() {
		try {
			Toast.makeText(this, "Sincronizando notas...", Toast.LENGTH_LONG).show();
			super.onRestart();
			data.removeAll(data);
			adapter.notifyDataSetChanged();
			DownloadNotes(Constant.CLASIFY_DATE_ASC);
		} catch (Exception e) {
			
		}				
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_menu);
		
		txt_user = (TextView) findViewById(R.id.txt_user);
		Bundle bnd = this.getIntent().getExtras();
		user_data = bnd.getStringArrayList("User_data");		
		txt_user.setText(user_data.get(0)+" "+user_data.get(1));
		nickname_user = user_data.get(2);
		password_user = user_data.get(3);
				
		
		list=(ListView) findViewById(R.id.list);
		
		data= new ArrayList<Notes>();
        adapter= new NotesAdpater(this, data);
        
        
        list.setAdapter(adapter);        
        registerForContextMenu(list);   
        
        //-------- Propiedades del diálogo "Agragar nota": --------------------//
        dialogEditNote = View.inflate(this, R.layout.dialog_edit_note, null);
        
        edit_title= (EditText) dialogEditNote.findViewById(R.id.edit_title);        
        
        dialog_edit= new AlertDialog.Builder(this)
        .setView(dialogEditNote)
        .setPositiveButton("Aceptar", this)
        .setNegativeButton("Cancelar", this)
        .setCancelable(false)
        .create();
        
        dialog_edit.setCanceledOnTouchOutside(true);
        
        list.setOnItemClickListener(this);
        
        DownloadNotes(Constant.CLASIFY_DATE_ASC);
        
    }

	@SuppressLint("SimpleDateFormat") 
	public static String GetCurrentDate() {
        String current_d;
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        
        current_d = df.format(c.getTime());
        
		return current_d;		
	}

	
	public void DownloadNotes(int Clasify) {
		//notes= getResources().getStringArray(R.array.title_note);
        //content= getResources().getStringArray(R.array.content_notes);
        //date= getResources().getStringArray(R.array.date_notes);
        
        HttpAsyncTask get_all_notes_task = new HttpAsyncTask(this, Constant.NICKNAME+"="+nickname_user
        		+"&"+Constant.CLASIFY+"="+Clasify);
        get_all_notes_task.execute(Constant.URL_ALL_NOTES);        
	}
	
	@Override
	public void setResponse(String rta) {
		
		String msg_toast="";
		Notes n_set;
		
		Log.d("Rta STRING", rta.toString());
		
		try {
			json_rta = new JSONObject(rta);
			int success = json_rta.getInt(Constant.SUCCESS);
			msg_toast = json_rta.getString(Constant.MESSAGE);
			
			switch (success) {
			
			case Constant.GET_NOTES_OK:
				json_notes = json_rta.getJSONArray(Constant.LIST_NOTES);
				
				for(int i=0; i<json_notes.length();i++){
					
					JSONObject note_db = json_notes.getJSONObject(i);
					
					Notes n= new Notes(note_db.getInt(Constant.ID_NOTE), note_db.getString(Constant.TITLE_NOTE), 
							note_db.getString(Constant.CONTENT_NOTE), note_db.getInt(Constant.TYPE_NOTE), 
							note_db.getString(Constant.DATE_NOTE), note_db.getDouble(Constant.LATITUDE_NOTE),
							note_db.getDouble(Constant.LONGITUDE_NOTE));
					data.add(n);
				}
				adapter.notifyDataSetChanged();
				break;
			
				
			case Constant.EDIT_NOTE_OK:
				data.remove(info_note.position);
				n_set = new Notes(note.getId_note(), edit_title.getText().toString(), note.getNote_content(),
						note.getType_note(), GetCurrentDate(), note.getLatitude(), note.getLongitude());
				data.add(info_note.position, n_set);
				adapter.notifyDataSetChanged();
				
				edit_title.setText("");
				break;
				
			case Constant.DEL_NOTE_OK:
				data.remove(info_note.position);
				adapter.notifyDataSetChanged();
				break;
				
			default:
				break;
			}
			
			if(success != Constant.GET_NOTES_OK)Toast.makeText(this, msg_toast, Toast.LENGTH_LONG ).show();
			
		} catch (JSONException e) {
			Log.d("Error JSON conv String - Json Array", e.toString());
			e.printStackTrace();
		}
				
	}
	

	//Configuracion del menu de opciones
	
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.add_note, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(R.id.action_add == item.getItemId()){
			Intent i_new = new Intent(this, AdminNote.class);
			Bundle bnd = new Bundle();
			
			bnd.putString(Constant.NICKNAME, nickname_user);
						
			i_new.putExtras(bnd);
			startActivity(i_new);			
		}
		if(R.id.action_inf == item.getItemId()){
			Toast.makeText(this, "Información !", Toast.LENGTH_SHORT).show();
		}
		if(R.id.action_about == item.getItemId()){
			Toast.makeText(this, "Acerca de  !", Toast.LENGTH_SHORT).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
		
	//Configuracion del menu contextual
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.admin_note, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		//Obtenemos la información asociada al view que
		//disparo el menu contextual
				
		info_note = (AdapterContextMenuInfo) item.getMenuInfo();
		note = data.get(info_note.position);
		
		if(item.getItemId() == R.id.action_edit){
			edit_title.setText(note.getNote_name());
			dialog_edit.setTitle("Renomrbar "+note.getNote_name());
			dialog_edit.show();
		}
		
		if(item.getItemId() == R.id.action_delete){
			params.add(new BasicNameValuePair(Constant.NICKNAME, nickname_user));
	        params.add(new BasicNameValuePair(Constant.ID_NOTE, String.valueOf(note.getId_note())));
			
			HttpAsyncTask login_task = new HttpAsyncTask(this, params);
	        login_task.execute(Constant.URL_DEL_NOTE);
				
		}
		return super.onContextItemSelected(item);
	}
		
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
			if(which==DialogInterface.BUTTON_POSITIVE){
				if(edit_title.getText().toString().equals("")){
					Toast.makeText(this, "Campos incompletos", Toast.LENGTH_SHORT).show();
					dialog_edit.show();
				}
				else{
					if(note.getType_note() == Constant.TYPE_NOTE_TXT){
						params.add(new BasicNameValuePair(Constant.CONTENT_NOTE, note.getNote_content()));
						params.add(new BasicNameValuePair(Constant.TYPE_NOTE, String.valueOf(Constant.TYPE_NOTE_TXT)));				        	
					}
					else{
						params.add(new BasicNameValuePair(Constant.CONTENT_NOTE, note.getNote_content()));
						params.add(new BasicNameValuePair(Constant.TYPE_NOTE, String.valueOf(Constant.TYPE_NOTE_VOICE)));
				    }					
				params.add(new BasicNameValuePair(Constant.NICKNAME, nickname_user));
				params.add(new BasicNameValuePair(Constant.ID_NOTE, String.valueOf(note.getId_note())));
				params.add(new BasicNameValuePair(Constant.TITLE_NOTE, edit_title.getText().toString()));
		        
				params.add(new BasicNameValuePair(Constant.DATE_NOTE, GetCurrentDate()));
		        
				HttpAsyncTask edit_task = new HttpAsyncTask(this, params);
		        edit_task.execute(Constant.URL_EDIT_NOTE);
		        
				dialog_edit.dismiss();
				}
			}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Notes note_sel = data.get(position);
		Intent i_desc = new Intent(this, NoteDescription.class);
		
		ArrayList<String> note_data = new ArrayList<String>();
	
		note_data.add(String.valueOf(note_sel.getId_note()));
		note_data.add(note_sel.getNote_name());
		note_data.add(note_sel.getNote_content());
		note_data.add(String.valueOf(note_sel.getType_note()));
		note_data.add(note_sel.getNote_date());
		note_data.add(String.valueOf(note_sel.getLatitude()));
		note_data.add(String.valueOf(note_sel.getLongitude()));
		
		i_desc.putExtra(Constant.NOTE_DESCRIPTION, note_data);
		startActivity(i_desc);
	}
}
