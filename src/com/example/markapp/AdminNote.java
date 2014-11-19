package com.example.markapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.markapp.adapters.SpinnerAdapter;
import com.example.markapp.net.HttpAsyncTask;
import com.example.markapp.net.HttpAsyncTask.HttpAsyncInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdminNote extends FragmentActivity implements OnClickListener, HttpAsyncInterface, OnCheckedChangeListener, OnCompletionListener, OnMapClickListener {

	Spinner spn;
	TextView btn_ok,voice_status;
	ImageView img_ok;
	JSONObject json_rta;
	
	String nkname_usr,temp_status_voice;
	double latitude_note, longitude_note;
	EditText new_title, new_cont;
	
	Button play, rec, erase,rec_stop;
	RelativeLayout content_btns_rec;	
	RadioButton rbtn_txt, rbtn_voice;	
	
	
	MediaRecorder recorder;
	MediaPlayer player;
	File audio_file;
	File path;
	String  audiostr="empty!";
	byte[] AudioBytes;
	
	
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	private GoogleMap mapa;
	
	private String[] Categories = {"Sin Categoría", "Personal", "Familia", "Educación", "Trabajo"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_note);
		
		 	mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	        mapa.setMyLocationEnabled(true);
	        //mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()), 15));
	        mapa.getUiSettings().setZoomControlsEnabled(true);
	        mapa.getUiSettings().setCompassEnabled(true);
	        //mapa.addMarker(new MarkerOptions()
	          //    .position(new LatLng( mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()))
	            //  .title("Ubicación origen MarkApp")
	             // .snippet("MarkApp")
	             // .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
	             // .anchor(0.4f, 0.4f));
	        
	        //latitude_note = mapa.getMyLocation().getLatitude();
	        //longitude_note = mapa.getMyLocation().getLongitude();
	        
	        //mapa.setOnMapClickListener(this);
	        if (mapa.getMyLocation() != null){
		         mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
		            new LatLng( mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()), 15));
		      Toast.makeText(this, mapa.getMyLocation().getProvider().toString(), Toast.LENGTH_SHORT).show();
		      }
		
		Bundle bnd = this.getIntent().getExtras();
		try {
			nkname_usr = bnd.getString(Constant.NICKNAME);
		} catch (Exception e) {
			Log.d("Error en ONCREATE", e.toString());
			finish();
		}
		
		new_title= (EditText) findViewById(R.id.note_title);        
        new_cont= (EditText) findViewById(R.id.note_content);        
        
        rbtn_txt = (RadioButton) findViewById(R.id.radio_type_text);
        rbtn_voice = (RadioButton) findViewById(R.id.radio_type_voice);
        
        rbtn_txt.setOnCheckedChangeListener(this);
        rbtn_voice.setOnCheckedChangeListener(this);
        
        play = (Button) findViewById(R.id.btn_play);
        rec = (Button) findViewById(R.id.btn_rec);
        rec_stop = (Button) findViewById(R.id.btn_stop_rec);
        erase = (Button) findViewById(R.id.btn_erase);
        content_btns_rec = (RelativeLayout) findViewById(R.id.btns_rec);
        
        play.setOnClickListener(this);
        rec.setOnClickListener(this);
        rec_stop.setOnClickListener(this);
        erase.setOnClickListener(this);
        
        
        
        Btns_rec_Visibility(false);
        
		spn=(Spinner) findViewById(R.id.spinner_op);
		
		spn.setAdapter(new SpinnerAdapter(this, R.layout.spinner_row, this.Categories));
		
		voice_status = (TextView) findViewById(R.id.voice_status);
		voice_status.setText(Constant.VOICE_EMPTY);
		btn_ok = (TextView) findViewById(R.id.btn_OK);
		img_ok = (ImageView) findViewById(R.id.img_ok);
		
		btn_ok.setOnClickListener(this);
		img_ok.setOnClickListener(this);
		
		temp_status_voice = Constant.VOICE_EMPTY;
	}

	@Override
	public void onClick(View v) {
		
		if(v == btn_ok || v == img_ok){
			
				if(rbtn_txt.isChecked()){
					if(new_title.getText().toString().equals("") || new_cont.getText().toString().equals("")){
						Toast.makeText(this, "Campos incompletos", Toast.LENGTH_SHORT).show();
					}
					else{
						params.add(new BasicNameValuePair(Constant.NICKNAME, nkname_usr));
						params.add(new BasicNameValuePair(Constant.TITLE_NOTE, new_title.getText().toString()));
						params.add(new BasicNameValuePair(Constant.CONTENT_NOTE, new_cont.getText().toString()));
				        params.add(new BasicNameValuePair(Constant.TYPE_NOTE, String.valueOf(Constant.TYPE_NOTE_TXT)));
						params.add(new BasicNameValuePair(Constant.DATE_NOTE, UserMenu.GetCurrentDate()));
						params.add(new BasicNameValuePair(Constant.LATITUDE_NOTE, String.valueOf(latitude_note)));
						params.add(new BasicNameValuePair(Constant.LONGITUDE_NOTE, String.valueOf(longitude_note)));
						
						HttpAsyncTask login_task = new HttpAsyncTask(this, params);
				        login_task.execute(Constant.URL_SET_NOTE);		
					}
				}
		        else if(rbtn_voice.isChecked()){
		        	if(voice_status.getText().toString().equals(Constant.VOICE_EMPTY)){	
		        		Toast.makeText(this, Constant.VOICE_EMPTY, Toast.LENGTH_SHORT).show();
		        	}
			        else{
						if(new_title.getText().toString().equals("")){
							Toast.makeText(this, "Ingrese un título", Toast.LENGTH_SHORT).show();
			        		}
			        		else{
								params.add(new BasicNameValuePair(Constant.NICKNAME, nkname_usr));
								params.add(new BasicNameValuePair(Constant.TITLE_NOTE, new_title.getText().toString()));
								params.add(new BasicNameValuePair(Constant.CONTENT_NOTE, audiostr));
						        params.add(new BasicNameValuePair(Constant.TYPE_NOTE, String.valueOf(Constant.TYPE_NOTE_VOICE)));
						        params.add(new BasicNameValuePair(Constant.DATE_NOTE, UserMenu.GetCurrentDate()));
						        params.add(new BasicNameValuePair(Constant.LATITUDE_NOTE, String.valueOf(latitude_note)));
								params.add(new BasicNameValuePair(Constant.LONGITUDE_NOTE, String.valueOf(longitude_note)));
								
								HttpAsyncTask login_task = new HttpAsyncTask(this, params);
						        login_task.execute(Constant.URL_SET_NOTE);				        		
							}
					}
		        }
		}
		
		else if(v==rec){
			voice_status.setText(Constant.VOICE_RECORDING);
			
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			path = new File(Environment.getExternalStorageDirectory()
					.getPath());
			try {
				audio_file = File.createTempFile("MarkApp_Voice", ".3gp", path);
			} catch (IOException e) {
			}
			recorder.setOutputFile(audio_file.getAbsolutePath());
			try {
				recorder.prepare();
				rec.setEnabled(false);
				rec.setVisibility(View.INVISIBLE);
				rec_stop.setEnabled(true);
				rec_stop.setVisibility(View.VISIBLE);
				
				play.setEnabled(false);
				erase.setEnabled(false);
			} catch (IOException e) {
				
			}
			recorder.start();
			
			play.setEnabled(false);
			erase.setEnabled(false);
			
		}
		else if(v==rec_stop){
			
			voice_status.setText(Constant.VOICE_READY);
			
			recorder.stop();
			recorder.release();
			
			FileInputStream fileInputStream = null;
			File file_audio = new File(audio_file.getAbsolutePath());
			AudioBytes = new byte[(int) file_audio.length()];
			
			try {
				// Con este código se obtienen los bytes del archivo.
				fileInputStream = new FileInputStream(file_audio);
				fileInputStream.read(AudioBytes);
				fileInputStream.close();				
			} catch (Exception e) {
				voice_status.setText(Constant.VOICE_EMPTY);
			}
			//audiostr es el String que representa el archivo de voz
			audiostr = Base64.encodeToString(AudioBytes, Base64.DEFAULT);
			 Log.d("DECO BASE 64 -->","Audio array-> "+audiostr);
				   
			rec.setEnabled(true);
			rec.setVisibility(View.VISIBLE);
			rec_stop.setEnabled(false);
			rec_stop.setVisibility(View.INVISIBLE);
			
			play.setEnabled(true);
			erase.setEnabled(true);
			
		}
		else if(v==play){
			
			voice_status.setText(Constant.VOICE_PLAYING);
			erase.setEnabled(true);
			rec.setEnabled(false);
						
			player = new MediaPlayer();
			player.setOnCompletionListener(this);
			try {
				player.setDataSource(audio_file.getAbsolutePath());
				player.prepare();
			} catch (IOException e) {
			}
			player.start();
			
			rec.setEnabled(false);
		}
		else if(v==erase){
			if(voice_status.getText().toString().equals(Constant.VOICE_EMPTY)){
			
			}else{			
				if(player.isPlaying()){
					player.stop();
					rec.setEnabled(true);
					play.setEnabled(false);
					erase.setEnabled(false);
				}
				voice_status.setText(Constant.VOICE_EMPTY);
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		rec.setEnabled(true);
		voice_status.setText(Constant.VOICE_READY);
	}
	
	@Override
	public void setResponse(String rta) {
		
		try {
			json_rta = new JSONObject(rta);
			int success = json_rta.getInt(Constant.SUCCESS);
			
			switch (success) {			
			case Constant.SET_NOTE_OK:
				finish();				
				break;
			
			default:
				Toast.makeText(this, "No se puede guardar el archivo!", Toast.LENGTH_SHORT).show();
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(this, "Error en la BD!", Toast.LENGTH_LONG ).show();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(rbtn_txt.isChecked()){
				Btns_rec_Visibility(false);				
				}
			else {
				Btns_rec_Visibility(true);				
			}
			
	}
	
	public void Btns_rec_Visibility(Boolean visibility){
		AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        
		if(visibility){
			//desde la esquina inferior derecha a la superior izquierda
            animation = new TranslateAnimation(
            		Animation.RELATIVE_TO_SELF, 1.0f, 
            		Animation.RELATIVE_TO_SELF, 0.0f, 
            		Animation.RELATIVE_TO_SELF, 1.0f, 
            		Animation.RELATIVE_TO_SELF, 0.0f);
            //voice_status.setVisibility(View.VISIBLE);
            content_btns_rec.setVisibility(View.VISIBLE);
            new_cont.setVisibility(View.INVISIBLE);
		}else {
			//desde la esquina superior izquierda a la esquina inferior derecha
            animation = new TranslateAnimation(
            		Animation.RELATIVE_TO_SELF, 0.0f, 
            		Animation.RELATIVE_TO_SELF, 1.0f, 
            		Animation.RELATIVE_TO_SELF, 0.0f, 
            		Animation.RELATIVE_TO_SELF, 1.0f);
            //voice_status.setVisibility(View.INVISIBLE);
            content_btns_rec.setVisibility(View.INVISIBLE);
            new_cont.setVisibility(View.VISIBLE);
        }
		
		animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);
 
        content_btns_rec.setLayoutAnimation(controller);
        content_btns_rec.startAnimation(animation);		
	}

	@Override
	public void onMapClick(LatLng arg0) {		
		
	}

	
	
}


