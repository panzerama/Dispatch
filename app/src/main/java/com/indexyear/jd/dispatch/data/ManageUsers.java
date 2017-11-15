package com.indexyear.jd.dispatch.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.activities.MainActivity;
import com.indexyear.jd.dispatch.models.Employee;
import com.indexyear.jd.dispatch.models.MCT;

import java.util.List;

public class ManageUsers {

    private static final String TAG = "ManageUsers";
    private DatabaseReference mDatabase;
    private Employee employee;

    public ManageUsers(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void AddNewEmployee(String userID, String firstName, String lastName, String phone){
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPhone(phone);

        mDatabase.child("employees").child(userID).setValue(employee);
    }

    public void AddNewEmployee(Employee employee){

        mDatabase.child("employees").child(employee.userID).setValue(employee);
    }

    public void AddNewTeam(String teamName, String teamDisplayName, List<Employee> teamMembers){
        MCT team = new MCT();
        team.teamName = teamDisplayName;
        team.teamID = teamName;

        mDatabase.child("teams").child(teamName).setValue(team);
    }

    public void AddTeamNameNode(String teamName, String teamID){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("employees").child(teamID).setValue(teamName);
    }

    public void setUserStatus(String userID, MainActivity.UserStatus status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("employees").child(userID).child("currentStatus").setValue(status);
    }

    public void setUserRole(String userID, String role){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("employees").child(userID).child("currentRole").setValue(role);
    }

    public void setUserTeam(String userID, String team){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("employees").child(userID).child("currentTeam").setValue(team);
    }

    public void addUserToTeam(final Employee user, final String teamName){
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, teamName);
        dbRef.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot teamSnapshot : dataSnapshot.getChildren()){
                    if(teamSnapshot.getKey().toString().equals((teamName.replaceAll("\\s+","")))){
                        DatabaseReference ref = teamSnapshot.getRef().child("teamMembers");
//                        GenericTypeIndicator<List<Employee>> t = new GenericTypeIndicator<List<Employee>>() {};
//                        List<Employee> memberList = dataSnapshot.child("teamMembers").getValue(t);
//                        Log.d(TAG, memberList.get(0).firstName);
                        MCT team = dataSnapshot.getValue(MCT.class);
                        List<Employee> teamMembers = team.teamMembers;
                        if(user != null){
                            teamMembers.add(user);
                        } else {
                            Log.d(TAG, "User not found.");
                        }
                        //memberList.add(user);
                        //ref.setValue(memberList);
                        Log.d(TAG, "Team Object Retrieved");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void GetEmployeeObject(final String userID){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    if(userSnapshot.getKey().equals(userID)){
                        Employee employee = userSnapshot.getValue(Employee.class);
                        setReturnEmployee(employee);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setReturnEmployee(Employee employee){
         this.employee = employee;
    }

    public Employee getEmployee(){
        return this.employee;
    }

    // TODO: 11/11/17 JD team management is still an issue


}
