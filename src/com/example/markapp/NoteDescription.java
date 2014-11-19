package com.example.markapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoteDescription extends FragmentActivity implements OnClickListener, OnCompletionListener{
	
	String desc_title, desc_content, desc_date;
	int desc_id_note, desc_type_note;
	
	double latitude_note, longitude_note;
	
	TextView title_desc, content_desc, date_desc;
	TextView btn_edit_desc;
	
	Button play_desc, pause_desc, stop_desc;
	ImageView img_edit_desc;
	RelativeLayout btns_desc;
	
	MediaPlayer player;
	
	private GoogleMap mapa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_description);
		
		Bundle bnd_desc = this.getIntent().getExtras();
		ArrayList<String> note_data;
		note_data = bnd_desc.getStringArrayList(Constant.NOTE_DESCRIPTION);		
		
		desc_id_note = Integer.parseInt(note_data.get(0));		
		desc_title = note_data.get(1);
		desc_content = note_data.get(2);
		desc_type_note = Integer.parseInt(note_data.get(3));
		desc_date = note_data.get(4);
		latitude_note = Double.parseDouble(note_data.get(5));
		longitude_note = Double.parseDouble(note_data.get(6));				
		
		LatLng MarkApp_origin = new LatLng(latitude_note, longitude_note);
		
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_desc)).getMap();
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(MarkApp_origin, 15));
        mapa.setMyLocationEnabled(true);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);
        mapa.addMarker(new MarkerOptions()
              .position(MarkApp_origin)
              .title("Ubicación origen MarkApp")
              .snippet("MarkApp")
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
              .anchor(0.4f, 0.4f));
        
		
		btns_desc = (RelativeLayout) findViewById(R.id.btns_desc);
		btn_edit_desc = (TextView) findViewById(R.id.btn_edit_desc);
		img_edit_desc = (ImageView) findViewById(R.id.img_edit);
		play_desc = (Button) findViewById(R.id.btn_play_desc);
		pause_desc = (Button) findViewById(R.id.btn_pause_desc);
		stop_desc = (Button) findViewById(R.id.btn_stop_desc);
		
		play_desc.setOnClickListener(this);
		pause_desc.setOnClickListener(this);
		stop_desc.setOnClickListener(this);
		btn_edit_desc.setOnClickListener(this);
		img_edit_desc.setOnClickListener(this);
		
		title_desc = (TextView) findViewById(R.id.note_title_desc);
		content_desc = (TextView) findViewById(R.id.note_content_desc);
		
		title_desc.setText(desc_title);
		title_desc.setKeyListener(null);
		if(desc_type_note == Constant.TYPE_NOTE_TXT){
			content_desc.setText(desc_content);
			content_desc.setKeyListener(null);
			btns_desc.setVisibility(View.INVISIBLE);			
		}else{
			content_desc.setText("");
			content_desc.setVisibility(View.INVISIBLE);			
			pause_desc.setVisibility(View.INVISIBLE);
			stop_desc.setEnabled(false);
		}
		
	}

	@Override
	public void onClick(View v) {
		if(v == play_desc){		
			File path = new File(Environment.getExternalStorageDirectory().getPath());			
			File audio_deco;
			 try {
				audio_deco = File.createTempFile("Markapp_temp", ".3gp", path);				
				byte[] AudioBytes = Base64.decode(desc_content, Base64.DEFAULT);
				
				FileOutputStream fileOuputStream = new FileOutputStream(audio_deco.getAbsolutePath());
				fileOuputStream.write(AudioBytes);
				fileOuputStream.close();
				
				player = new MediaPlayer();
				player.setOnCompletionListener(this);
				try {
					player.setDataSource(audio_deco.getAbsolutePath());
					player.prepare();
					
					pause_desc.setVisibility(View.VISIBLE);
					play_desc.setVisibility(View.INVISIBLE);
					stop_desc.setEnabled(true);					
				} catch (IOException e) {
				}				
			} catch (IOException e1) {
				e1.printStackTrace();
			}				
			player.start();
		}
		else if(v == pause_desc){
			player.pause();	
			pause_desc.setVisibility(View.INVISIBLE);
			play_desc.setVisibility(View.VISIBLE);
		}
		else if(v == stop_desc){
			player.stop();
			pause_desc.setVisibility(View.INVISIBLE);
			play_desc.setVisibility(View.VISIBLE);
		}
		
		else if(v == btn_edit_desc || v == img_edit_desc){
			
			
		}		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		pause_desc.setVisibility(View.INVISIBLE);
		play_desc.setVisibility(View.VISIBLE);
	}
}
