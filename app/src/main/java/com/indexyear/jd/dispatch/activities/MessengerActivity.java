package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.MCT;

import java.util.List;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();
    private static Context context;
    public static List<MCT> mctList;
    private DatabaseReference db;
    private ListView mListView;
    private FirebaseListAdapter<MCT> adapter;
    private ListView listOfTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        context = getApplicationContext();
        db = FirebaseDatabase.getInstance().getReference();

        //Create App Bar - Enable Back Notification
        // toolbar_messenger is defined in the activity messenger layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar_messenger);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

//        ManageUsers mu = new ManageUsers();
//        List<User> employees = new ArrayList<User>();
//        User employee1 = new User("rick", "Rick", "Sanchez", "999-999-9999");
//        User employee2 = new User("morty", "Morty", "", "999-999-9999");
//        employees.add(employee1);
//        employees.add(employee2);
//
//        mu.AddNewTeam("MCT1", "MCT 1", employees);
//        mu.AddNewTeam("MCT2", "MCT 2", employees);
//        mu.AddNewTeam("MCT3", "MCT 3", employees);

        createTeamList();

        listOfTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MCT team = (MCT)parent.getItemAtPosition(position);

                String teamID = team.getTeamName();

                Intent intent = new Intent(MessengerActivity.context, ChatActivity.class);
                intent.putExtra("selectedMCT", teamID.toString());
                startActivity(intent);
            }
        });
    }

    private void createTeamList() {

        listOfTeams = (ListView)findViewById(R.id.mct_list);

        adapter = new FirebaseListAdapter<MCT>(this, MCT.class,
                R.layout.message_list_item, FirebaseDatabase.getInstance().getReference().child("teams")) {
            @Override
            protected void populateView(View v, MCT model, int position) {
                TextView teamName = (TextView)v.findViewById(R.id.title_mct_name);
                teamName.setText(model.getTeamName());
            }
        };

        listOfTeams.setAdapter(adapter);
    }

}