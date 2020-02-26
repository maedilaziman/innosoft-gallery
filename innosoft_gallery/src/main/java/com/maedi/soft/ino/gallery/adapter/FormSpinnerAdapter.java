package com.maedi.soft.ino.gallery.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maedi.soft.ino.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class FormSpinnerAdapter extends ArrayAdapter<String> {

	private Activity context;
    ArrayList<String> data = null;

    public FormSpinnerAdapter(Activity context, int resource, List<String> list)
    {
        super(context, resource, list);
        this.context = context;
        this.data = (ArrayList<String>) list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return super.getView(position, convertView, parent);   
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.form_spinner_item, parent, false);
        }

        String item = data.get(position);

        if(item != null)
        {
            TextView textData = (TextView) row.findViewById(R.id.form_spinner_item_textview);
            textData.setText(item);
        }

        return row;
    }	
}
