package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.MCT;

import java.util.HashMap;
import java.util.Map;

public class DispatchTeamActivity extends AppCompatActivity {

    private ListView listOfTeams;
    private FirebaseListAdapter<MCT> adapter;
    private static Context context;
    private DatabaseReference db;
    private static final String TAG = "DispatchActivity";
    private String selectedTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_team);

        context = getApplicationContext();
        db = FirebaseDatabase.getInstance().getReference();

        //Create App Bar - Enable Back Notification
        // toolbar_messenger is defined in the activity messenger layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar_dispatch);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //TO-DO Use this address to calculate travel times
        //and update travel time node on MCT object to reflect
        //those values
        Intent intent = getIntent();
        String address = intent.getExtras().getString("crisisAddress");


        createTeamList();
    }

    //TO-DO Create a Firebase Query to sort teams by travel time and add those to list adapter
    private void createTeamList() {

        listOfTeams = (ListView)findViewById(R.id.mct_dispatch_list);

        //Query orderByTravelTime = db.child("teams").orderByChild(<Node with travel time>);
        adapter = new FirebaseListAdapter<MCT>(this, MCT.class,
                R.layout.message_list_item, FirebaseDatabase.getInstance().getReference().child("teams")) {
            @Override
            protected void populateView(View v, MCT model, int position) {
                TextView teamName = (TextView)v.findViewById(R.id.title_mct_name);
                teamName.setText(model.getTeamName());
            }
        };

        listOfTeams.setAdapter(adapter);

        listOfTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MCT team = (MCT)parent.getItemAtPosition(position);

                selectedTeam = team.getTeamID();
                createConfirmDispatchDialog();
                triggerNotification();
            }
        });
    }

    private void createConfirmDispatchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dispatch " + selectedTeam + "?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TO-DO Send message to team notifying them they've been dispatched
                //Create new dispatch activity node with pertinent details
                Toast.makeText(context, "Dispatched"+ selectedTeam,
                        Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerNotification(){
        Map<String, Object> crisisInfo = new HashMap<>();
        crisisInfo.put("crisisID", "000001");
        crisisInfo.put("crisisAddress", "1210 N 152nd St 98133");
        crisisInfo.put("team", "MCT1");

        db.child("crisis/").child("000001").updateChildren(crisisInfo);
    } // set this information programmatically

}
