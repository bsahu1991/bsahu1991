package com.example.onlineusertest;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class OnlineUserActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference usersListRef,onlineStatus,connectedRef;
    ValueEventListener userListValueEventListener;
    ListView userListView;
    ArrayList<String> userListItems;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_user);
        userListView =(ListView) findViewById(R.id.user_list);
        userListItems = new ArrayList<String>();
        db = FirebaseDatabase.getInstance();
        usersListRef = db.getReference("users");
        user=FirebaseAuth.getInstance().getCurrentUser();
        addToUserList(user);
        populateUserList();
    }
    private void addToUserList(FirebaseUser user) {
        usersListRef.child(user.getUid()).setValue(new User(user.getDisplayName(),"Online"));
        onlineStatus = db.getReference("users/"+user.getUid()+"/onlineStatus");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    onlineStatus.onDisconnect().setValue("offline");
                    onlineStatus.setValue("Online");
                } else {
                    onlineStatus.setValue("offline");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void populateUserList() {
        userListValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListItems.clear();
//first check datasnap shot exist
//then add all users except current/self user
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.exists() && !ds.getKey().equals(user.getUid())){
                            String name = ds.child("name").getValue(String.class);
                            String onlineStatus = ds.child("onlineStatus").getValue(String.class);
                            userListItems.add(name+" status : "+onlineStatus);
                        }
                    }
                }
                adapter = new ArrayAdapter<String>(OnlineUserActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, userListItems);
                userListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        usersListRef.addValueEventListener(userListValueEventListener);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        usersListRef.removeEventListener(userListValueEventListener);
    }
}