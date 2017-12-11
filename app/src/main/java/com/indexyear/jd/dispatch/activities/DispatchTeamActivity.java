package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.crisis.CrisisManager;
import com.indexyear.jd.dispatch.models.Crisis;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;

public class DispatchTeamActivity extends AppCompatActivity {

    private static final String TAG = "DispatchActivity";

    private Context context;
    private Crisis inputCrisisObject;
    private CrisisManager mCrisisManager;

    //FOR FIREBASE ADAPTER
    private Team selectedTeam;
    protected static final Query teamQuery =
            FirebaseDatabase.getInstance().getReference().child("teams").orderByChild("travelTime");
    RecyclerView teamRecyclerView;
    public ArrayList<Team> teamItems;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LUKE - 12/9/17 - lock orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        Intent intent = getIntent();
        inputCrisisObject = intent.getParcelableExtra("crisis");

        //GET USER OBJECT TO PASS BACK TO MAIN
        mUser = intent.getParcelableExtra("user");

        //INIT FIREBASE TEAMS ADAPTER
        teamRecyclerView = (RecyclerView)findViewById(R.id.team_dispatch_list);
        teamRecyclerView.setHasFixedSize(true);
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamItems = new ArrayList<>();

    }

    @Override
    public void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void attachRecyclerViewAdapter() {

        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                teamRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        teamRecyclerView.setAdapter(adapter);
    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Team> options =
                new FirebaseRecyclerOptions.Builder<Team>()
                        .setQuery(teamQuery, Team.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Team, TeamHolder>(options) {
            @Override
            public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TeamHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.team_card_view, parent, false));
            }

            @Override
            protected void onBindViewHolder(TeamHolder holder, int position, Team model) {
                teamItems.add(model);
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    void createConfirmDispatchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String teamStatus = selectedTeam.getStatus();
        if(!teamStatus.equals("Active")){
                builder.setTitle(selectedTeam.getTeamName() + " is currently " + teamStatus + ". Continue anyway?");

        } else {
            builder.setTitle("Dispatch " + selectedTeam.getTeamName() + "?");
        }

        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                triggerNotification(inputCrisisObject, selectedTeam);
                Toast.makeText(context, "Dispatched "+ selectedTeam.getTeamName(),
                        Toast.LENGTH_LONG).show();

                returnToMainActivity();
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

    private void returnToMainActivity(){
        Intent returnToMain = new Intent(context, MainActivity.class);
        returnToMain.putExtra("user", mUser);
        returnToMain.putExtra("intent_purpose", "passing_user");
        startActivity(returnToMain);
    }

    void triggerNotification(Crisis inputCrisis, Team selectedTeam){
        if(!selectedTeam.equals(null)){
            inputCrisis.setTeamName(selectedTeam.getTeamName());
            inputCrisis.setStatus("open");
        }
        mCrisisManager.updateCrisisInDatabase(inputCrisis);
    }

    class TeamHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView teamName;
        TextView teamStatus;
        TextView travelTime;
        int availableColor;
        int unavailableColor;

        public TeamHolder(View itemView) {
            super(itemView);
            findViews(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    selectedTeam = teamItems.get(pos);
                    createConfirmDispatchDialog();
                }
            });
        }

        private void findViews(View itemView) {
            cv = (CardView)itemView.findViewById(R.id.team_card_view);
            teamName = (TextView) itemView.findViewById(R.id.team_name_text);
            teamStatus = (TextView) itemView.findViewById(R.id.team_status_text);
            travelTime = (TextView) itemView.findViewById(R.id.team_travel_time);
            availableColor = ContextCompat.getColor(itemView.getContext(), R.color.availableColor);
            unavailableColor = ContextCompat.getColor(itemView.getContext(), R.color.unavailableColor);
        }

        public void bind(Team team) {

            selectedTeam = team;
            teamName.setText(team.getTeamName());

            String status = team.getStatus();
            teamStatus.setText(status);
            teamStatus.setAllCaps(true);
            travelTime.setText("Estimated Arrival Time: " + team.getTravelTimeReadable());

            isAvailableColorFormat(status.equalsIgnoreCase("Active"));
        }

        //TODO KB 12/5/17 can be removed -- String will come from API response
//        private String convertFloatDateToStringDate(float time) {
//            if(time > 60){
//                int hours = (int) time;
//                int minutes = (int) (60 * (time - hours));
//                String newtime = "Estimated Travel Time: " + hours + "h " + minutes + "m";
//                return newtime;
//            } else {
//                int minutes = (int) time;
//                String newtime = "Estimated Travel Time: " + minutes + "m";
//                return newtime;
//            }
//        }

        private void isAvailableColorFormat(boolean isAvailable) {
            if (isAvailable) {
                travelTime.setTextColor(availableColor);
            } else {
                travelTime.setTextColor(unavailableColor);
            }
        }
    }

}
