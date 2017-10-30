package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.activities.MainActivity;
import com.indexyear.jd.dispatch.models.Employee;
import com.indexyear.jd.dispatch.models.MCT;

import java.util.List;

public class ManageUsers {

    private DatabaseReference mDatabase; //Firebase reference

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
        team.teamMembers = teamMembers;
        team.teamID = teamName;

        mDatabase.child("teams").child(teamName).setValue(team);
    }

    public void AddTeamNameNode(String teamName, String teamID){
        mDatabase.child("teams").child(teamID).setValue(teamName);
    }

    public void SetUserStatus(String userID, MainActivity.UserStatus status){
        mDatabase.child("employees").child(userID).child("currentStatus").setValue(status);
    }
}
