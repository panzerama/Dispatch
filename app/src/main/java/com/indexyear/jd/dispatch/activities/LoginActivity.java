package com.indexyear.jd.dispatch.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.user.IUserEventListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.models.User;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "LoginActivity: ";

    // Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Events and Logging
    private FirebaseAnalytics mAnalyticsInstance;

    //TODO 12/5/17 KB everything commented below can be removed
    // Can this be removed after refactoring with Teams and Users?
//    private DatabaseReference mDB;
    // User Management
//    private UserManager mUserManager;
//    private User mUser;

//    private IUserEventListener mUserEventListener;

    // UI Elements
    private EditText mEmailField;
    private EditText mPasswordField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: 10/31/17 JD update the ui function, modify layout to conform to material standards, welcome user already logged in

        // Login form
        mEmailField = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);

        //TODO 12/5/17 Remove prior to release
        //For Testing
        mEmailField.setText("kari@example.org");
        mPasswordField.setText("123456");

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

        // TODO: 12/5/17 KB implement logout procedure
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
                // TODO: 11/18/17 JD what's the purpose behind the listenere beyond logging?
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        mAnalyticsInstance = FirebaseAnalytics.getInstance(this);

//        mUserManager = new UserManager();
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

    private void signIn(final String email, String password) {
        //TODO KB can be removed
//        Log.d(TAG, "signInOrRegister: " + email);

        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            beginShift();
                        } else {
                            //TODO 12/5/17 KB can be removed
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Bundle params = new Bundle();
                            params.putString("time_stamp", "");
                            mAnalyticsInstance.logEvent("login_failure", params);

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Updates user node with appropriate shift-start data
     */
    private void beginShift(){
        Intent authenticationHandoff = new Intent(this, ShiftStartActivity.class);

        //Update user object in database as logged in
        FirebaseUser user = mAuth.getCurrentUser();

        String loginSuccess = "User " + user.getUid() + " has signed in.";
        Bundle params = new Bundle();
        params.putString("time_stamp", "");

        //TODO 12/5/17 KB can be removed
//                            Log.d(TAG, "signInWithEmail:success");
//
//                            Log.d(TAG, "instanceid: " + FirebaseInstanceId.getInstance().getToken());

        mAnalyticsInstance.logEvent(FirebaseAnalytics.Event.LOGIN, params);
        //TODO 12/5/17 KB can be removed
        //get or add employee in database
        //createEmployee(email);
        startActivity(authenticationHandoff);
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

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //TODO 12/5/17 KB can be removed
//    private void createEmployee(String email){
//        User newUser = User.createFromIDAndEmail(mAuth.getUid(), email);
//        newUser.setToken(FirebaseInstanceId.getInstance().getToken());
//        mUserManager.addOrUpdateNewEmployee(newUser);
//    }
}