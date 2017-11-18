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
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.CrisisManager;
import com.indexyear.jd.dispatch.data.CrisisParcel;
import com.indexyear.jd.dispatch.models.Crisis;
import com.indexyear.jd.dispatch.models.MCT;

public class DispatchTeamActivity extends AppCompatActivity {

    private static final String TAG = "DispatchActivity";

    private ListView listOfTeams;
    private FirebaseListAdapter<MCT> adapter;
    private Context context;
    private String selectedTeam;
    private Crisis inputCrisisObject;
    private CrisisManager mCrisisManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_team);

        context = getApplicationContext();
        mCrisisManager = new CrisisManager();

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
        CrisisParcel incomingCrisisParcel = intent.getParcelableExtra("crisis");
        inputCrisisObject = incomingCrisisParcel.getCrisis();

        createTeamList();
    }

    private void createTeamList() {

        listOfTeams = (ListView)findViewById(R.id.mct_dispatch_list);

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

                selectedTeam = team.getTeamName();
                createConfirmDispatchDialog();
                triggerNotification(inputCrisisObject, selectedTeam);
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

    private void triggerNotification(Crisis inputCrisis, String selectedTeam){
        inputCrisis.setTeamName(selectedTeam);
        mCrisisManager.addCrisisToDatabase(inputCrisis);
    }

}
