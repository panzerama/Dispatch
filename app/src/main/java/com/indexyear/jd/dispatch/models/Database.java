package com.indexyear.jd.dispatch.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jd on 11/10/17.
 */

public class Database {
    private FirebaseDatabase database;
    private DatabaseReference dataRef;

    public Database(String targetDatabasePath, String targetDatabaseReference){
        setDatabaseConnection(targetDatabasePath + "/" + targetDatabaseReference);
    }

    public void setDatabaseConnection(String databaseConnectionPath){
        database = FirebaseDatabase.getInstance();
        dataRef = database.getReference(databaseConnectionPath);
    }

    public DatabaseReference getDataRef(){
        return dataRef;
    }

    public DatabaseReference getDataRef(String childNode){
        return dataRef.child(childNode);
    }

    public void setDataRef(DatabaseReference newDataRef){
        dataRef = newDataRef;
    }

    public Map returnRecord(String uid){
        return new HashMap();
    }

    public void setValueAt(String nodeReference, String value){
        dataRef.child(nodeReference).setValue(value);
    }
}
