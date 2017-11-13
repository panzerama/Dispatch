package com.indexyear.jd.dispatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.Employee;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity: ";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAnalytics mAnalyticsInstance;

    private DatabaseReference mDB;

    private EditText mEmailField;
    private EditText mPasswordField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: 10/31/17 JD update the ui function, modify layout to conform to material standards, welcome user already logged in

        // Set up the login form.
        mEmailField = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordField = (EditText) findViewById(R.id.password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_sign_out_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // This should change the display to 'logout' or 'change role'
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // Default view.
                    // Pull the buttons, click listeners, and other setup in here.
                }
                // ...
            }
        };

        mAnalyticsInstance = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_out_button) {
            signOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user){
        //todo jd progressdialog?
        if (user != null) {
            //set something in the ui to reflect signed in?
        } else {
            //set something in the ui to reflect signed out?
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signInOrRegister: " + email);
        if (!validateForm()) {
            return;
        }

        final Intent authenticationHandoff = new Intent(this, ShiftStartActivity.class);

        // TODO: 11/11/17 JD implement the showprogress dialog, encapsulate the actual login process

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //todo jd Sign in success, update UI with the signed-in user's information
                            String loginSuccess = "User " + user.getUid() + " has signed in.";
                            Bundle params = new Bundle();
                            params.putString("time_stamp", "");

                            Log.d(TAG, "signInWithEmail:success");
                            Log.d(TAG, "instanceid: " + FirebaseInstanceId.getInstance().getToken());
                            mAnalyticsInstance.logEvent(FirebaseAnalytics.Event.LOGIN, params);
                            //get or add employee in database
                            createEmployee();
                            updateUI(user);
                            startActivity(authenticationHandoff);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Bundle params = new Bundle();
                            params.putString("time_stamp", "");
                            mAnalyticsInstance.logEvent("login_failure", params);

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        /* [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                         [END_EXCLUDE] */
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        mAuth.signOut();

        // TODO: 10/31/17 JD what is the state of mAuth now, and do we need to check it? 
        Bundle logParams = new Bundle();
        logParams.putString("user_logout", "User " + userID + " has logged out.");
        logParams.putString("time_stamp", " ");

        mAnalyticsInstance.logEvent("user_logout", logParams);
        
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    //todo jd is this needed still?
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createEmployee(){
        //get database reference
        mDB = FirebaseDatabase.getInstance().getReference("employees/");

        mDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){

                    Employee newEmployee = new Employee(mAuth.getCurrentUser().getUid(), "OtherExample", "User", "123-456-7890");
                    Map<String, Object> employeeValues = newEmployee.toMap();

                    Map<String, Object> databaseValue = new HashMap<>();
                    databaseValue.put(mAuth.getCurrentUser().getUid(), employeeValues);

                    mDB.updateChildren(databaseValue);
                } else {
                    Log.d(TAG, " employee found in database, do something here?");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}