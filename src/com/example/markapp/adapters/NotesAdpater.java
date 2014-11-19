package com.example.markapp.adapters;

import java.util.List;

import com.example.markapp.models.Notes;
import com.example.markapp.Constant;
import com.example.markapp.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotesAdpater  extends BaseAdapter{

	Context context;
	List<Notes> data;
	
	public NotesAdpater(Context context, List<Notes> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {		
		return data.size();
	}

	@Override
	public Object getItem(int position) {		
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		TextView txt;
		
		if(convertView!=null){
			v=convertView;
		}
		else {
			v=View.inflate(context, R.layout.item_list, null);
		}
		
		Notes n = (Notes) getItem(position);
		
		txt=(TextView) v.findViewById(R.id.note_title);
		txt.setText(n.getNote_name());
		
		txt=(TextView) v.findViewById(R.id.note_content);
		if(n.getType_note() == Constant.TYPE_NOTE_TXT){
			txt.setText("Nota de texto");	
		}
		else{
			txt.setText("Nota de voz");
		}
		
		txt=(TextView) v.findViewById(R.id.note_date);
		txt.setText(n.getNote_date());
		
		return v;
	}

}
