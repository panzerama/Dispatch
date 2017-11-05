package com.indexyear.jd.dispatch.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.Employee;
import com.indexyear.jd.dispatch.models.MCT;
import com.indexyear.jd.dispatch.models.Message;

import java.util.HashMap;
import java.util.Map;

import static com.indexyear.jd.dispatch.R.id.fab;

public class ChatActivity extends AppCompatActivity {

    FloatingActionButton sendMessage;
    Employee emp1;
    Employee emp2;
    private DatabaseReference db;
    private FirebaseListAdapter<Message> adapter;
    private static String selectedMCT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        selectedMCT = this.getIntent().getStringExtra("selectedMCT");
        Toast.makeText(getApplicationContext(), "List Item Value: "+this.getIntent().getStringExtra("selectedMCT"), Toast.LENGTH_LONG).show();

        db = FirebaseDatabase.getInstance().getReference();

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar_messenger);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        sendMessage = (FloatingActionButton)findViewById(fab);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                WriteNewMessage(getTeam().teamName, getUser().userID, input.getText().toString());

                // Clear the input
                input.setText("");
            }
        });
        DisplayChatMessages();
    }

    //Creates a message for the team, and adds it to a user's node
    private void WriteNewMessage(String mctID, String userName, String newMessage){
        String key = db.child("messages").push().getKey();
        Message message = new Message(newMessage, userName, getTeam());
        Map<String, Object> messageValues = message.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/team-messages/" + getTeam().teamName +"/"+ key, messageValues);
//        childUpdates.put("/user-messages/" + getUser().userID + "/" + key, messageValues);

        db.updateChildren(childUpdates);
//        db.getReference("messages").child(mctID).push().setValue(message);
    }

    private MCT getTeam(){
        return new MCT(selectedMCT);
    }

    private Employee getUser(){
        return new Employee("rick", "Rick", "Sanchez", "999-999-9999");
    }

    private void DisplayChatMessages(){
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<Message>(this, Message.class,
                R.layout.chat_messages, FirebaseDatabase.getInstance().getReference("team-messages/"+getTeam().teamName)) {
            @Override
            protected void populateView(View v, Message model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getText());
                messageUser.setText(model.getUserName());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("(HH:mm) dd-MM-yyyy",
                        model.getTimeSent()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}
