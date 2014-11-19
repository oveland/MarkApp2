package com.example.markapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.markapp.R;

public class SpinnerAdapter extends ArrayAdapter<Object>{

	private Context contexto;
	private String[] Categories;
	
		public SpinnerAdapter(Context context, int textViewResourceId, String[] items) {
		super(context, textViewResourceId, items);
		this.contexto = context;
		this.Categories = items;
		}	

		@Override
		public View getDropDownView(int position, View convertView,
		ViewGroup parent) {
		return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflador = ((Activity) this.contexto).getLayoutInflater();
		View fila = inflador.inflate(R.layout.spinner_row, parent, false);
		TextView texto = (TextView) fila.findViewById(R.id.category_sel);
		texto.setText(Categories[position]);

		ImageView icono = (ImageView) fila.findViewById(R.id.icon_category);

		if (Categories[position].equals("Personal")){
			icono.setImageResource(R.drawable.personal);
		}
		else if(Categories[position].equals("Educación")){
			icono.setImageResource(R.drawable.educacion);
		}
		else if(Categories[position].equals("Familia")){
			icono.setImageResource(R.drawable.familia);
		}
		else if(Categories[position].equals("Trabajo")){
			icono.setImageResource(R.drawable.trabajo);
		}
		
		
		
		return fila;
		}
		
}
