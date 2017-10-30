package com.indexyear.jd.dispatch.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.MCT;

import java.util.List;

/**
 * Created by karibullard on 10/29/17.
 */

public class MessageListAdapter extends ArrayAdapter<MCT>{

    public MessageListAdapter (Context context, List<MCT> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Create View
        MCT team = getItem(position);
        if (convertView == null) { // Check if an existing view is being reused, otherwise inflate the view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_item, parent, false);
        }

        TextView mctItem = (TextView) convertView.findViewById(R.id.title_mct_name);
        mctItem.setText(team.teamName);

        //Attach Event Listeners
        mctItem.setTag(position);
        mctItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                int position = (Integer) view.getTag();
                MCT selectedTeam = getItem(position);
            }
        });


        return convertView;
    }
}
